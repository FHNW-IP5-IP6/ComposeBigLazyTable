package demo.bigLazyTable.ui.theme

import androidx.compose.ui.graphics.Color

val HoverColor by lazy {
    println("Inside lazy HoverColor")
    Color.Red.copy(alpha = 0.8f)
}
val UnhoverColor by lazy {
    println("Inside lazy UnhoverColor")
    Color.Red.copy(alpha = 0.3f)
}
