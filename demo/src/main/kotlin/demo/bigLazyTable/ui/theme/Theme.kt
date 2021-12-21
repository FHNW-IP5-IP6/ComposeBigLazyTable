package demo.bigLazyTable.ui.theme

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowPlacement
import java.awt.Dimension

// TODO: Needs to be spezified
private val DarkColorPalette = darkColors(
    primary = BackgroundColorGroups,
    primaryVariant = BackgroundColorGroups,
    secondary = BackgroundColorGroups
)

// TODO: Needs to be spezified
private val LightColorPalette = lightColors(
    primary = BackgroundColorLight,
    primaryVariant = BackgroundColorLight,
    secondary = BackgroundColorLight
)

fun FrameWindowScope.initializeWindowSize() {
    window.apply {
        minimumSize = Dimension(1000, 800)
        placement = WindowPlacement.Maximized
    }
}

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