package com.dqpi.takeout.service.impl

import com.dqpi.takeout.config.WeChatConfig
import com.dqpi.takeout.dto.OrderDTO
import com.dqpi.takeout.service.OrderService
import com.dqpi.takeout.service.PayService
import com.dqpi.takeout.utils.OrderInfo
import com.dqpi.takeout.utils.WechatPayService
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono



@Service
class PayServiceImpl(
  private val weChatConfig: WeChatConfig,
  private val orderService: OrderService
): PayService {

  override fun create(orderDTO: OrderDTO, returnUrlWhenPaid: String): Mono<String> {
    return WechatPayService.pay(weChatConfig, OrderInfo(
      orderNo = orderDTO.orderNo!!,
      orderName = "微信点餐订单",
      orderAmount = orderDTO.amount!!,
      returnUrlWhenPaid = returnUrlWhenPaid
    ))
  }

  override fun notify(notifyData: String): Mono<String> {
    return WechatPayService.notify(notifyData) { orderNo ->
      orderService.findOne(orderNo).flatMap { orderService.paid(it) }
    }
  }
}