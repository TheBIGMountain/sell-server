package index.ajax.api

import index.ajax.Method
import index.ajax.PageInfo
import index.ajax.ajax
import kotlinx.coroutines.flow.Flow
import kotlin.js.Date
import kotlin.js.json

interface Product {
  var id: Int
  var name: String
  var price: Double
  var stock: Int
  var description: String
  var icon: String
  var status: Int
  var categoryType: Int
  var createTime: Long
  var updateTime: Long
}

suspend fun productList(pageNum: Int): Flow<PageInfo<Product>> {
  return Method.GET.ajax("/seller/product/list?pageNum=$pageNum")
}

suspend fun Int.productInfo(): Flow<Product> {
  return Method.GET.ajax("/seller/product/detail?productId=$this")
}

suspend fun Product.save(): Flow<Unit> {
  return Method.POST.ajax("/seller/product/save", json(
    "id" to id,
    "name" to name,
    "categoryType" to categoryType,
    "price" to price,
    "stock" to stock,
    "description" to description,
    "icon" to icon,
    "status" to status,
    "createTime" to createTime,
    "updateTime" to updateTime
  ))
}

suspend fun Int.productOnSell(): Flow<Unit> {
  return Method.PUT.ajax("/seller/product/onSell?productId=$this")
}

suspend fun Int.productNotSell(): Flow<Unit> {
  return Method.PUT.ajax("/seller/product/notSell?productId=$this")
}