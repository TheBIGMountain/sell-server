package com.dqpi.takeout

import com.dqpi.takeout.config.WeChatConfig
import com.dqpi.takeout.utils.OrderInfo
import com.dqpi.takeout.utils.WechatPayService
import com.dqpi.takeout.utils.randomOrderNo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

/**
 * @author TheBIGMountain
 */
@SpringBootTest
class Test {
  @Autowired
  private lateinit var weChatConfig: WeChatConfig

  @Test
  fun test() {
    WechatPayService.pay(weChatConfig, OrderInfo(randomOrderNo(), "支付测试", 0.01.toBigDecimal(), "http://www.baidu.com"))
      .doOnNext { println(it) }
      .block()
  }
}