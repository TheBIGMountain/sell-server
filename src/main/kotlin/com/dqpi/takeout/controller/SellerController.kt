package com.dqpi.takeout.controller

import com.dqpi.takeout.entity.ProductInfo
import com.dqpi.takeout.service.CategoryService
import com.dqpi.takeout.service.OrderService
import com.dqpi.takeout.service.ProductInfoService
import com.dqpi.takeout.service.SellerService
import com.dqpi.takeout.utils.toResultVo
import com.dqpi.takeout.utils.toServerResponse
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration
import java.util.*


@Controller
class SellerController(
  private val orderService: OrderService,
  private val categoryService: CategoryService,
  private val productInfoService: ProductInfoService,
  private val sellerService: SellerService,
  private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>
) {

  fun login(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("openid"))
      // 查询是否存在该用户
      .flatMap { sellerService.findByOpenId(it) }
      // 生成 redis Key
      .zipWith("token_${UUID.randomUUID()}".toMono())
      // 存入 redis
      .doOnNext { reactiveRedisTemplate.opsForValue().set(it.t2, it.t1.openId, Duration.ofSeconds(7200)).subscribe() }
      // 生成 cookie
      .map { ResponseCookie.from("token", it.t2).path("/").maxAge(7200).build() }
      // 响应结果处理
      .over()
  }

  fun logout(req: ServerRequest): Mono<ServerResponse> {
    return req.cookies().getFirst("token")!!.toMono()
      // 移除 redis
      .flatMap { reactiveRedisTemplate.opsForValue().delete("token_${it.value}") }
      // 移除 cookie
      .map { ResponseCookie.from("token", "").path("/").maxAge(0).build() }
      // 响应结果处理
      .over()
  }

  fun orderList(req: ServerRequest): Mono<ServerResponse> {
    return req.queryParams().toMono()
      .flatMap { (it.getFirst("pageNum") ?: "1").toInt().toMono().zipWith((it.getFirst("pageSize") ?: "10").toInt().toMono()) }
      .flatMap { orderService.findList(it.t1, it.t2) }
      .toResultVo().toServerResponse()
  }

  fun orderCancel(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("orderId"))
      .flatMap { orderService.findOne(it) }
      .flatMap { orderService.cancel(it) }
      .toResultVo().toServerResponse()
  }

  fun orderFinish(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("orderId"))
      .flatMap { orderService.findOne(it) }
      .flatMap { orderService.finish(it) }
      .toResultVo().toServerResponse()
  }

  fun orderDetail(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("orderId"))
      .flatMap { orderService.findOne(it) }
      .toResultVo().toServerResponse()
  }

  fun productList(req: ServerRequest): Mono<ServerResponse> {
    return req.queryParams().toMono()
      .flatMap { (it.getFirst("pageNum") ?: "1").toInt().toMono().zipWith((it.getFirst("pageSize") ?: "10").toInt().toMono()) }
      .flatMap { productInfoService.findAll(it.t1, it.t2) }
      .toResultVo().toServerResponse()
  }

  fun productInfo(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("productId"))
      .flatMap { productInfoService.findOne(it.toInt()) }
      .toResultVo().toServerResponse()
  }

  fun productSave(req: ServerRequest): Mono<ServerResponse> {
    return req.bodyToMono<ProductInfo>()
      .flatMap { productInfoService.save(it) }
      .toResultVo().toServerResponse()
  }

  fun productOnSell(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("productId"))
      .flatMap { productInfoService.saleOnUp(it.toInt()) }
      .toResultVo().toServerResponse()
  }

  fun productNotSell(req: ServerRequest): Mono<ServerResponse> {
    return Mono.justOrEmpty(req.queryParam("productId"))
      .flatMap { productInfoService.saleOnDown(it.toInt()) }
      .toResultVo().toServerResponse()
  }

  fun categoryList(req: ServerRequest): Mono<ServerResponse> {
    return categoryService.findAll().collectList()
      .toResultVo().toServerResponse()
  }

  private fun Mono<ResponseCookie>.over(): Mono<ServerResponse> {
    return zipWith("success".toMono().toResultVo())
      .flatMap { ServerResponse.ok().cookie(it.t1).bodyValue(it.t2) }
      .switchIfEmpty("error".toMono().toResultVo().toServerResponse())
  }
}