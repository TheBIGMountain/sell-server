package com.dqpi.takeout.utils

import com.dqpi.takeout.dto.OrderDTO
import com.dqpi.takeout.entity.Order
import com.dqpi.takeout.entity.OrderDetail
import com.dqpi.takeout.entity.ProductInfo
import com.dqpi.takeout.entity.Rating
import com.dqpi.takeout.form.OrderForm
import com.dqpi.takeout.vo.ProductVo

fun OrderDTO.toOrder() = Order(
  id = id,
  orderNo = orderNo ?: "",
  buyerName = buyerName!!,
  buyerAddress = buyerAddress!!,
  buyerOpenId = buyerOpenId,
  buyerPhone = buyerPhone!!,
  status = status,
  payStatus = payStatus,
  createTime = createTime,
  amount = amount ?: 0.0.toBigDecimal()
)

fun Order.toOrderDTO(orderDetails: List<OrderDetail>) = OrderDTO(
  id = id,
  orderNo = orderNo,
  buyerName = buyerName,
  buyerAddress = buyerAddress,
  buyerOpenId = buyerOpenId,
  buyerPhone = buyerPhone,
  status = status,
  payStatus = payStatus,
  amount = amount,
  createTime = createTime,
  orderDetails = orderDetails
)

fun OrderForm.toOrderDTO() = OrderDTO(
  buyerName = name,
  buyerPhone = phone,
  buyerOpenId = openId,
  buyerAddress = address,
  orderDetails = cartDTO.products.map { OrderDetail(productId = it.productId, productQuantity = it.quantity, productName = it.productName) }
)

fun ProductInfo.toProductVo(ratings: List<Rating>) = ProductVo(
  id = id!!,
  name = name,
  price = price,
  description = description,
  icon = icon,
  sellCount = sellCount,
  rating = rating,
  categoryType = categoryType,
  ratings = ratings
)
