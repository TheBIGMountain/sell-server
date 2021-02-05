package index.components

import antd.FormEvent
import antd.button.button
import antd.form.FormProps
import antd.form.form
import antd.form.formItem
import antd.input.input
import antd.message.message
import antd.modal.ModalComponent
import antd.modal.modal
import antd.pagination.PaginationConfig
import antd.select.SelectComponent
import antd.select.option
import antd.select.select
import antd.table.ColumnProps
import antd.table.TableComponent
import antd.table.table
import index.ajax.*
import index.ajax.api.*
import index.utils.formatDate
import kotlinext.js.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.html.HTML
import kotlinx.html.INPUT
import kotlinx.html.classes
import kotlinx.html.style
import org.w3c.dom.HTMLInputElement
import react.*
import react.dom.div

import react.dom.img
import kotlin.js.Date
import kotlin.js.Promise


private lateinit var refresh: (ProductState) -> Unit
private lateinit var state: ProductState
private var isUpdate = false
private var currentPage = 1

private fun RBuilder.productImg(url: String) {
  img {
    attrs {
      src = url
      width = "50px"
      height = "50px"
      style = jsObject<dynamic> {
        position = "absolute"
        transform = "translateY(-25px)"
      }.unsafeCast<String>()
    }
  }
}

private fun RBuilder.buttonGroup(data: Product, index: Int) {
  div { attrs.style = jsObject<dynamic> { position = "relative" }.unsafeCast<String>()
    if (index == 0) {
      button {
        attrs {
          type = "primary"
          className = "add-product"
          onClickCapture = { isUpdate = false; refresh(state.copy(product = jsObject { categoryType = 0 }, showModal = true)) }
        }; +"添加"
      }
    }
    button { attrs.onClickCapture = { getProductInfo(data.id) }; +"更新" }
    button {
      attrs {
        style = jsObject { marginRight = 10; marginLeft = 10 }
        if (data.status == 0) disabled = true
        onClickCapture = { onSellOrNotSell(data.id, true) }
      }; +"上架"
    }
    button {
      attrs {
        if (data.status == 1) disabled = true
        onClickCapture = { onSellOrNotSell(data.id, false) }
      }; +"下架"
    }
  }
}

private val categoryTypeMap = mapOf(
  "0" to "热销榜",
  "1" to "单人精彩套餐",
  "2" to "冰爽饮品限时特惠",
  "3" to "精选热菜",
  "4" to "爽口凉菜",
  "5" to "精选套餐",
  "6" to "果拼果汁",
  "7" to "小吃主食",
  "8" to "特色粥品"
)

private val source = arrayOf<ColumnProps<Product>>(
  jsObject { title = "商品id"; dataIndex = "id"; key = "id" },
  jsObject { title = "商品图片"; dataIndex = "icon"; key = "icon";
    render = { item, _, _ -> buildElement { productImg(item.toString()) }.unsafeCast<Any>() }
  },
  jsObject { title = "商品名"; dataIndex = "name"; key = "name" },
  jsObject { title = "商品价格"; dataIndex = "price"; key = "price" },
  jsObject { title = "商品库存"; dataIndex = "stock"; key = "stock" },
  jsObject { title = "商品描述"; dataIndex = "description"; key = "description" },
  jsObject { title = "商品状态"; dataIndex = "status"; key = "status"
    render = { item, _, _ ->
      when(item.toString().toInt()) {
        0 -> "在售"
        1 -> "已下架"
        else -> "商品状态异常"
      }
    }
  },
  jsObject { title = "商品类目"; dataIndex = "categoryType"; key = "categoryType"
    render = { item, _, _ -> categoryTypeMap[item.toString()] ?: "商品类目异常" }
  },
  jsObject { title = "创建时间"; dataIndex = "createTime"; key = "createTime"
    render = { item, _, _ -> Date(item.toString().toLong()).formatDate() }
  },
  jsObject { title = "更新时间"; dataIndex = "updateTime"; key = "updateTime"
    render = { item, _, _ -> Date(item.toString().toLong()).formatDate() }
  },
  jsObject { title = "操作"; dataIndex = "ops"; key = "ops"
    render = { _, data, index -> buildElements { buttonGroup(data, index.toInt()) }.unsafeCast<Any>() }
  },
)

private fun getProductInfo(productId: Int)
= MainScope().launch {
  isUpdate = true
  productId.productInfo()
    .onEach { refresh(state.copy(product = it, showModal = true)) }
    .collect()
}

