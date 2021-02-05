package index.ajax

import kotlinext.js.jsObject
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.css.body
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestRedirect
import kotlin.js.Json
import kotlin.js.json

const val index = "localhost:80"

interface ResultVo<T> {
  var code: Int
  var msg: String
  var data: T
}

interface PageInfo<T> {
  var pageNum: Int
  var pageSize: Int
  var totalSize: Int
  var content: Array<T>
}

enum class Method(val type: String) {
  GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE")
}

suspend fun <T> Method.ajax(url: String, json: Json? = null): Flow<T> {
  return window.fetch("http://${index}$url", jsObject {
    method = type
    headers = json("Content-Type" to "application/json")
    credentials = RequestCredentials.INCLUDE
    if (json != null) body = JSON.stringify(json)
  }).await().json().await().unsafeCast<ResultVo<T>>().let { flowOf(it.data) }
}

