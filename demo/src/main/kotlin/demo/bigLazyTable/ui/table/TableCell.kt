package demo.bigLazyTable.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute

// TODO: Make Example Application to play around with the TableCell and its attribute
@Composable
fun RowScope.AttributeTableCell(
    attribute: Attribute<*, *, *>,
    backgroundColor: Color
) = TableCell(
    text = attribute.getValueAsText(),
    backgroundColor = backgroundColor,
    hasError = !attribute.isValid()
)

@Composable
fun TableCell(
    text: String,
    // TODO: I heard that you should never give a fixed width!
    width: Dp = 150.dp, // TODO: Make it dynamically adjust when there is more or less text
    color: Color = Color.Black,
    backgroundColor: Color,
    fontWeight: FontWeight = FontWeight.Normal,
    hasError: Boolean = false
) {
    Text(
        text = text,
        color = if (hasError) Color.Red else color,
        fontWeight = if (hasError) FontWeight.Bold else fontWeight,
        modifier = Modifier
            .background(backgroundColor)
            .width(width)
            .padding(8.dp),
    )
}