private fun onSellOrNotSell(productId: Int, isOnSell: Boolean)
= MainScope().launch {
  val msg = if (isOnSell) "上架" else "下架"
  ModalComponent.confirm(jsObject {
    title = "确定要${msg}吗?"
    onOk = {
      MainScope().launch {
        (if (isOnSell) productId.productOnSell() else productId.productNotSell() )
        .onEach {
          delay(1000)
          message.success("${msg}成功~")
          ModalComponent.destroyAll()
        }.onEach { getProductList() }.collect()
      }
      Promise<dynamic> { _, _ -> }
    }
  })
}

private fun getProductList()
= MainScope().launch {
  productList(currentPage)
    .onEach { it.content.forEach { product -> product.asDynamic().key = product.id } }
    .onEach { refresh(state.copy(pageInfo = it)) }
    .collect()
}

private val formLayout = jsObject<FormProps> {
  labelCol = jsObject { span = 5 }
  wrapperCol = jsObject { span = 19 }
}

private data class ProductState(
  val pageInfo: PageInfo<Product> = jsObject {  },
  val product: Product = jsObject { categoryType = 0 },
  val showModal: Boolean = false
)

private fun <T> FormEvent<HTMLInputElement>.value(): T {
  return target.unsafeCast<INPUT>().value.unsafeCast<T>()
}


private fun productSave() = MainScope().launch {
  state.product.save()
    .onEach { getProductList() }
    .onEach { message.info("正在执行操作~") }
    .onEach { delay(1000) }
    .onEach { message.success("操作成功~") }
    .onEach { refresh(state.copy(showModal = false)) }
    .collect()
}

private val product = functionalComponent<RProps> {
  useState(ProductState()).apply { state = first; refresh = second }

  useEffect(emptyList()) { getProductList() }

  div { attrs.classes = setOf("table")
    table<Product, TableComponent<Product>> {
      attrs {
        columns = source
        dataSource = state.pageInfo.content
        bordered = true
        pagination = jsObject<PaginationConfig> {
          onChange = { cur, _ -> currentPage = cur.toInt(); getProductList() }
          current = state.pageInfo.pageNum
          pageSize = state.pageInfo.pageSize
          total = state.pageInfo.totalSize
          showQuickJumper = true
        }
      }
    }
  }
  modal {
    attrs {
      visible = state.showModal
      title = "商品数据"
      onOk = { productSave() }
      onCancel = { refresh(state.copy(showModal = false)) }
    }
    state.product.let { product ->
      form { attrs { wrapperCol = formLayout.wrapperCol; labelCol = formLayout.labelCol }
        if (isUpdate) {
          formItem { attrs { label = "商品id" }
            input { attrs { value = product.id; disabled = true } }
          }
        }
        formItem { attrs { label = "商品名称" }
          input {
            attrs {
              value = product.name
              onChangeCapture = { refresh(state.copy(product = product.apply { name = it.value() })) }
            }
          }
        }
        formItem { attrs { label = "商品价格" }
          input {
            attrs {
              value = product.price
              onChangeCapture = { try { refresh(state.copy(product = product.apply { price = it.value() })) } catch (_: Exception) {} }
            }
          }
        }
        formItem { attrs { label = "商品库存" }
          input {
            attrs {
              value = product.stock
              onChangeCapture = { try { refresh(state.copy(product = product.apply { stock = it.value() })) } catch (_: Exception) {} }
            }
          }
        }
        formItem { attrs { label = "商品描述" }
          input {
            attrs {
              value = product.description
              onChangeCapture = { refresh(state.copy(product = product.apply { description = it.value() })) }
            }
          }
        }
        formItem { attrs { label = "商品图片" }
          input {
            attrs {
              value = product.icon
              onChangeCapture = { refresh(state.copy(product = product.apply { icon = it.value() })) }
            }
          }
        }
        formItem { attrs { label = "商品目录" }
          select<String, SelectComponent<String>> {
            attrs {
              value = product.categoryType.toString()
              onChange = { value, _ -> refresh(state.copy(product = product.apply { categoryType = value.toInt() })) }
            }
            categoryTypeMap.entries.forEach {
              option { attrs { value = it.key; +it.value } }
            }
          }
        }
      }
    }
  }
}

fun RBuilder.product() = child(product)