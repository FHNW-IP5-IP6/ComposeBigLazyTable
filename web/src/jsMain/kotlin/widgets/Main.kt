package widgets

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import kotlinx.browser.document
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.jetbrains.compose.web.ui.Styles

fun main() {
    val root = document.getElementById("root") as HTMLElement

    renderComposable(root = root) {
        Style(Styles)
        val text: String by mutableStateOf("test")
        FallingBalls(text = text)
    }
}
