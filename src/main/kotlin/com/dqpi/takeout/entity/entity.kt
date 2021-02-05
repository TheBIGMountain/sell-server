package com.dqpi.takeout.entity

import com.dqpi.takeout.enums.OrderStatus
import com.dqpi.takeout.enums.PayStatus
import com.dqpi.takeout.utils.LocalDateTimeToTimeStamp
import com.dqpi.takeout.utils.TimeStampToLocalDateTime
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("seller_info")
data class Seller(
  @Id
  val id: Int? = null,
  val username: String,
  val password: String,
  val openId: String,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var createTime: LocalDateTime? = null,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var updateTime: LocalDateTime? = null
)

@Table("order_master")
data class Order(
  @Id
  @Column("order_id")
  val id: Int? = null,
  val orderNo: String,
  val buyerName: String,
  val buyerPhone: String,
  val buyerAddress: String,
  val buyerOpenId: String,
  @Column("order_amount")
  val amount: BigDecimal,
  @Column("order_status")
  val status: Int = OrderStatus.NEW.code,
  val payStatus: Int = PayStatus.WAIT.code,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var createTime: LocalDateTime? = null,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var updateTime: LocalDateTime? = null
)

@Table
data class Rating(
  @Id
  val id: Int? = null,
  val productId: Int,
  val username: String,
  val rateTime: String,
  val rateType: Int,
  val text: String,
  val avatar: String
)

@Table
data class OrderDetail(
  @Id
  val id: Int? = null,
  var orderNo: String? = null,
  val productId: Int,
  val productQuantity: Int,
  val productName: String,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var createTime: LocalDateTime? = null,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var updateTime: LocalDateTime? = null
)

@Table("product_category")
data class Category(
  @Id
  val categoryId: Int? = null,
  val categoryName: String,
  val categoryType: Int,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var createTime: LocalDateTime? = null,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  var updateTime: LocalDateTime? = null,
)

@Table
data class ProductInfo(
  @Id
  val id: Int? = null,
  val name: String,
  val price: BigDecimal,
  val stock: Int,
  val description: String,
  val icon: String,
  val status: Int,
  val categoryType: Int,
  val sellCount: Int,
  val rating: Int,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  @JsonDeserialize(using = TimeStampToLocalDateTime::class)
  var createTime: LocalDateTime? = null,
  @JsonSerialize(using = LocalDateTimeToTimeStamp::class)
  @JsonDeserialize(using = TimeStampToLocalDateTime::class)
  var updateTime: LocalDateTime? = null
)
