package demo.bigLazyTable.ui.table.header

import androidx.compose.foundation.layout.*
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
    when (attribute) {
        is BooleanAttribute -> {
            val toggleState = remember { mutableStateOf(ToggleableState.Indeterminate) }
            TriStateCheckbox(
                state = toggleState.value,
                onClick = {
                    // TODO: Move complete when into controller
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
        is StringAttribute -> {
            TextField(
                modifier = Modifier.width(attribute.tableColumnWidth),
                value = controller.displayedFilterStrings[attribute].toString(),
                onValueChange = { newValue ->
                    controller.displayedFilterStrings[attribute] = newValue
                    controller.attributeFilterNew[attribute] = StringFilter(
                        filter = newValue,
                        dbField = attribute.databaseField as Column<String>,
                        // Case sensitive is not set again after first time! -> Workaround is that we create a new
                        // StringFilter everytime CaseSensitive icon is clicked [see below]
                        caseSensitive = controller.attributeCaseSensitive[attribute]!!
                    )
                    controller.onFilterChanged()
                },
                textStyle = TextStyle(color = Color.White),
                // TODO: Hardcoded strings oke oder .properties file oder sonst was? recherche falls einfach sonst oke so
                label = { Text("Filter", color = Color.White) },
                singleLine = true,
                leadingIcon  = { LeadingIcon(controller = controller, attribute = attribute) },
                trailingIcon = { TrailingIcon(controller = controller, attribute = attribute) }
            )
        }
        is NumberAttribute -> {
            TextField(
                modifier = Modifier.width(attribute.tableColumnWidth),
                value = controller.displayedFilterStrings[attribute].toString(),
                onValueChange = { newValue ->
                    // Controller.doit
                    NumberTextFieldUtil.onValueChange(
                        newValue = newValue,
                        controller = controller,
                        attribute = attribute
                    )
                },
                textStyle = TextStyle(color = Color.White),
                // TODO: Hardcoded strings oke oder .properties file oder sonst was?
                label = { Text("Filter", color = Color.White) },
                singleLine = true,
                trailingIcon = { TrailingIcon(controller = controller, attribute = attribute) }
            )
        }
    }
}

@Composable
fun LeadingIcon(
    controller: LazyTableController,
    attribute: Attribute<*, *, *>
) {
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

@Composable
fun TrailingIcon(
    controller: LazyTableController,
    attribute: Attribute<*, *, *>
) {
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