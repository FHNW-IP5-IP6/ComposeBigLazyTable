package demo.bigLazyTable.ui.table

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
    width: Dp = 180.dp, // TODO: How to dynamically adjust?
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
            .padding(8.dp)
    )
}