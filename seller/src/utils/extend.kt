package index.utils

import kotlin.js.Date

fun Date.formatDate(): String {
  return "${getFullYear()}-" +
          "${(getMonth() + 1).isAdd0()}-" +
          "${getDate().isAdd0()} " +
          "${getHours().isAdd0()}:" +
          "${getMinutes().isAdd0()}:" +
          "${getSeconds().isAdd0()}"
}
private fun Int.isAdd0() = if (this <= 10) "0$this" else this