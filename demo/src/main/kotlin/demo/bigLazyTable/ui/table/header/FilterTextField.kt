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
            modifier = Modifier.width(attribute.tableColumnWidth),
            value = controller.displayedFilterStrings[attribute].toString(),
            onValueChange = { newValue ->
                when (attribute) {
                    is NumberAttribute -> {
                        val allowedNonNumberChars = listOf('=', '!', '>', '<', '[', ',', ']')
                        val newRestrictedValue = newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
                        controller.displayedFilterStrings[attribute] = newRestrictedValue
                        println("newNumberValue: $newRestrictedValue")
                        if (newRestrictedValue.length > 1) {
                            when (newRestrictedValue[0]) {
                                '!' -> {
                                    if (newRestrictedValue[1] == '=') {
                                        if (newRestrictedValue.length > 2) {
                                            try {
                                                val value = newRestrictedValue.substring(2).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.NOT_EQUALS
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                // Invalid input
                                            }
                                        }
                                    } else {
                                        // invalid
                                    }
                                }
                                '=' -> {
                                    if (newRestrictedValue[1] == '!') {
                                        if (newRestrictedValue.length > 2) {
                                            try {
                                                val value = newRestrictedValue.substring(2).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.NOT_EQUALS
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                // Invalid input
                                            }
                                        }
                                    } else if (newRestrictedValue[1].isDigit()) {
                                        try {
                                            val value = newRestrictedValue.substring(1).trim()
                                            if (value != "") {
                                                createFilter(
                                                    controller = controller,
                                                    attribute = attribute,
                                                    value = value,
                                                    filterType = NumberFilterType.EQUALS
                                                )
                                            }
                                        } catch (e: Exception) {
                                            // Invalid input
                                        }
                                    } else {
                                        // invalid
                                    }
                                }
                                '>' -> {
                                    when {
                                        newRestrictedValue[1] == '=' -> {
                                            if (newRestrictedValue.length > 2) {
                                                try {
                                                    val value = newRestrictedValue.substring(2).trim()
                                                    if (value != "") {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = value,
                                                            filterType = NumberFilterType.GREATER_EQUALS
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    // Invalid input
                                                }
                                            }
                                        }
                                        newRestrictedValue[1].isDigit() -> {
                                            try {
                                                val value = newRestrictedValue.substring(1).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.GREATER
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                // Invalid input
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
                                            if (newRestrictedValue.length > 2) {
                                                try {
                                                    val value = newRestrictedValue.substring(2).trim()
                                                    if (value != "") {
                                                        createFilter(
                                                            controller = controller,
                                                            attribute = attribute,
                                                            value = value,
                                                            filterType = NumberFilterType.LESS_EQUALS
                                                        )
                                                    }
                                                } catch (e: Exception) {
                                                    // Invalid input
                                                }
                                            }
                                        }
                                        newRestrictedValue[1].isDigit() -> {
                                            try {
                                                val value = newRestrictedValue.substring(1).trim()
                                                if (value != "") {
                                                    createFilter(
                                                        controller = controller,
                                                        attribute = attribute,
                                                        value = value,
                                                        filterType = NumberFilterType.LESS
                                                    )
                                                }
                                            } catch (e: Exception) {
                                                // Invalid input
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

// TODO: Exception in thread "AWT-EventQueue-0" java.lang.NumberFormatException: For input string: "12900000000"
fun createFilter(
    controller: LazyTableController,
    attribute: Attribute<*, *, *>,
    value: String,
    filterType: NumberFilterType,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    when (attribute) {
        is ShortAttribute -> {
            controller.attributeFilterNew[attribute] = ShortFilter(
                filter = if (isBetween) 0 else value.toShort(),
                dbField = attribute.databaseField!!,
                filterType = filterType,
                between = if (isBetween) {
                    Between(
                        fromFilter = ShortFilter(
                            filter = from.toShort(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        ),
                        toFilter = ShortFilter(
                            filter = to.toShort(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        )
                    )
                } else null
            )
        }
        is IntegerAttribute -> {
            controller.attributeFilterNew[attribute] = IntFilter(
                filter = if (isBetween) 0 else value.toInt(),
                dbField = attribute.databaseField!!,
                filterType = filterType,
                between = if (isBetween) {
                    Between(
                        fromFilter = IntFilter(
                            filter = from.toInt(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        ),
                        toFilter = IntFilter(
                            filter = to.toInt(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        )
                    )
                } else null
            )
        }
        is LongAttribute -> {
            controller.attributeFilterNew[attribute] = LongFilter(
                filter = if (isBetween) 0 else value.toLong(),
                dbField = attribute.databaseField!!,
                filterType = filterType,
                between = if (isBetween) {
                    Between(
                        fromFilter = LongFilter(
                            filter = from.toLong(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        ),
                        toFilter = LongFilter(
                            filter = to.toLong(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        )
                    )
                } else null
            )
        }
        is FloatAttribute -> {
            controller.attributeFilterNew[attribute] = FloatFilter(
                filter = if (isBetween) 0f else value.toFloat(),
                dbField = attribute.databaseField!!,
                filterType = filterType,
                between = if (isBetween) {
                    Between(
                        fromFilter = FloatFilter(
                            filter = from.toFloat(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        ),
                        toFilter = FloatFilter(
                            filter = to.toFloat(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        )
                    )
                } else null
            )
        }
        is DoubleAttribute -> {
            controller.attributeFilterNew[attribute] = DoubleFilter(
                filter = if (isBetween) 0.0 else value.toDouble(),
                dbField = attribute.databaseField!!,
                filterType = filterType,
                between = if (isBetween) {
                    Between(
                        fromFilter = DoubleFilter(
                            filter = from.toDouble(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        ),
                        toFilter = DoubleFilter(
                            filter = to.toDouble(),
                            dbField = attribute.databaseField!!,
                            filterType = filterType
                        )
                    )
                } else null
            )
        }
    }
    controller.onFilterChanged()
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