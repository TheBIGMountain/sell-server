package index.ajax.api

import index.ajax.Method
import index.ajax.PageInfo
import index.ajax.ajax
import kotlinx.coroutines.flow.Flow

interface Order {
  var orderNo: String
  var buyerName: String
  var buyerPhone: String
  var buyerAddress: String
  var buyerOpenId: String
  var amount: Double
  var status: Int
  var payStatus: Int
  var orderDetails: Array<OrderDetail>
}

interface OrderDetail {
  var id: Int
  var orderNo: String
  var productId: Int
  var productQuantity: Int
  var createTime: String
  var updateTime: String
}

suspend fun orderList(pageNum: Int): Flow<PageInfo<Order>> {
  return Method.GET.ajax("/seller/order/list?pageNum=$pageNum")
}

suspend fun String.cancel(): Flow<Unit> {
  return Method.PUT.ajax("/seller/order/cancel?orderId=$this")
}

suspend fun String.finish(): Flow<Unit> {
  return Method.PUT.ajax("/seller/order/finish?orderId=$this")
}

suspend fun String.detail(): Flow<Order> {
  return Method.GET.ajax("/seller/order/detail?orderId=$this")
}