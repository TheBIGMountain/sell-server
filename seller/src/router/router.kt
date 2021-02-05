package index.router

import antd.notification.notification
import index.components.category
import index.components.orderContent
import index.components.product
import index.modules.websocket
import index.page.home
import kotlinext.js.jsObject
import kotlinx.browser.document



import kotlinx.html.classes
import kotlinx.html.id
import kotlinx.html.source
import modules.cookie

import react.*
import react.dom.audio
import react.dom.div
import react.dom.source
import react.router.dom.browserRouter

import react.router.dom.hashRouter
import react.router.dom.route
import react.router.dom.switch
import kotlin.time.Duration

private val router = functionalComponent<RProps> {
  hashRouter {
    route("/", render = {
      home {
        switch {
          route("/order", render = { orderContent() })
          route("/product", render = { product() })
          route("/category", render = { category() })
          route("/", render = {
            div { attrs.classes = setOf("index"); +"欢迎来到卖家管理系统" }
          })
        }
      }
    })
  }
}

fun RBuilder.router() = child(router)