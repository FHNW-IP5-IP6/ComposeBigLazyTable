package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.layout.width
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import bigLazyTable.paging.NumberFilterType
import composeForms.model.attributes.*
import demo.bigLazyTable.model.LazyTableController
import java.lang.Character.isDigit

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
                        val allowedNonNumberChars = listOf('=', '!', '>', '<', '[', ',', ']')
                        val newRestrictedValue = newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
                        controller.attributeFilter[attribute] = newRestrictedValue
                        println("newNumberValue: $newRestrictedValue")
                        if (newRestrictedValue.length > 1) {
                            when (newRestrictedValue[0]) {
                                '!' -> {
                                    if (newRestrictedValue[1] == '=') {
                                        // not equals
                                        controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.NOT_EQUALS)
                                    } else {
                                        // invalid
                                    }
                                }
                                '=' -> {
                                    // equals
                                    controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.EQUALS)
                                }
                                '>' -> {
                                    when {
                                        newRestrictedValue[1] == '=' -> {
                                            // greaterEquals
                                            controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.GREATER_EQUALS)
                                        }
                                        newRestrictedValue[1].isDigit() -> {
                                            // greater
                                            controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.GREATER)
                                        }
                                        else -> {
                                            // invalid input
                                        }
                                    }
                                }
                                '<' -> {
                                    when {
                                        newRestrictedValue[1] == '=' -> {
                                            // lessEquals
                                            controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.LESS_EQUALS)
                                        }
                                        newRestrictedValue[1].isDigit() -> {
                                            // less
                                            controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.LESS)
                                        }
                                        else -> {
                                            // invalid input
                                        }
                                    }
                                }
                                '[' -> {
                                    val lastChar = newRestrictedValue.trim().last()
                                    if (newRestrictedValue.contains(',') && ((lastChar == ']') || lastChar == '[')) {
                                        try {
                                            val fromString = newRestrictedValue.substringBefore(',').trim()
                                            if (fromString.isNotBlank()) {
                                                val from = fromString.toInt()
                                                println("from $from")
                                            } else {
                                                // invalid input
                                            }

                                            val toString = newRestrictedValue.substringBefore(lastChar).trim()
                                            if (toString.isNotBlank()) {
                                                val to = toString.toInt()
                                                println("to $to")
                                                if (lastChar == ']') {
                                                    // from (included) between to (included)
                                                    controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.BETWEEN)
                                                } else {
                                                    // from (included) between to (not included)
                                                    controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.BETWEEN)
                                                }
                                            } else {
                                                // invalid input
                                            }

                                        } catch (e: Exception) {
                                            // invalid input
                                        }
                                    } else {
                                        // invalid input
                                    }
                                }
                                ']' -> {
                                    val lastChar = newRestrictedValue.trim().last()
                                    if (newRestrictedValue.contains(',') && ((lastChar == ']') || lastChar == '[')) {
                                        try {
                                            val fromString = newRestrictedValue.substringBefore(',').trim()
                                            if (fromString.isNotBlank()) {
                                                val from = fromString.toInt()
                                                println("from $from")
                                            } else {
                                                // invalid input
                                            }

                                            val toString = newRestrictedValue.substringBefore(lastChar).trim()
                                            if (toString.isNotBlank()) {
                                                val to = toString.toInt()
                                                println("to $to")
                                                if (lastChar == ']') {
                                                    // from (not included) between to (included)
                                                    controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.BETWEEN)
                                                } else {
                                                    // from (not included) between to (not included)
                                                    controller.onNumberFilterChanged(attribute, newRestrictedValue, NumberFilterType.BETWEEN)
                                                }
                                            } else {
                                                // invalid input
                                            }

                                        } catch (e: Exception) {
                                            // invalid input
                                        }
                                    } else {
                                        // invalid input
                                    }
                                }
                                else -> {
                                    // invalid input
                                }
                            }
                        }
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