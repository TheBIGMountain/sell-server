package com.dqpi.takeout.repository

import com.dqpi.takeout.entity.*
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


interface CategoryRepository: ReactiveCrudRepository<Category, Int>

interface ProductInfoRepository: ReactiveCrudRepository<ProductInfo, Int>

interface RatingRepository: ReactiveCrudRepository<Rating, Int> {
  fun findAllByProductIdIs(productId: Int): Flux<Rating>
}

interface SellerInfoRepository: ReactiveCrudRepository<Seller, Int> {
  fun findByOpenIdIs(openId: String): Mono<Seller>
}

interface OrderRepository: ReactiveCrudRepository<Order, Int> {
  fun findAllByBuyerOpenIdIs(openId: String): Flux<Order>
  fun findByOrderNoIs(orderNo: String): Mono<Order>
}

interface OrderDetailRepository: ReactiveCrudRepository<OrderDetail, Int> {
  fun findAllByOrderNoIs(orderNo: String): Flux<OrderDetail>
}
