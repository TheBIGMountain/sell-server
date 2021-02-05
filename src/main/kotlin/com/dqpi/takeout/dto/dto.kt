package com.dqpi.takeout.dto

import com.dqpi.takeout.entity.OrderDetail
import com.dqpi.takeout.enums.OrderStatus
import com.dqpi.takeout.enums.PayStatus
import com.dqpi.takeout.utils.LocalDateTimeToTimeStamp
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.math.BigDecimal
import java.time.LocalDateTime

data class CartDTO(
  val products: MutableList<ProductInCart>
)

class ProductInCart(
  val productId: Int,
  val productName: String,
  val quantity: Int
)

data class OrderDTO(
  @Id
  @Column("order_id")
  var id: Int? = null,
  var orderNo: String? = null,
  val buyerName: String? = null,
  val buyerPhone: String? = null,
  val buyerAddress: String? = null,
  val buyerOpenId: String,
  var amount: BigDecimal? = null,
  @Column("order_status")
  val status: Int = OrderStatus.NEW.code,
  val payStatus: Int = PayStatus.WAIT.code,
  val orderDetails: List<OrderDetail>? = null,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  val createTime: LocalDateTime? = null
)