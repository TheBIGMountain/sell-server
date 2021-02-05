package index.ajax.api

import index.ajax.Method
import index.ajax.ajax
import kotlinx.coroutines.flow.Flow

suspend fun categoryList(): Flow<Array<dynamic>> {
  return Method.GET.ajax("/seller/category/list")
}