package demo.bigLazyTable.ui.theme

import androidx.compose.ui.graphics.Color
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors

// Lazy properties: the value is computed only on first access
val BackgroundColorHeader by lazy { get(FormColors.BACKGROUND_COLOR_HEADER) }
val BackgroundColorGroups by lazy { get(FormColors.BACKGROUND_COLOR_GROUPS) }
val BackgroundColorLight by lazy { get(FormColors.BACKGROUND_COLOR_LIGHT) }

val HoverColor by lazy { Color.Red.copy(alpha = 0.8f) }
val UnhoverColor by lazy { Color.Red.copy(alpha = 0.3f) }
