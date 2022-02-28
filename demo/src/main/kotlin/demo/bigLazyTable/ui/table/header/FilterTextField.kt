package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import composeForms.model.attributes.*
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
    // TODO: Has no effect - can be deleted
    val keyboardOptions = when (attribute) {
        is NumberAttribute -> KeyboardOptions(keyboardType = KeyboardType.Number)
        else -> KeyboardOptions.Default
    }

    TextField(
        modifier = Modifier.width(180.dp),
        keyboardOptions = keyboardOptions,
        value = controller.attributeFilter[attribute].toString(),
        onValueChange = { newValue ->
            when (attribute) {
                is NumberAttribute -> {
                    val newNumberValue = newValue.filter { it.isDigit() /*|| (it == '>') || (it == '<') || (it == '=')*/ }
                    controller.onFiltersChanged(attribute, newNumberValue)
                }
                else -> controller.onFiltersChanged(attribute, newValue)
            }
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