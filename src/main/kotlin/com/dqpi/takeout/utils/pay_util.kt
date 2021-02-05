package com.dqpi.takeout.utils

import com.dqpi.takeout.config.WeChatConfig
import org.apache.commons.codec.digest.DigestUtils
import org.reactivestreams.Publisher
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.ThreadLocalRandom

/**
 * 发起支付所需订单信息
 */
class OrderInfo(
  val orderNo: String,
  val orderName: String,
  val orderAmount: BigDecimal,
  val returnUrlWhenPaid: String,
)

/**
 * 微信支付简易封装
 */
object WechatPayService {
  private const val RANDOM_STR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
  private val regex = "<.*><!\\[CDATA\\[(.*)]]></(.*)>".toRegex()
  private val orderNoRegex = "<out_trade_no><!\\[CDATA\\[(.*)]]></out_trade_no>".toRegex()
  private val notifySuccess = """
    <xml>
      <return_code><![CDATA[SUCCESS]]></return_code>
      <return_msg><![CDATA[OK]]></return_msg>
    </xml>  
  """.trimIndent()

  /**
   * 发起微信支付
   */
  fun pay(weChatConfig: WeChatConfig, orderInfo: OrderInfo): Mono<String> {
    return randomStr().let { randomStr ->
      // 构建发起支付所需请求体
      """
        <xml>
           <appid>${weChatConfig.appId}</appid>
           <mch_id>${weChatConfig.mchId}</mch_id>
           <nonce_str>${randomStr}</nonce_str>
           <sign>${weChatConfig.mchKey.sign(configToTreeMap(weChatConfig, orderInfo, randomStr))}</sign>
           <body>${orderInfo.orderName}</body>
           <notify_url>${weChatConfig.notifyUrl}</notify_url>
           <openid>${weChatConfig.openId}</openid>
           <out_trade_no>${orderInfo.orderNo}</out_trade_no>
           <spbill_create_ip>8.8.8.8</spbill_create_ip>
           <total_fee>${orderInfo.orderAmount.movePointRight(2).toInt()}</total_fee>
           <trade_type>JSAPI</trade_type>
        </xml>
      """.trimIndent()
    }.let { body ->
      // 发起微信支付
      WebClient.create("https://api.mch.weixin.qq.com/pay/unifiedorder")
        .post()
        .contentType(MediaType.APPLICATION_XML)
        .bodyValue(body)
        .retrieve()
        .bodyToMono<String>()
        .flatMap { it.getResValue(weChatConfig.mchKey, orderInfo.returnUrlWhenPaid) }
    }
  }

  /**
   * 通知微信
   * @param doSomething 传入订单编号: orderNo
   */
  fun <T> notify(notifyData: String, doSomething: (String) -> Publisher<T>): Mono<String> {
    return notifyData.split("\n").toFlux()
      .filter { orderNoRegex.matches(it) }
      .flatMap { doSomething(orderNoRegex.matchEntire(it)!!.groupValues[1]) }
      .then(notifySuccess.toMono())
  }

  /**
   * 微信配置转为排序后的map, 便于之后设置sign并加密
   */
  private fun configToTreeMap(weChatConfig: WeChatConfig, orderInfo: OrderInfo, randomStr: String)
  : TreeMap<String, String> {
    val map = TreeMap<String, String>()
    map["appid"] = weChatConfig.appId
    map["mch_id"] = weChatConfig.mchId
    map["nonce_str"] = randomStr
    map["total_fee"] = orderInfo.orderAmount.movePointRight(2).toInt().toString()
    map["out_trade_no"] = orderInfo.orderNo
    map["body"] = orderInfo.orderName
    map["openid"] = weChatConfig.openId
    map["notify_url"] = weChatConfig.notifyUrl
    map["spbill_create_ip"] = "8.8.8.8"
    map["trade_type"] = "JSAPI"
    return map
  }

  /**
   * 生成随机字符串
   */
  private fun randomStr(): String {
    var randomStr = ""
    repeat(16) {
      randomStr += RANDOM_STR[ThreadLocalRandom.current().nextInt(RANDOM_STR.length)]
    }
    return randomStr
  }

  /**
   * 将map字段拼接并加密
   */
  private fun String.sign(map: TreeMap<String, String>): String {
    var toSign = ""
    map.entries.forEach { toSign += "${it.key}=${it.value}&" }
    toSign += "key=${this}"
    return DigestUtils.md5Hex(toSign).toUpperCase()
  }

  /**
   * 拼接调用发起支付使用JSAPI所需参数
   */
  private fun String.getResValue(mchKey: String, returnUrlWhenPaid: String): Mono<String> {
    return split("\n").toFlux()
      .skipLast(1)
      .map { regex.matchEntire(it)!!.groupValues }
      .map { it[2] to it[1] }
      .collectList().map { it.toMap() }.zipWith(TreeMap<String, String>().toMono())
      .doOnNext { it.t2["appId"] = "${it.t1["appid"]}" }
      .doOnNext { it.t2["timeStamp"] = (System.currentTimeMillis() / 1000).toString() }
      .doOnNext { it.t2["package"] = "prepay_id=${it.t1["prepay_id"]}" }
      .doOnNext { it.t2["nonceStr"] = "${it.t1["nonce_str"]}" }
      .doOnNext { it.t2["signType"] = "MD5" }
      .doOnNext { it.t2["paySign"] = mchKey.sign(it.t2) }
      .map { it.t2.toJSAPI(returnUrlWhenPaid) }
  }

  /**
   * JSAPI发起支付脚本
   */
  private fun Map<String, String>.toJSAPI(returnUrlWhenPaid: String): String {
    return """
      <script>
          function onBridgeReady(){
              window.WeixinJSBridge.invoke(
                  'getBrandWCPayRequest', {
                      "appId": "${get("appId")}",
                      "timeStamp": "${get("timeStamp")}",
                      "nonceStr": "${get("nonceStr")}",
                      "package": "${get("package")}",
                      "signType": "${get("signType")}",
                      "paySign": "${get("paySign")}"
                  },
                  function(res){
                      window.location = "$returnUrlWhenPaid"
                  }
              );
          }
          if (typeof WeixinJSBridge == "undefined"){
              if( document.addEventListener ){
                  document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
              }else if (document.attachEvent){
                  document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                  document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
              }
          }else{
              onBridgeReady();
          }
      </script>
    """.trimIndent()
  }
}




