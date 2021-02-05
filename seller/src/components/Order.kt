package index.components

import antd.button.button
import antd.collapse.collapse
import antd.collapse.collapsePanel
import antd.message.message
import antd.modal.ModalComponent
import antd.pagination.PaginationConfig
import antd.table.ColumnProps
import antd.table.TableComponent
import antd.table.table
import index.ajax.*
import index.ajax.api.*
import index.modules.websocket
import index.utils.formatDate
import kotlinext.js.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.html.classes

import react.*
import react.dom.div
import react.dom.p
import kotlin.js.Date
import kotlin.js.Promise


private lateinit var refresh: (PageInfo<Order>) -> Unit
private lateinit var pageInfo: PageInfo<Order>

private val source = arrayOf<ColumnProps<Order>>(
  jsObject { title = "订单编号"; dataIndex = "orderNo"; key = "orderNo" },
  jsObject { title = "姓名"; dataIndex = "buyerName"; key = "buyerName" },
  jsObject { title = "手机号"; dataIndex = "buyerPhone"; key = "buyerPhone" },
  jsObject { title = "地址"; dataIndex = "buyerAddress"; key = "buyerAddress" },
  jsObject { title = "金额"; dataIndex = "amount"; key = "amount" },
  jsObject { title = "订单状态"; dataIndex = "status"; key = "status"
    render = { item, _, _ ->
      when(item.toString().toInt()) {
        0 -> "新订单"
        1 -> "完结"
        2 -> "已取消"
        else -> "订单状态异常"
      }
    }
  },
  jsObject { title = "支付方式"; dataIndex = "type"; key = "type" },
  jsObject { title = "支付状态"; dataIndex = "payStatus"; key = "payStatus"
    render = { item, _, _ ->
      when(item.toString().toInt()) {
        0 -> "未支付"
        1 -> "支付完成"
        else -> "支付状态异常"
      }
    }
  },
  jsObject { title = "创建时间"; dataIndex = "createTime"; key = "createTime"
    render = { item, _, _ -> Date(item.toString().toLong()).formatDate() }
  },
  jsObject { title = "操作"; dataIndex = "ops"; key = "ops"
    render = { _, data, _ ->
      buildElement {
        div {
          button { attrs.onClickCapture = { getDetail(data.orderNo) }; +"详情" }
          button {
            attrs {
              style = jsObject { marginRight = 10; marginLeft = 10 }
              if (data.status in (1..2)) disabled = true
              onClickCapture = { finishOrCancel(data.orderNo, true) }
            }; +"完结"
          }
          button {
            attrs {
              if (data.status in (1..2)) disabled = true
              onClickCapture = { finishOrCancel(data.orderNo, false) }
            }; +"取消"
          }
        }
      }.unsafeCast<Any>()
    }
  },
)


private fun RBuilder.orderDetail(order: Order) {
  div { attrs.classes = setOf("order-detail")
    collapse {
      order.orderDetails.forEach {
        collapsePanel {
          attrs {
            header = "订单id: ${it.id}"
            key = "${it.id}"
          }
          p { +"订单编号: ${it.orderNo}" }
          p { +"商品id: ${it.productId}" }
          p { +"商品数量: ${it.productQuantity}" }
          p { +"创建时间: ${Date(it.createTime).formatDate()}" }
          p { +"更新时间: ${Date(it.updateTime).formatDate()}" }
        }
      }
    }
  }
}

private fun getDetail(orderId: String)
= MainScope().launch {
  orderId.detail().onEach {
    ModalComponent.info(jsObject {
      title = "订单详情"
      content = buildElement { orderDetail(it) }
      onOk = { ModalComponent.destroyAll() }
    })
  }.collect()
}

private fun getOrderList()
= MainScope().launch {
  orderList(pageInfo.pageNum).onEach {
    it.content.withIndex().forEach { order ->
      order.value.asDynamic().key = order.index
      order.value.asDynamic().type = "微信"
    }
    refresh(it)
  }.collect()
}

private fun finishOrCancel(orderId: String, isFinish: Boolean) = ModalComponent.confirm(jsObject {
  val msg = if (isFinish) "完结" else "取消"
  title = "${msg}确认"
  content = "您确认要${msg}吗?"
  onOk = {
    MainScope().launch {
      (if (isFinish) orderId.finish() else orderId.cancel())
        .onEach { delay(1000) }
        .onEach { message.success("${msg}成功~") }
        .onEach { ModalComponent.destroyAll() }
        .onEach { getOrderList() }
        .collect()
    }
    Promise<dynamic> { _, _ ->  }
  }
})

private val orderContent = functionalComponent<RProps> {
  useState(jsObject<PageInfo<Order>> { pageNum = 1 }).apply {
    pageInfo = first; refresh = second
  }

  useEffect(emptyList()) { getOrderList() }

  div { attrs.classes = setOf("table")
    table<Order, TableComponent<Order>> {
      attrs {
        columns = source
        dataSource = pageInfo.content
        bordered = true
        pagination = jsObject<PaginationConfig> {
          onChange = { cur, _ -> pageInfo.pageNum = cur.toInt(); getOrderList() }
          current = pageInfo.pageNum
          pageSize = pageInfo.pageSize
          total = pageInfo.totalSize
          showQuickJumper = true
        }
      }
    }
  }
}

fun RBuilder.orderContent() = child(orderContent)
