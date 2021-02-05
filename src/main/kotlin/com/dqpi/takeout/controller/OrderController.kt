package com.dqpi.takeout.controller

import com.dqpi.takeout.form.OrderForm
import com.dqpi.takeout.service.OrderService
import com.dqpi.takeout.utils.toOrderDTO
import com.dqpi.takeout.utils.toResultVo
import com.dqpi.takeout.utils.toServerResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
class OrderController(
  @Value("#{@orderServiceImpl}")
  private val orderService: OrderService
) {
  fun create(req: ServerRequest): Mono<ServerResponse> {
    return req.bodyToMono(OrderForm::class.java)
      .flatMap { orderService.create(it.toOrderDTO()) }
      .toResultVo().toServerResponse()
      .onErrorResume { ServerResponse.badRequest().build() }
  }

  fun list(req: ServerRequest): Mono<ServerResponse> {
    return req.queryParams().toMono()
      .flatMap {
        orderService.findList(
          it.getFirst("openId")!!,
          (it.getFirst("pageNum") ?: "1").toInt(),
          (it.getFirst("pageSize") ?: "10").toInt()
        ) }
      .toResultVo().toServerResponse()
  }

  fun detail(req: ServerRequest): Mono<ServerResponse> {
    return req.queryParams().toMono()
      .flatMap { orderService.findOne(it.getFirst("orderNo")!!) }
      .doOnNext { if (it.buyerOpenId != req.queryParam("openId").orElse("")) throw RuntimeException() }
      .toResultVo().toServerResponse()
  }

  fun cancel(req: ServerRequest): Mono<ServerResponse> {
    return req.bodyToMono(Map::class.java)
      .flatMap { orderService.findOne(it["orderNo"].toString()).zipWith(it["openId"]!!.toMono()) }
      .doOnNext { if (it.t1.buyerOpenId != it.t2) throw RuntimeException() }
      .flatMap { orderService.cancel(it.t1) }
      .toResultVo().toServerResponse()
  }
}