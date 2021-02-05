package index


import index.router.router
import kotlinext.js.*
import kotlinx.browser.document
import react.dom.*
import kotlin.js.RegExp

fun main() {
  require("antd/dist/antd.css")
  requireAll(require.context("src/style", true, js("/\\.css$/").unsafeCast<RegExp>()))

  render(document.getElementById("root")) {
    app()
  }
}
