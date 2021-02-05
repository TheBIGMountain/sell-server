package com.dqpi.takeout.service.impl

import com.dqpi.takeout.config.WeChatConfig
import com.dqpi.takeout.dto.OrderDTO
import com.dqpi.takeout.enums.OrderStatus
import com.dqpi.takeout.service.PushMessageService
import me.chanjar.weixin.mp.api.WxMpService
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono



@Service
class PushMessageServiceImpl(
  private val wxMpService: WxMpService,
  private val weChatConfig: WeChatConfig
): PushMessageService {
  override fun orderStatus(orderDTO: OrderDTO): Mono<Unit> {
    return wxMpService.templateMsgService.sendTemplateMsg(WxMpTemplateMessage().also {
      it.templateId = weChatConfig.templateId
      it.toUser = orderDTO.buyerOpenId
      it.data = listOf(
        WxMpTemplateData("TITLE", "订单详情"),
        WxMpTemplateData("SELLER_NAME", orderDTO.buyerName),
        WxMpTemplateData("SELLER_PHONE", orderDTO.buyerPhone),
        WxMpTemplateData("ORDER_NO", orderDTO.orderNo),
        WxMpTemplateData("STATUS", OrderStatus.values()[orderDTO.status].msg),
        WxMpTemplateData("PRICE", "￥${orderDTO.amount.toString()}"),
        WxMpTemplateData("TAIL", "欢迎再次光临"),
      )
    }).toMono().then(Mono.just(Unit))
  }
}
