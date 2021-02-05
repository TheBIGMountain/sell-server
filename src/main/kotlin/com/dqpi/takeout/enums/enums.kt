package com.dqpi.takeout.enums


enum class ProductStatus(
  val code: Int,
  val msg: String
) {
  ON_SELL(0, "在售"),
  NOT_SELL(1, "已下架")
}

enum class ResultEnum(
  val code: Int,
  val msg: String
) {
  PRODUCT_STOCK_ERROR(0, "商品库存不正确"),
  ORDER_STATUS_ERROR(1, "订单状态异常, 取消失败")
}

enum class PayStatus(
  val code: Int,
  val msg: String
) {
  WAIT(0, "未支付"),
  SUCCESS(1, "支付成功")
}

enum class OrderStatus(
  val code: Int,
  val msg: String
) {
  NEW(0, "新订单"),
  FINISHED(1, "完结"),
  CANCEL(2, "已取消");
}