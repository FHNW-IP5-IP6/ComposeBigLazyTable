package widgets

import androidx.compose.runtime.*
import org.jetbrains.compose.common.core.graphics.Color
import org.jetbrains.compose.common.foundation.border
import org.jetbrains.compose.common.foundation.layout.*
import org.jetbrains.compose.common.material.Button
import org.jetbrains.compose.common.material.Slider
import org.jetbrains.compose.common.material.Text
import org.jetbrains.compose.common.ui.Modifier
import org.jetbrains.compose.common.ui.background
import org.jetbrains.compose.common.ui.unit.dp
import org.jetbrains.compose.common.ui.unit.em

@Composable
fun FallingBalls(text: String) {
    Column(Modifier.fillMaxWidth().fillMaxHeight(1f)) {
        var value by mutableStateOf(0f)
        Box {
            Text(
                "Catch balls! $text",
                size = 1.8f.em,
                color = Color(218, 120, 91)
            )
        }
        Box {
            Text(
                "Score: $text",
                size = 1.8f.em
            )
        }
        Row {
            Slider(
                value = value,
                onValueChange = { value = it },
                modifier = Modifier.width(100.dp)
            )
        }
        var buttonText by mutableStateOf("Button")
        Button(
            Modifier
                .border(2.dp, Color(255, 215, 0))
                .background(Color.Yellow),
            onClick = { buttonText += " Uff" }
        ) {
            Text(if (value > 5f) buttonText else text, size = 2f.em)
        }
    }
}
