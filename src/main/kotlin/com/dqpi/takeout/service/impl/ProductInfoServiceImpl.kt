package com.dqpi.takeout.service.impl

import com.dqpi.takeout.dto.CartDTO
import com.dqpi.takeout.entity.ProductInfo
import com.dqpi.takeout.enums.ProductStatus
import com.dqpi.takeout.enums.ProductStatus.NOT_SELL
import com.dqpi.takeout.enums.ProductStatus.ON_SELL
import com.dqpi.takeout.enums.ResultEnum.PRODUCT_STOCK_ERROR
import com.dqpi.takeout.exception.BusinessException
import com.dqpi.takeout.repository.ProductInfoRepository
import com.dqpi.takeout.repository.RatingRepository
import com.dqpi.takeout.service.ProductInfoService
import com.dqpi.takeout.utils.toPageInfo
import com.dqpi.takeout.utils.toProductVo
import com.dqpi.takeout.utils.transition
import com.dqpi.takeout.vo.PageInfo
import com.dqpi.takeout.vo.ProductVo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono


@Service
class ProductInfoServiceImpl(
  @Value("#{@productInfoRepository}")
  private val productInfoRepository: ProductInfoRepository,
  @Value("#{@transitionOperator}")
  private val transactionalOperator: TransactionalOperator,
  private val ratingRepository: RatingRepository
) : ProductInfoService {
  override fun findOne(productId: Int): Mono<ProductInfo> {
    return productInfoRepository.findById(productId)
  }

  override fun findAllOnSell(): Flux<ProductVo> {
    return productInfoRepository.findAll().filter { it.status == ON_SELL.code }
      .flatMap { it.toMono().zipWith(ratingRepository.findAllByProductIdIs(it.id!!).collectList()) }
      .map { it.t1.toProductVo(it.t2) }
  }

  override fun save(productInfo: ProductInfo): Mono<ProductInfo> {
    return productInfoRepository.save(productInfo)
  }

  override fun findAll(pageNum: Int, pageSize: Int): Mono<PageInfo<ProductInfo>> {
    return productInfoRepository.findAll().index()
      .publishOn(Schedulers.parallel())
      // t1 -> index
      .filter { it.t1 >= (pageNum - 1) * pageSize.toLong() }
      .take(pageSize.toLong())
      // t2 -> productInfo
      .map { it.t2 }.collectList()
      .zipWith(productInfoRepository.count().publishOn(Schedulers.parallel()))
      // t1 -> List<ProductInfo>, t2 -> 商品记录总数量
      .map { it.t1.toPageInfo(pageNum, pageSize, it.t2.toInt()) }
  }

  override fun increaseStock(cartDTO: CartDTO): Mono<Unit> {
    return cartDTO.products.toFlux()
      // 查询数据库中商品
      .flatMap { productInfoRepository.findById(it.productId).zipWith(it.toMono()) }
      // 添加库存, 存入数据库
      .flatMap { productInfoRepository.save(it.t1.copy(stock = it.t1.stock + it.t2.quantity)) }
      // 添加事务
      .then().then(Mono.just(Unit)).transition(transactionalOperator)
  }

  override fun decreaseStock(cartDTO: CartDTO): Mono<Unit> {
    return cartDTO.products.toFlux()
      // 查询数据库中商品
      .flatMap { productInfoRepository.findById(it.productId).zipWith(it.toMono()) }
      // 查看库存是否充足
      .flatMap { it.t1.toMono().zipWith((it.t1.stock - it.t2.quantity).toMono()) }
      // 库存不足则抛出
      .doOnNext { if (it.t2 < 0) throw BusinessException(PRODUCT_STOCK_ERROR) }
      // 存入数据库
      .flatMap { productInfoRepository.save(it.t1.copy(stock = it.t2)) }
      // 等待完成
      .then().then(Mono.just(Unit))
      // 添加事务
      .transition(transactionalOperator)
  }

  override fun saleOnUp(productId: Int): Mono<ProductInfo> = productId.updateStatus(ON_SELL)

  override fun saleOnDown(productId: Int): Mono<ProductInfo> = productId.updateStatus(NOT_SELL)

  private fun Int.updateStatus(productStatus: ProductStatus): Mono<ProductInfo> {
    return productInfoRepository.findById(this)
      .filter { it.status != productStatus.code }
      .flatMap { productInfoRepository.save(it.copy(status = productStatus.code)) }
  }
}

