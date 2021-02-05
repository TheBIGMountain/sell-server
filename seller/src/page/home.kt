package index.page


import index.components.navLeft


import kotlinx.html.classes
import react.*
import react.dom.div


private interface HomeProps: RProps {
  var content: RBuilder.() -> Unit
}

private val home = functionalComponent<HomeProps> { props ->
  div { attrs.classes = setOf("home")
    navLeft()
    props.content(this)
  }
}

fun RBuilder.home(content: RBuilder.() -> Unit) = child(home) { attrs.content = content }