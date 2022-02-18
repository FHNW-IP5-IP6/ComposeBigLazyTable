package demo.bigLazyTable.ui.theme

import androidx.compose.ui.graphics.Color
import composeForms.ui.theme.ColorsUtil.get
//import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors

// TODO: Check if there is a advantage when by lazy {} is used!
// Lazy properties: the value is computed only on first access
val BackgroundColorHeader by lazy {
    println("Inside lazy BackgroundColorHeader")
    get(FormColors.BACKGROUND_COLOR_HEADER)
}
val BackgroundColorGroups by lazy {
    println("Inside lazy BackgroundColorGroups")
    get(FormColors.BACKGROUND_COLOR_GROUPS)
}
val BackgroundColorLight by lazy {
    println("Inside lazy BackgroundColorLight")
    get(FormColors.BACKGROUND_COLOR_LIGHT)
}

val HoverColor by lazy {
    println("Inside lazy HoverColor")
    Color.Red.copy(alpha = 0.8f)
}
val UnhoverColor by lazy {
    println("Inside lazy UnhoverColor")
    Color.Red.copy(alpha = 0.3f)
}
