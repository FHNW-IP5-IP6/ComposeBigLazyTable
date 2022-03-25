package bigLazyTable.view.table.header

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DisabledByDefault
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.TextStyle
import bigLazyTable.controller.LazyTableController
import bigLazyTable.data.paging.StringFilter
import bigLazyTable.view.theme.ContentDescriptionCaseSensitiveIcon
import bigLazyTable.view.theme.ContentDescriptionClearFilterIcon
import bigLazyTable.view.theme.FilterLabel
import composeForms.model.attributes.*
import org.jetbrains.exposed.sql.Column

/**
 * @author Marco Sprenger, Livio Näf
 */
@Composable
fun FilterTextField(
    attribute: Attribute<*, *, *>,
    controller: LazyTableController<*>
) {
    if (attribute.canBeFiltered) {
        FilterEnabledTextField(
            attribute = attribute,
            controller = controller
        )
    }
    else FilterDisabledTextField(
        attribute = attribute,
        showDisabledTextField = false
    )
}

@Composable
fun FilterEnabledTextField(
    attribute: Attribute<*, *, *>,
    controller: LazyTableController<*>
) {
    when (attribute) {
        is BooleanAttribute -> {
            val toggleState = remember { mutableStateOf(ToggleableState.Indeterminate) }
            TriStateCheckbox(
                state = toggleState.value,
                onClick = { controller.onBooleanFilterChanged(toggleState, attribute) }
            )
        }
        is NumberAttribute -> {
            TextField(
                modifier = Modifier.width(attribute.tableColumnWidth),
                value = controller.displayedFilterStrings[attribute].toString(),
                onValueChange = { newValue ->
                    controller.onNumberFilterChanged(
                        newValue = newValue,
                        attribute = attribute
                    )
                },
                textStyle = TextStyle(color = Color.White),
                label = { Text(text = FilterLabel, color = Color.White) },
                singleLine = true,
                trailingIcon = { TrailingIcon(controller = controller, attribute = attribute) }
            )
        }
        is StringAttribute -> {
            TextField(
                modifier = Modifier.width(attribute.tableColumnWidth),
                value = controller.displayedFilterStrings[attribute].toString(),
                onValueChange = { newValue ->
                    controller.onStringFilterChanged(
                        newValue = newValue,
                        attribute = attribute,
                        notEqualsFilter = newValue.startsWith('!')
                    )
                },
                textStyle = TextStyle(color = Color.White),
                label = { Text(text = FilterLabel, color = Color.White) },
                singleLine = true,
                leadingIcon  = { LeadingIcon(controller = controller, attribute = attribute) },
                trailingIcon = { TrailingIcon(controller = controller, attribute = attribute) }
            )
        }
        // TODO-Future: Define a specific UI Element for SelectionAttribute & DecisionAttribute
        //  SelectionAttribute could have a DropdownMenu
        //  DecisionAttribute  could have a DropdownMenu or something like BooleanAttribute
    }
}

@Composable
fun LeadingIcon(
    controller: LazyTableController<*>,
    attribute: Attribute<*, *, *>
) {
    IconButton(
        enabled = controller.attributeFilter[attribute] != null,
        onClick = {
            controller.attributeCaseSensitive[attribute] = !controller.attributeCaseSensitive[attribute]!!

            // Here we create a new StringFilter that caseSensitive changes are reflected
            controller.attributeFilter[attribute] = StringFilter(
                filter = controller.displayedFilterStrings[attribute]!!,
                dbField = attribute.databaseField as Column<String>,
                caseSensitive = controller.attributeCaseSensitive[attribute]!!
            )
            controller.onFilterChanged()
        }
    ) {
        Icon(
            imageVector = Icons.Filled.FormatSize,
            contentDescription = ContentDescriptionCaseSensitiveIcon,
            tint = if (controller.attributeCaseSensitive[attribute] == true) Color.White else Color.Gray
        )
    }
}

@Composable
fun TrailingIcon(
    controller: LazyTableController<*>,
    attribute: Attribute<*, *, *>
) {
    if (controller.displayedFilterStrings[attribute].toString().isNotEmpty()) {
        IconButton(
            onClick = {
                controller.displayedFilterStrings[attribute] = ""
                controller.attributeFilter[attribute] = null
                controller.onFilterChanged()
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = ContentDescriptionClearFilterIcon,
                tint = Color.White
            )
        }
    }
}

@Composable
fun FilterDisabledTextField(attribute: Attribute<*, *, *>, showDisabledTextField: Boolean) {
    if (showDisabledTextField) {
        TextField(
            modifier = Modifier.width(attribute.tableColumnWidth),
            value = "Disabled",
            onValueChange = {},
            singleLine = true,
            enabled = false,
            readOnly = true,
            textStyle = TextStyle(color = Color.LightGray),
            leadingIcon = { Icon(Icons.Default.DisabledByDefault, null, tint = Color.LightGray) }
        )
    }
}