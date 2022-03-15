package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import bigLazyTable.paging.*
import composeForms.model.attributes.*
import demo.bigLazyTable.model.LazyTableController
import org.jetbrains.exposed.sql.Column

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
    } else FilterDisabledTextField(attribute)
}

@Composable
fun FilterEnabledTextField(
    attribute: Attribute<*, *, *>,
    controller: LazyTableController
) {
    if (attribute is BooleanAttribute) {
        val toggleState = remember { mutableStateOf(ToggleableState.Indeterminate) }
        Row(
            horizontalArrangement = Arrangement.Center
        ) {
            TriStateCheckbox(
                state = toggleState.value,
                onClick = {
                    when (toggleState.value) {
                        ToggleableState.Indeterminate -> {
                            toggleState.value = ToggleableState.On
                            val value = true
                            controller.displayedFilterStrings[attribute] = value.toString()
                            controller.attributeFilterNew[attribute] = BooleanFilter(
                                filter = value,
                                dbField = attribute.databaseField as Column<Boolean>
                            )
                            controller.onFilterChanged()
                        }
                        ToggleableState.On -> {
                            toggleState.value = ToggleableState.Off
                            val value = false
                            controller.displayedFilterStrings[attribute] = value.toString()
                            controller.attributeFilterNew[attribute] = BooleanFilter(
                                filter = value,
                                dbField = attribute.databaseField as Column<Boolean>
                            )
                            controller.onFilterChanged()
                        }
                        else -> {
                            toggleState.value = ToggleableState.Indeterminate
                            controller.displayedFilterStrings[attribute] = ""
                            controller.attributeFilterNew[attribute] = null
                            controller.onFilterChanged()
                        }
                    }
                }
            )
        }
    } else {
        TextField(
            modifier = Modifier.width(attribute.tableColumnWidth),
            value = controller.displayedFilterStrings[attribute].toString(),
            onValueChange = { newValue ->
                when (attribute) {
                    is NumberAttribute -> {
                        try {
                            val allowedNonNumberChars = listOf('=', '!', '>', '<', '[', ',', ']')
                            val newRestrictedValue =
                                newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
                            controller.displayedFilterStrings[attribute] = newRestrictedValue
                            println("newNumberValue: $newRestrictedValue")
                            if (newRestrictedValue.length > 1) {
                                when (newRestrictedValue[0]) {
                                    '!' -> {
                                        if (newRestrictedValue[1] == '=') {
                                            if (newRestrictedValue.length > 2) {
                                                val value = newRestrictedValue.substring(2).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.NOT_EQUALS
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    '=' -> {
                                        if (newRestrictedValue[1] == '!') {
                                            if (newRestrictedValue.length > 2) {
                                                val value = newRestrictedValue.substring(2).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.NOT_EQUALS
                                                    )
                                                }
                                            }
                                        } else if (newRestrictedValue[1].isDigit()) {
                                            val value = newRestrictedValue.substring(1).trim()
                                            if (value != "") {
                                                createFilter(
                                                    controller = controller,
                                                    attribute = attribute,
                                                    value = value,
                                                    filterType = NumberFilterType.EQUALS
                                                )
                                            }
                                        }
                                    }
                                    '>' -> {
                                        when {
                                            newRestrictedValue[1] == '=' -> {
                                                if (newRestrictedValue.length > 2) {
                                                    val value = newRestrictedValue.substring(2).trim()
                                                    if (value != "") {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = value,
                                                            filterType = NumberFilterType.GREATER_EQUALS
                                                        )
                                                    }
                                                }
                                            }
                                            newRestrictedValue[1].isDigit() -> {
                                                val value = newRestrictedValue.substring(1).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.GREATER
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    '<' -> {
                                        when {
                                            newRestrictedValue[1] == '=' -> {
                                                if (newRestrictedValue.length > 2) {
                                                    val value = newRestrictedValue.substring(2).trim()
                                                    if (value != "") {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = value,
                                                            filterType = NumberFilterType.LESS_EQUALS
                                                        )
                                                    }
                                                }
                                            }
                                            newRestrictedValue[1].isDigit() -> {
                                                val value = newRestrictedValue.substring(1).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.LESS
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    '[' -> {
                                        val lastChar = newRestrictedValue.trim().last()
                                        if (newRestrictedValue.contains(',') && ((lastChar == ']') || lastChar == '[')) {
                                            val from =
                                                newRestrictedValue.substringAfter('[').substringBefore(',').trim()
                                            if (from.isNotBlank()) {
                                                println("from $from")

                                                val to =
                                                    newRestrictedValue.substringAfter(',').substringBefore(lastChar)
                                                        .trim()
                                                if (to.isNotBlank()) {
                                                    println("to $to")
                                                    if (lastChar == ']') {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = "",
                                                            filterType = NumberFilterType.BETWEEN_BOTH_INCLUDED,
                                                            isBetween = true,
                                                            from = from,
                                                            to = to
                                                        )
                                                    } else {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = "",
                                                            filterType = NumberFilterType.BETWEEN_FROM_INCLUDED,
                                                            isBetween = true,
                                                            from = from,
                                                            to = to
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    ']' -> {
                                        val lastChar = newRestrictedValue.trim().last()
                                        if (newRestrictedValue.contains(',') && ((lastChar == ']') || lastChar == '[')) {
                                            val from =
                                                newRestrictedValue.substringAfter(']').substringBefore(',').trim()
                                            if (from.isNotBlank()) {
                                                println("from $from")

                                                val to =
                                                    newRestrictedValue.substringAfter(',').substringBefore(lastChar)
                                                        .trim()
                                                if (to.isNotBlank()) {
                                                    println("to $to")
                                                    if (lastChar == ']') {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = "",
                                                            filterType = NumberFilterType.BETWEEN_TO_INCLUDED,
                                                            isBetween = true,
                                                            from = from,
                                                            to = to
                                                        )
                                                    } else {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = "",
                                                            filterType = NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED,
                                                            isBetween = true,
                                                            from = from,
                                                            to = to
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            // TODO: Give User hint what went wrong & how to do it right
                            // for now we just ignore any occurring exception
                        }
                    }
                    else -> {
                        controller.displayedFilterStrings[attribute] = newValue
                        controller.attributeFilterNew[attribute] = StringFilter(
                            filter = newValue,
                            dbField = attribute.databaseField as Column<String>,
                            // Case sensitive is not set again after first time! -> Workaround is that we create a new
                            // StringFilter everytime CaseSensitive icon is clicked [see below]
                            caseSensitive = controller.attributeCaseSensitive[attribute]!!
                        )
                        controller.onFilterChanged()
                    }
                }
            },
            textStyle = TextStyle(color = Color.White),
            // TODO: Hardcoded strings oke oder .properties file oder sonst was?
            label = { Text("Filter", color = Color.White) },
            singleLine = true,
            leadingIcon = {
                if (attribute is StringAttribute) {
                    IconButton(
                        enabled = controller.attributeFilterNew[attribute] != null,
                        onClick = {
                            controller.attributeCaseSensitive[attribute] =
                                !controller.attributeCaseSensitive[attribute]!!

                            // Here we create a new StringFilter that caseSensitive changes are reflected
                            controller.attributeFilterNew[attribute] = StringFilter(
                                filter = controller.displayedFilterStrings[attribute]!!,
                                dbField = attribute.databaseField as Column<String>,
                                caseSensitive = controller.attributeCaseSensitive[attribute]!!
                            )
                            controller.onFilterChanged()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FormatSize,
                            contentDescription = "Case Sensitive Filtering",
                            tint = if (controller.attributeCaseSensitive[attribute] == true) Color.White else Color.Gray
                        )
                    }
                }
            },
            trailingIcon = {
                if (controller.displayedFilterStrings[attribute].toString().isNotEmpty()) {
                    IconButton(
                        onClick = {
                            controller.displayedFilterStrings[attribute] = ""
                            controller.attributeFilterNew[attribute] = null
                            controller.onFilterChanged()
                        }
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
fun FilterDisabledTextField(attribute: Attribute<*, *, *>) {
    TextField(
        modifier = Modifier.width(attribute.tableColumnWidth),
        value = "",
        onValueChange = {},
        textStyle = TextStyle(color = Color.White),
        singleLine = true,
        enabled = false
    )
}