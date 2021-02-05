package com.dqpi.takeout.service.impl

import com.dqpi.takeout.dto.CartDTO
import com.dqpi.takeout.dto.OrderDTO
import com.dqpi.takeout.dto.ProductInCart
import com.dqpi.takeout.entity.Order
import com.dqpi.takeout.enums.OrderStatus
import com.dqpi.takeout.enums.PayStatus
import com.dqpi.takeout.enums.ResultEnum
import com.dqpi.takeout.exception.BusinessException
import com.dqpi.takeout.repository.OrderDetailRepository
import com.dqpi.takeout.repository.OrderRepository
import com.dqpi.takeout.repository.ProductInfoRepository
import com.dqpi.takeout.service.OrderService
import com.dqpi.takeout.service.ProductInfoService
import com.dqpi.takeout.service.PushMessageService
import com.dqpi.takeout.utils.*
import com.dqpi.takeout.vo.PageInfo
import com.dqpi.takeout.websocket.Websocket
import org.springframework.stereotype.Service
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicReference

@Service
class OrderServiceImpl(
  private val productInfoRepository: ProductInfoRepository,
  private val orderDetailRepository: OrderDetailRepository,
  private val orderRepository: OrderRepository,
  private val productInfoService: ProductInfoService,
  private val transitionOperator: TransactionalOperator,
  private val pushMessageService: PushMessageService
) : OrderService {

  override fun create(orderDTO: OrderDTO): Mono<Map<String, String>> {
    // 商品总价
    val totalPrice = AtomicReference(0.0.toBigDecimal())
    // 购物车商品, 扣库存使用
    val cart = CartDTO(CopyOnWriteArrayList())
    // 获取订单编号
    val orderNo = randomOrderNo()
    // 处理订单数据
    return orderDTO.orderDetails!!.toFlux()
      // 并行处理
      .parallel()
      // 切换并行线程池
      .runOn(Schedulers.parallel())
      // 查询数据库中商品, 不存在则抛出异常
      .flatMap { productInfoRepository.findById(it.productId).zipWith(Mono.just(it)).switchIfEmpty { Mono.error(RuntimeException()) } }
      // 设置订单id
      .doOnNext { it.t2.orderNo = orderNo }
      // 计算购物车相应商品数量
      .doOnNext { cart.products.add(ProductInCart(it.t1.id!!, it.t2.productName, it.t2.productQuantity)) }
      // 计算订单总价
      .doOnNext { totalPrice.getAndUpdate { price -> price + it.t1.price * it.t2.productQuantity.toBigDecimal() } }
      // 数据库存入订单详情
      .flatMap { orderDetailRepository.save(it.t2) }
      // 等待并行完成
      .then().then(Unit.toMono())
      // 保存订单金额重新存入数据库
      .flatMap { orderRepository.save(orderDTO.copy(orderNo = orderNo, amount = totalPrice.get()).toOrder()) }
      // 获取购物车商品数据扣库存
      .flatMap { productInfoService.decreaseStock(cart) }
      // 通知卖家有新的订单
      .doOnNext { Websocket.sendMessage() }
      // 返回订单编号
      .map { mapOf("orderNo" to orderNo) }
      // 添加事务
      .transition(transitionOperator)
  }

  override fun findOne(orderNo: String): Mono<OrderDTO> {
    return orderRepository.findByOrderNoIs(orderNo)
      .zipWith(orderDetailRepository.findAllByOrderNoIs(orderNo).collectList())
      // t1 -> order, t2 -> List<OrderDetail>
      .map { it.t1.toOrderDTO(it.t2) }
  }

  override fun findList(openId: String, pageNum: Int, pageSize: Int): Mono<PageInfo<OrderDTO>> {
    return orderRepository.findAllByBuyerOpenIdIs(openId).index()
      // t1 -> index
      .filter { it.t1 >= (pageNum - 1) * pageSize }
      .take(pageSize.toLong())
      // t2 -> Order
      .flatMap { orderDetailRepository.findAllByOrderNoIs(it.t2.orderNo).collectList().zipWith(it.t2.toMono()) }
      // t1 -> List<OrderDetail>, t2 -> Order
      .map { it.t2.toOrderDTO(it.t1) }.collectList().zipWith(orderRepository.count())
      // t1 -> List<OrderDto>, t2 -> totalCount
      .map { it.t1.toPageInfo(pageNum, pageSize, it.t2.toInt()) }
  }

  override fun findList(pageNum: Int, pageSize: Int): Mono<PageInfo<OrderDTO>> {
    return orderRepository.findAll().index()
      .filter { it.t1 >= (pageNum - 1) * pageSize }
      .take(pageSize.toLong())
      // t2 -> Order
      .flatMap { orderDetailRepository.findAllByOrderNoIs(it.t2.orderNo).collectList().zipWith(it.t2.toMono()) }
      // t1 -> List<OrderDetail>, t2 -> Order
      .map { it.t2.toOrderDTO(it.t1) }.collectList().zipWith(orderRepository.count())
      // t1 -> List<OrderDto>, t2 -> totalCount
      .map { it.t1.toPageInfo(pageNum, pageSize, it.t2.toInt()) }
  }

  override fun cancel(orderDTO: OrderDTO): Mono<Unit> {
    return orderDTO.toMono().updateStatus(OrderStatus.CANCEL)
      // 计算商品数量
      .then(CartDTO(orderDTO.orderDetails!!.map { ProductInCart(it.productId, it.productName, it.productQuantity) }.toMutableList()).toMono())
      // 增加库存
      .doOnNext { productInfoService.increaseStock(it).subscribe() }
      // 等待结束
      .then().then(Mono.just(Unit))
      // 添加事务
      .transition(transitionOperator)
  }

  override fun finish(orderDTO: OrderDTO): Mono<Unit> {
    return orderDTO.toMono().updateStatus(OrderStatus.FINISHED)
      // 推送微信模板信息
      .flatMap { pushMessageService.orderStatus(it.toOrderDTO(emptyList())) }
      // 等待完成
      .then(Mono.just(Unit))
  }

  override fun paid(orderDTO: OrderDTO): Mono<Unit> {
    return orderDTO.toMono()
      // 订单状态判断
      .doOnNext { if (orderDTO.status != OrderStatus.NEW.code) throw BusinessException(ResultEnum.ORDER_STATUS_ERROR) }
      // 支付状态判断
      .doOnNext { if (orderDTO.payStatus != PayStatus.WAIT.code) throw BusinessException(ResultEnum.ORDER_STATUS_ERROR) }
      // 修改订单状态
      .map { it.toOrder().copy(payStatus = PayStatus.SUCCESS.code) }
      // 存入数据库
      .flatMap { orderRepository.save(it) }
      // 等待完成
      .then().then(Mono.just(Unit))
  }


  private fun Mono<OrderDTO>.updateStatus(status: OrderStatus): Mono<Order> {
    // 是否为新订单
    return doOnNext { if (it.status != OrderStatus.NEW.code) throw BusinessException(ResultEnum.ORDER_STATUS_ERROR) }
      // 修改状态
      .map { it.toOrder().copy(status = status.code) }
      // 存入数据库
      .flatMap { orderRepository.save(it) }
  }
}

