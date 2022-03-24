package bigLazyTable.view.theme

import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import composeForms.ui.theme.*

private val DarkColorPalette = darkColors(
    primary = BackgroundColorGroups,
    primaryVariant = BackgroundColorGroups,
    secondary = BackgroundColorGroups
)

private val LightColorPalette = lightColors(
    primary = BackgroundColorLight,
    primaryVariant = BackgroundColorLight,
    secondary = BackgroundColorLight
)

val CustomScrollbarStyle = ScrollbarStyle(
    minimalHeight = ScrollbarMinimumHeight,
    thickness = ScrollbarThickness,
    shape = RoundedCornerShape(4.dp),
    hoverDurationMillis = 1000,
    hoverColor = HoverColor,
    unhoverColor = UnhoverColor
)

@Composable
fun BigLazyTableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // TODO-Future: Configure Dark/Light Colors & make UI completely respond to it (not just TextFields)
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(content = content)
}