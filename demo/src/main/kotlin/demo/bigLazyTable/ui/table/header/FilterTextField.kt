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
import bigLazyTable.paging.Between
import bigLazyTable.paging.IntFilter
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
                    is IntegerAttribute -> {
                        val allowedNonNumberChars = listOf('=', '!', '>', '<', '[', ',', ']')
                        val newRestrictedValue = newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
                        controller.attributeFilter[attribute] = newRestrictedValue
                        println("newNumberValue: $newRestrictedValue")
                        if (newRestrictedValue.length > 1) {
                            when (newRestrictedValue[0]) {
                                '!' -> {
                                    if (newRestrictedValue[1] == '=') {
                                        // not equals
                                        if (newRestrictedValue.length > 2) {
                                            val value = newRestrictedValue.substring(2).trim()
                                            if (value != "") {
                                                controller.attributeFilterNew[attribute] = IntFilter(
                                                    filter = value.toInt(),
                                                    dbField = attribute.databaseField!!,
                                                    filterType = NumberFilterType.NOT_EQUALS
                                                )
                                                controller.onNumberFilterChanged(
                                                    attribute,
                                                    value,
                                                    null,
                                                    NumberFilterType.NOT_EQUALS
                                                )
                                            }
                                        }
                                    } else {
                                        // invalid
                                    }
                                }
                                '=' -> {
                                    if (newRestrictedValue[1] == '!') {
                                        // not equals
                                        if (newRestrictedValue.length > 2) {
                                            val value = newRestrictedValue.substring(2).trim()
                                            if (value != "") {
                                                controller.attributeFilterNew[attribute] = IntFilter(
                                                    filter = value.toInt(),
                                                    dbField = attribute.databaseField!!,
                                                    filterType = NumberFilterType.NOT_EQUALS
                                                )
                                                controller.onNumberFilterChanged(
                                                    attribute,
                                                    value,
                                                    null,
                                                    NumberFilterType.NOT_EQUALS
                                                )
                                            }
                                        }
                                    } else if (newRestrictedValue[1].isDigit()) {
                                        // equals
                                        val value = newRestrictedValue.substring(1).trim()
                                        if (value != "") {
                                            controller.attributeFilterNew[attribute] = IntFilter(
                                                filter = value.toInt(),
                                                dbField = attribute.databaseField!!,
                                                filterType = NumberFilterType.EQUALS
                                            )
                                            controller.onNumberFilterChanged(
                                                attribute,
                                                value,
                                                null,
                                                NumberFilterType.EQUALS
                                            )
                                        }
                                    } else {
                                        // invalid
                                    }
                                }
                                '>' -> {
                                    when {
                                        newRestrictedValue[1] == '=' -> {
                                            // greaterEquals
                                            if (newRestrictedValue.length > 2) {
                                                val value = newRestrictedValue.substring(2).trim()
                                                if (value != "") {
                                                    controller.attributeFilterNew[attribute] = IntFilter(
                                                        filter = value.toInt(),
                                                        dbField = attribute.databaseField!!,
                                                        filterType = NumberFilterType.GREATER_EQUALS
                                                    )
                                                    controller.onNumberFilterChanged(
                                                        attribute,
                                                        value,
                                                        null,
                                                        NumberFilterType.GREATER_EQUALS
                                                    )
                                                }
                                            }
                                        }
                                        newRestrictedValue[1].isDigit() -> {
                                            // greater
                                            val value = newRestrictedValue.substring(1).trim()
                                            if (value != "") {
                                                controller.attributeFilterNew[attribute] = IntFilter(
                                                    filter = value.toInt(),
                                                    dbField = attribute.databaseField!!,
                                                    filterType = NumberFilterType.GREATER
                                                )
                                                controller.onNumberFilterChanged(
                                                    attribute,
                                                    value,
                                                    null,
                                                    NumberFilterType.GREATER
                                                )
                                            }
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
                                            if (newRestrictedValue.length > 2) {
                                                val value = newRestrictedValue.substring(2).trim()
                                                if (value != "") {
                                                    controller.attributeFilterNew[attribute] = IntFilter(
                                                        filter = value.toInt(),
                                                        dbField = attribute.databaseField!!,
                                                        filterType = NumberFilterType.LESS_EQUALS
                                                    )
                                                    controller.onNumberFilterChanged(
                                                        attribute,
                                                        value,
                                                        null,
                                                        NumberFilterType.LESS_EQUALS
                                                    )
                                                }
                                            }
                                        }
                                        newRestrictedValue[1].isDigit() -> {
                                            // less
                                            val value = newRestrictedValue.substring(1).trim()
                                            if (value != "") {
                                                controller.attributeFilterNew[attribute] = IntFilter(
                                                    filter = value.toInt(),
                                                    dbField = attribute.databaseField!!,
                                                    filterType = NumberFilterType.LESS
                                                )
                                                controller.onNumberFilterChanged(
                                                    attribute,
                                                    value,
                                                    null,
                                                    NumberFilterType.LESS
                                                )
                                            }
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
                                            val fromString = newRestrictedValue.substringAfter('[').substringBefore(',').trim()
                                            if (fromString.isNotBlank()) {
                                                val from = fromString//.toInt()
                                                println("from $from")

                                                val toString = newRestrictedValue.substringAfter(',').substringBefore(lastChar).trim()
                                                if (toString.isNotBlank()) {
                                                    val to = toString//.toInt()
                                                    println("to $to")
                                                    if (lastChar == ']') {
                                                        // from (included) between to (included)
                                                        controller.attributeFilterNew[attribute] = IntFilter(
                                                            filter = -1,
                                                            dbField = attribute.databaseField!!,
                                                            filterType = NumberFilterType.BETWEEN_BOTH_INCLUDED,
                                                            between = Between(
                                                                fromFilter = IntFilter(
                                                                    filter = from.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_BOTH_INCLUDED
                                                                ),
                                                                toFilter = IntFilter(
                                                                    filter = to.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_BOTH_INCLUDED
                                                                )
                                                            )
                                                        )
                                                        controller.onNumberFilterChanged(
                                                            attribute,
                                                            from,
                                                            to,
                                                            NumberFilterType.BETWEEN_BOTH_INCLUDED
                                                        )
                                                    } else {
                                                        // from (included) between to (not included)
                                                        controller.attributeFilterNew[attribute] = IntFilter(
                                                            filter = -1,
                                                            dbField = attribute.databaseField!!,
                                                            filterType = NumberFilterType.BETWEEN_FROM_INCLUDED,
                                                            between = Between(
                                                                fromFilter = IntFilter(
                                                                    filter = from.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_FROM_INCLUDED
                                                                ),
                                                                toFilter = IntFilter(
                                                                    filter = to.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_FROM_INCLUDED
                                                                )
                                                            )
                                                        )
                                                        controller.onNumberFilterChanged(
                                                            attribute,
                                                            from,
                                                            to,
                                                            NumberFilterType.BETWEEN_FROM_INCLUDED
                                                        )
                                                    }
                                                } else {
                                                    // invalid input
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
                                            val fromString = newRestrictedValue.substringAfter(']').substringBefore(',').trim()
                                            if (fromString.isNotBlank()) {
                                                val from = fromString//.toInt()
                                                println("from $from")

                                                val toString = newRestrictedValue.substringAfter(',').substringBefore(lastChar).trim()
                                                if (toString.isNotBlank()) {
                                                    val to = toString//.toInt()
                                                    println("to $to")
                                                    if (lastChar == ']') {
                                                        // from (not included) between to (included)
                                                        controller.attributeFilterNew[attribute] = IntFilter(
                                                            filter = -1,
                                                            dbField = attribute.databaseField!!,
                                                            filterType = NumberFilterType.BETWEEN_TO_INCLUDED,
                                                            between = Between(
                                                                fromFilter = IntFilter(
                                                                    filter = from.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_TO_INCLUDED
                                                                ),
                                                                toFilter = IntFilter(
                                                                    filter = to.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_TO_INCLUDED
                                                                )
                                                            )
                                                        )
                                                        controller.onNumberFilterChanged(
                                                            attribute,
                                                            from,
                                                            to,
                                                            NumberFilterType.BETWEEN_TO_INCLUDED
                                                        )
                                                    } else {
                                                        // from (not included) between to (not included)
                                                        controller.attributeFilterNew[attribute] = IntFilter(
                                                            filter = -1,
                                                            dbField = attribute.databaseField!!,
                                                            filterType = NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED,
                                                            between = Between(
                                                                fromFilter = IntFilter(
                                                                    filter = from.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED
                                                                ),
                                                                toFilter = IntFilter(
                                                                    filter = to.toInt(),
                                                                    dbField = attribute.databaseField!!,
                                                                    filterType = NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED
                                                                )
                                                            )
                                                        )
                                                        controller.onNumberFilterChanged(
                                                            attribute,
                                                            from,
                                                            to,
                                                            NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED
                                                        )
                                                    }
                                                } else {
                                                    // invalid input
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
                            controller.attributeCaseSensitive[attribute] =
                                !controller.attributeCaseSensitive[attribute]!!
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