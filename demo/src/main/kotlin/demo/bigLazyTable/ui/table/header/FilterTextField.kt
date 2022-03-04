package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cases
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
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
    if (attribute is BooleanAttribute) {
        val toggleState = remember { mutableStateOf(ToggleableState.Indeterminate) }
        TriStateCheckbox(
            state = toggleState.value,
            onClick = {
                if (toggleState.value == ToggleableState.Indeterminate) {
                    toggleState.value = ToggleableState.On
                    controller.onFiltersChanged(attribute, "true")
                } else if (toggleState.value == ToggleableState.On) {
                    toggleState.value = ToggleableState.Off
                    controller.onFiltersChanged(attribute, "false")
                } else {
                    toggleState.value = ToggleableState.Indeterminate
                    controller.onFiltersChanged(attribute, "")
                }
            }
        )
    } else {
        TextField(
            modifier = Modifier.width(180.dp),
            value = controller.attributeFilter[attribute].toString(),
            onValueChange = { newValue ->
                when (attribute) {
                    is NumberAttribute -> {
                        val allowedNonNumberChars = listOf('=', '>', '<', '[', ',', ']')
                        val newNumberValue = newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
                        println("newNumberValue: $newNumberValue")
                        when (newNumberValue[0]) {
                            '=' -> {
                                // normal
                            }
                        }
                        controller.onFiltersChanged(attribute, newNumberValue)
                    }
                    else -> controller.onFiltersChanged(attribute, newValue)
                }
            },
            textStyle = TextStyle(color = Color.White),
            // TODO: Hardcoded strings oke oder .properties file oder sonst was?
            label = { Text("Filter", color = Color.White) },
            singleLine = true,
            leadingIcon = {
                if (attribute is StringAttribute) {
                    IconButton(
                        onClick = {
                            controller.attributeCaseSensitive[attribute] = !controller.attributeCaseSensitive[attribute]!!
                            controller.onFiltersChanged(attribute, controller.attributeFilter[attribute]!!)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FormatSize,
                            contentDescription = "Case Sensitive Filtering",
                            tint = if (controller.attributeCaseSensitive[attribute]!!) Color.White else Color.Gray
                        )
                    }
                }
            },
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