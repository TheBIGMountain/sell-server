package index.components


import antd.menu.menu
import antd.menu.subMenu
import antd.message.message
import antd.modal.ModalComponent
import index.ajax.api.logout
import index.modules.useHistory
import kotlinext.js.jsObject
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import react.*
import react.dom.span
import kotlin.js.Promise


private fun logoutConfirm() = ModalComponent.confirm(jsObject {
  title = "登出确认"
  content = "您确认要登出吗?"
  onOk = {
    MainScope().launch {
      logout().onEach { delay(1000) }
        .onEach { message.success("登出成功") }
        .onEach { ModalComponent.destroyAll(); delay(500) }
        .onEach { window.location.href = "/" }
        .collect()
    }
    Promise<dynamic> { _, _ -> }
  }
})

private val navLeft = functionalComponent<RProps> {
  val history = useHistory()

  menu { attrs { theme = "dark"; className = "menu" }
    subMenu {
      attrs {
        className = "title"
        key = "sub0"
        title = buildElement { span { +"卖家管理系统" } }
        onTitleClick = { history.push("/") }
      }
    }
    subMenu {
      attrs {
        className = "sub-menu"
        key = "sub1"
        title = buildElement { span { +"订单" } }
        onTitleClick = { history.push("/order") }
      }
    }
    subMenu {
      attrs {
        className = "sub-menu"
        key = "sub2"
        title = buildElement { span { +"商品" } }
        onTitleClick = { history.push("/product") }

      }
    }
    subMenu {
      attrs {
        className = "sub-menu"
        key = "sub3"
        title = buildElement { span { +"类目" } }
        onTitleClick = { history.push("/category") }
      }
    }
    subMenu {
      attrs {
        className = "sub-menu logout"
        key = "sub4"
        title = buildElement { span { +"登出" } }
        onTitleClick = { logoutConfirm() }
      }
    }
  }
}

fun RBuilder.navLeft() = child(navLeft)