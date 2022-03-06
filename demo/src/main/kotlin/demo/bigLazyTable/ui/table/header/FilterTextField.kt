package demo.bigLazyTable.ui.table.header

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
import androidx.compose.ui.unit.dp
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
    } else FilterDisabledTextField()
}

@Composable
fun FilterEnabledTextField(
    attribute: Attribute<*, *, *>,
    controller: LazyTableController
) {
    if (attribute is BooleanAttribute) {
        val toggleState = remember { mutableStateOf(ToggleableState.Indeterminate) }
        Row {
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
            Text(controller.displayedFilterStrings[attribute]!!, color = Color.White)
        }
    } else {
        TextField(
            modifier = Modifier.width(180.dp),
            value = controller.displayedFilterStrings[attribute].toString(),
            onValueChange = { newValue ->
                when (attribute) {
                    is IntegerAttribute -> {
                        val allowedNonNumberChars = listOf('=', '!', '>', '<', '[', ',', ']')
                        val newRestrictedValue = newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
                        controller.displayedFilterStrings[attribute] = newRestrictedValue
                        println("newNumberValue: $newRestrictedValue")
                        if (newRestrictedValue.length > 1) {
                            if (newRestrictedValue[0].isDigit()) {
                                // TODO: Why does this not work & we never come inside here?
                                println("kjfklerjfkerjk")
                                controller.attributeFilterNew[attribute] = IntFilter(
                                    filter = newRestrictedValue.toInt(),
                                    dbField = attribute.databaseField!!,
                                    filterType = NumberFilterType.EQUALS
                                )
                                controller.onFilterChanged()
                            } else {
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
                                                    controller.onFilterChanged()
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
                                                    controller.onFilterChanged()
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
                                                controller.onFilterChanged()
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
                                                        controller.onFilterChanged()
                                                    }
                                                }
                                            }
                                            newRestrictedValue[1].isDigit() -> {
                                                // greater
                                                val value = newRestrictedValue.substring(1).trim()
                                                if (value != "") {
                                                    controller.attributeFilterNew[attribute] = IntFilter(
                                                        // TODO: Exception in thread "AWT-EventQueue-0" java.lang.NumberFormatException: For input string: "12900000000"
                                                        //  2147483647 -> 10 Stellen
                                                        filter = value.toInt(),
                                                        dbField = attribute.databaseField!!,
                                                        filterType = NumberFilterType.GREATER
                                                    )
                                                    controller.onFilterChanged()
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
                                                        controller.onFilterChanged()
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
                                                    controller.onFilterChanged()
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
                                                val fromString =
                                                    newRestrictedValue.substringAfter('[').substringBefore(',').trim()
                                                if (fromString.isNotBlank()) {
                                                    val from = fromString//.toInt()
                                                    println("from $from")

                                                    val toString =
                                                        newRestrictedValue.substringAfter(',').substringBefore(lastChar)
                                                            .trim()
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
                                                            controller.onFilterChanged()
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
                                                            controller.onFilterChanged()
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
                                                val fromString =
                                                    newRestrictedValue.substringAfter(']').substringBefore(',').trim()
                                                if (fromString.isNotBlank()) {
                                                    val from = fromString//.toInt()
                                                    println("from $from")

                                                    val toString =
                                                        newRestrictedValue.substringAfter(',').substringBefore(lastChar)
                                                            .trim()
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
                                                            controller.onFilterChanged()
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
                                                            controller.onFilterChanged()
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