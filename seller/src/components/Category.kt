package index.components



import antd.table.ColumnProps
import antd.table.TableComponent
import antd.table.table
import index.ajax.api.categoryList
import index.utils.formatDate
import kotlinext.js.jsObject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.html.classes
import react.*
import react.dom.div
import kotlin.js.Date

private val source = arrayOf<ColumnProps<dynamic>>(
  jsObject { title = "目录id"; dataIndex = "categoryId"; key = "categoryId" },
  jsObject { title = "目录名"; dataIndex = "categoryName"; key = "categoryName" },
  jsObject { title = "创建时间"; dataIndex = "createTime"; key = "createTime"
    render = { item, _, _ -> Date(item.toString().toLong()).formatDate() }
  },
  jsObject { title = "更新时间"; dataIndex = "updateTime"; key = "updateTime";
    render = { item, _, _ -> Date(item.toString().toLong()).formatDate() }
  },
)

private val category = functionalComponent<RProps> {
  val (categoryList, setCategoryList) = useState<Array<dynamic>>(emptyArray())

  useEffect(emptyList()) {
    MainScope().launch {
      categoryList()
        .onEach { it.forEachIndexed { index, b -> b.key = index } }
        .onEach { setCategoryList(it) }
        .collect()
    }
  }

  div { attrs.classes = setOf("table")
    table<dynamic, TableComponent<dynamic>> {
      attrs {
        style = jsObject { transform = "translateY(-18%)" }
        columns = source
        dataSource = categoryList
        bordered = true
        pagination = false
      }
    }
  }
}

fun RBuilder.category() = child(category)