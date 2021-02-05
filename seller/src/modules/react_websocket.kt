package index.modules

import react.RClass
import react.RProps

@JsModule("react-websocket")
@JsNonModule
external val websocket: RClass<WebsocketProps>

external interface WebsocketProps: RProps {
  var url: String
  var onMessage: (dynamic) -> Unit
}