package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.LazyTableController

@Composable
fun FilterTextField(
    attribute: Attribute<*, *, *>,
    controller: LazyTableController
) {
    if (attribute.canBeFiltered) {
        FilterEnabledTextField(
            attribute = attribute,
            controller = controller
        )
    } else FilterDisabledTextField()
}

@Composable
fun FilterEnabledTextField(
    attribute: Attribute<*, *, *>,
    controller: LazyTableController
) {
    TextField(
        modifier = Modifier.width(180.dp),
        value = controller.attributeFilter[attribute].toString(),
        onValueChange = { newValue ->
            controller.onFiltersChanged(attribute, newValue)
        },
        textStyle = TextStyle(color = Color.White),
        // TODO: Hardcoded strings oke oder .properties file oder sonst was?
        label = { Text("Filter", color = Color.White) },
        singleLine = true,
        trailingIcon = {
            if (controller.attributeFilter[attribute].toString().isNotEmpty()) {
                IconButton(
                    onClick = { controller.onFiltersChanged(attribute, "") }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Clear Filter",
                        tint = Color.White
                    )
                }
            }
        }
    )
}

// TODO: Should we write something when a field can not be filtered?
@Composable
fun FilterDisabledTextField() {
    TextField(
        modifier = Modifier.width(180.dp),
        value = "",
        onValueChange = {},
        textStyle = TextStyle(color = Color.White),
        singleLine = true,
        enabled = false
    )
}