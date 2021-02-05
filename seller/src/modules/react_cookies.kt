@file:JsModule("react-cookies")
package modules

@JsName("default")
external val cookie: Cookie

external interface Cookie {
  fun save(key: String, value: String, option: CookieOption = definedExternally)
  fun load(key: String): String?
  fun remove(key: String)
}

external interface CookieOption {
  var maxAge: Int
}


