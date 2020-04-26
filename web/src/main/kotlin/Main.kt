import react.child
import react.dom.render
import kotlin.browser.document

fun main() {
    render(document.getElementById("root")) {
        child(functionalComponent = App)
    }
}