package com.dqpi.takeout.controller

import com.dqpi.takeout.service.OrderService
import com.dqpi.takeout.service.PayService
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Controller
class PayController(
  private val orderService: OrderService,
  private val payService: PayService
) {

  fun create(req: ServerRequest): Mono<ServerResponse> {
    return req.queryParams().toMono()
      .map { it.getFirst("orderId")!! }
      .flatMap { orderService.findOne(it) }
      .flatMap { payService.create(it, req.queryParam("returnUrl").get()) }
      .flatMap { ServerResponse.ok().bodyValue(it) }
  }

  fun notify(req: ServerRequest): Mono<ServerResponse> {
    return req.bodyToMono<String>()
      .flatMap { payService.notify(it) }
      .flatMap { ServerResponse.ok().bodyValue(it) }
  }
}