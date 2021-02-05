package index.ajax.api

import index.ajax.Method
import index.ajax.ajax
import kotlinx.coroutines.flow.Flow

suspend fun String.login(): Flow<dynamic> {
  return Method.GET.ajax("/seller/login?openid=$this")
}

suspend fun logout(): Flow<dynamic> {
  return Method.GET.ajax("/seller/logout")
}