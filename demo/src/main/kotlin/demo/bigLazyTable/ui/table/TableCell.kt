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

// TODO: Attribute- HeaderCell & TableCell
@Composable
fun AttributeTableCell(
    attribute: Attribute<*, *, *>,
    isTitle: Boolean = false,
    color: Color = Color.Black,
    backgroundColor: Color
) {
    TableCell(
        attribute = attribute,
        text = if (isTitle) attribute.getLabel() else attribute.getValueAsText(),
        width = attribute.tableColumnWidth,
        color = color,
        backgroundColor = backgroundColor,
        hasError = !attribute.isValid(),
        fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal
    )
}

@Composable
fun TableCell(
    attribute: Attribute<*, *, *>,
    text: String,
    width: Dp = attribute.tableColumnWidth,
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