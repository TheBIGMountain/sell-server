package index

import antd.message.message
import antd.notification.notification
import index.ajax.api.login
import index.ajax.index
import index.modules.websocket
import index.router.router
import kotlinext.js.jsObject
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.html.id
import modules.cookie
import react.*
import react.dom.audio
import react.dom.source

private const val loginUrl = "https://open.weixin.qq.com/connect/qrconnect?appid=wx6ad144e54af67d87&redirect_uri=http%3A%2F%2Fsell.springboot.cn%2Fsell%2Fqr%2FoTgZpwb54GGy0m3iQCu4RhCrCqWI&response_type=code&scope=snsapi_login&state=http%3A%2F%2Fmountain.cn1.utools.club%2Fwechat%2FqrUserInfo"

private val app = functionalComponent<RProps> {
  val (show, isShow) = useState(false)

  useEffect {
    if (cookie.load("token") == null) {
      window.location.hash.substring(2).let { openid ->
        if (openid == "")
          window.location.href = loginUrl
        else {
          MainScope().launch {
            openid.login()
              .onEach { if (it == "success") { message.success("登录成功~"); isShow(true) } }
              .onEach { if (it == "error") { message.error("登录失败"); window.location.href = loginUrl } }
              .collect()
          }
        }
      }
    } else isShow(true)
  }

  if (show) {
    audio { attrs.id = "audio"
      source { attrs { src = "/song.mp3"; type = "audio/mpeg" } }
    }
    websocket {
      attrs {
        url = "ws://${index}/websocket"
        onMessage = {
          notification.info(jsObject {
            message = "订单消息"
            description = "您有新的订单"
          })
          document.getElementById("audio").asDynamic().play().unsafeCast<Unit>()
        }
      }
    }
    router()
  }
}

fun RBuilder.app() = child(app)