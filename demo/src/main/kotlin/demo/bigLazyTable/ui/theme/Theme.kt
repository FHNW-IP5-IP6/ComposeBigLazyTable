package demo.bigLazyTable.ui.theme

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

// TODO: Needs to be spezified
private val DarkColorPalette = darkColors(
    primary = BackgroundColor,
    primaryVariant = BackgroundColor,
    secondary = BackgroundColor
)

// TODO: Needs to be spezified
private val LightColorPalette = lightColors(
    primary = BackgroundColorLight,
    primaryVariant = BackgroundColorLight,
    secondary = BackgroundColorLight
)

// TODO: When Dark/Light Theme should be supported
@Composable
fun BigLazyTableTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    DesktopMaterialTheme(
//        colors = colors,
        content = content
    )
}