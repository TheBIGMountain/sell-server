package com.dqpi.takeout.service

import com.dqpi.takeout.dto.CartDTO
import com.dqpi.takeout.dto.OrderDTO
import com.dqpi.takeout.entity.Category
import com.dqpi.takeout.entity.ProductInfo
import com.dqpi.takeout.entity.Seller
import com.dqpi.takeout.vo.PageInfo
import com.dqpi.takeout.vo.ProductVo
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface PayService {
  fun create(orderDTO: OrderDTO, returnUrlWhenPaid: String): Mono<String>
  fun notify(notifyData: String): Mono<String>
}

interface PushMessageService {
  fun orderStatus(orderDTO: OrderDTO): Mono<Unit>
}

interface SellerService {
  fun findByOpenId(openId: String): Mono<Seller>
}

interface OrderService {
  fun create(orderDTO: OrderDTO): Mono<Map<String, String>>
  fun findOne(orderNo: String): Mono<OrderDTO>
  fun findList(openId: String, pageNum: Int, pageSize: Int): Mono<PageInfo<OrderDTO>>
  fun cancel(orderDTO: OrderDTO): Mono<Unit>
  fun finish(orderDTO: OrderDTO): Mono<Unit>
  fun paid(orderDTO: OrderDTO): Mono<Unit>
  fun findList(pageNum: Int, pageSize: Int): Mono<PageInfo<OrderDTO>>
}

interface CategoryService {
  fun findOne(categoryId: Int): Mono<Category>
  fun findAll(): Flux<Category>
  fun save(category: Category): Mono<Category>
}

interface ProductInfoService {
  fun findOne(productId: Int): Mono<ProductInfo>
  fun findAllOnSell(): Flux<ProductVo>
  fun findAll(pageNum: Int, pageSize: Int): Mono<PageInfo<ProductInfo>>
  fun save(productInfo: ProductInfo): Mono<ProductInfo>
  fun increaseStock(cartDTO: CartDTO): Mono<Unit>
  fun decreaseStock(cartDTO: CartDTO): Mono<Unit>
  fun saleOnUp(productId: Int): Mono<ProductInfo>
  fun saleOnDown(productId: Int): Mono<ProductInfo>
}