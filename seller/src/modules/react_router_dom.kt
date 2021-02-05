@file:JsModule("react-router-dom")
package index.modules

@JsName("useHistory")
external val useHistory: () -> History

external interface History {
  fun push(url: String)
}


