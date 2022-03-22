/*
 *
 *   ========================LICENSE_START=================================
 *   Compose Forms
 *   %%
 *   Copyright (C) 2021 FHNW Technik
 *   %%
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   =========================LICENSE_END==================================
 *
 */

package composeForms.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import composeForms.model.IModel
import composeForms.model.modelElements.Field
import composeForms.model.modelElements.FieldSize
import composeForms.model.modelElements.Group
import composeForms.model.modelElements.HeaderGroup
import composeForms.ui.theme.*

/**
 * Creating the UI for a [Group].
 *
 * @param model: [IModel]
 * @param group: The [Group] that the UI is generated for
 * @param showValidationMessages: flag that indicates if the validations messages should be shown
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun Group(model : IModel<*>, group : Group<*>, showValidationMessages: MutableState<Boolean>, keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>> = mutableMapOf()){
    with(model){
        var isOpen by remember { mutableStateOf(true) }

        Column(modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 12.dp)) {

            if(group is HeaderGroup){
                HeaderGroup(model, group, isOpen) { isOpen = it }
            }else{
                GroupTitle(model, group, isOpen){ isOpen = it}
                if(isOpen) {
                    VerticalGrid(width = 600.dp, items = calculateNumberOfFieldsPerCell(group)) {
                        CellElement(model, it, group, showValidationMessages, keyListener)
                    }
                }
            }
        }

    }
}


//**********************************************************************************************************************
//Internal functions

/**
 * Creating the UI for [HeaderGroup].
 *
 * @param model: [IModel] used for the form
 * @param group: [HeaderGroup] for the UI to create
 * @param isOpen: flag that indicates if everything is shown
 * @param changeIsOpen: function for changing flag [isOpen]
 */
@ExperimentalMaterialApi
@Composable
private fun HeaderGroup(model: IModel<*>, group: HeaderGroup<*>, isOpen: Boolean, changeIsOpen: (Boolean) -> Unit = {}) {
    with(model){
        GroupTitle(model, group, isOpen, changeIsOpen)
        if (isOpen) {

            group.topSideHeader()

            Row(modifier = Modifier.fillMaxWidth()) {
                Box{
                    group.leftSideHeader()
                }
                Box(modifier = Modifier.weight(1f)) {
                    VerticalGrid(width = 600.dp, items = calculateNumberOfFieldsPerCell(group)) {
                        CellElement(model, it, group, mutableStateOf(false))
                    }
                }
                Box{
                    group.rightSideHeader()
                }
            }

            group.bottomSideHeader()

            Divider(color = BackgroundColorHeader)
        }
    }
}

/**
 * Calculating the grid cells
 *
 * @param group: The group that is put into the grid
 * @return MutableList<MutableList<Field>> list that is splited for the grid
 */
private fun calculateNumberOfFieldsPerCell(group: Group<*>): MutableList<MutableList<Field>> {

    //lists with one, two, three, or four fields (depending on field sizes) for grid cells
    val gridCells: MutableList<MutableList<Field>> = mutableListOf()

    fun getWeightOfGridCells(cellList : MutableList<Field>): Int {
        var size = 0
        cellList.forEach{
            size += when(it.getFieldSize()){
                FieldSize.BIG       -> 4
                FieldSize.NORMAL    -> 2
                FieldSize.SMALL     -> 1
            }
        }
        return size
    }

    group.getFields().forEach {
        when (it.getFieldSize()) {
            FieldSize.BIG -> gridCells.add(mutableStateListOf(it)) //new cell
            FieldSize.NORMAL -> if (gridCells.isEmpty() || getWeightOfGridCells(gridCells[gridCells.size - 1]) > 2) {
                gridCells.add(mutableStateListOf(it))       //new cell
            } else {
                gridCells.get(gridCells.size - 1).add(it)   //add field to current cell
            }
            FieldSize.SMALL -> if (gridCells.isEmpty() || getWeightOfGridCells(gridCells[gridCells.size - 1]) > 3) {
                gridCells.add(mutableStateListOf(it))       //new cell
            } else {
                gridCells.get(gridCells.size - 1).add(it)   //add field to current cell
            }
        }
    }

    return gridCells
}

/**
 * Creating the Title of a group
 *
 * @param model: [IModel] that the form is created for
 * @param group: the group that the title is created for
 * @param isOpen: flag that indicates if the group detail is open
 * @param changeIsOpen: function that sets the [isOpen]
 */
@Composable
private fun GroupTitle(model: IModel<*>, group: Group<*>, isOpen: Boolean, changeIsOpen: (Boolean) -> Unit = {}){
    val backgroundColor = if(group is HeaderGroup) BackgroundColorHeader else BackgroundColorGroups
    Card(
        modifier = Modifier.fillMaxWidth().height(38.dp).border(0.dp, Color.Transparent, RoundedCornerShape(10.dp)),
        backgroundColor = backgroundColor
    ){
        BoxWithConstraints(contentAlignment = Alignment.CenterEnd) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(group.getTitle(), color = FontOnBackground)
            }
            if(!model.isWizardMode() || group is HeaderGroup) {
                val icon = if (isOpen) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore
                val text = if (isOpen) "Expand Less" else "Expand More"
                IconButton(onClick = { changeIsOpen(!isOpen) }, modifier = Modifier.align(Alignment.CenterEnd)){
                    Icon(icon,
                        contentDescription = text,
                        tint = FontOnBackground)
                }
            }
        }
    }
}

/**
 * Creating one cell in the grid
 *
 * @param model: [IModel] that the form is made for
 * @param listOfFields: List with the fields for that cell
 * @param group: group that this cell is created for
 * @param showValidationMessages: flag that indicates if validation messages should be shown
 */
@ExperimentalMaterialApi
@Composable
private fun CellElement(model: IModel<*>, listOfFields : MutableList<Field>, group: Group<*>, showValidationMessages: MutableState<Boolean>, keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>> = mutableMapOf()){

    if(listOfFields[0].getFieldSize() == FieldSize.BIG){
        AttributeElement(model, listOfFields[0].getAttribute(), group, showValidationMessages, keyListener)
    }else{
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            Row {
                listOfFields.forEach{
                    val width = if(it.getFieldSize() == FieldSize.NORMAL) this@BoxWithConstraints.maxWidth/2 else this@BoxWithConstraints.maxWidth/4
                    Row(modifier = Modifier.width(width)) {
                        AttributeElement(model, it.getAttribute(), group, showValidationMessages, keyListener)
                    }
                }
            }
        }
    }
}

/**
 * Own Implementation of Vertical Grid. Taken from the implementation of
 * @see [LazyVerticalGrid]
 * Does not use vertical scrollable items.
 */
@Composable
private fun VerticalGrid(
    width: Dp,
    items: MutableList<MutableList<Field>>,
    modifier: Modifier = Modifier,
    content: @Composable (MutableList<Field>) -> Unit = { }
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val nColumns = maxOf((maxWidth / width).toInt(), 1)
        val totalSize = items.size
        val rows = (totalSize + nColumns - 1) / nColumns
        Column(
            modifier = modifier,
        ) {
            for(rowIndex in 0 until rows) {
                Row {
                    for (columnIndex in 0 until nColumns) {
                        val itemIndex = rowIndex * nColumns + columnIndex
                        if (itemIndex < totalSize) {
                            Box(
                                modifier = Modifier.weight(1f, fill = true),
                                propagateMinConstraints = true
                            ) {
                                content(items[itemIndex])
                            }
                        } else {
                            Spacer(Modifier.weight(1f, fill = true))
                        }
                    }
                }
            }
        }
    }
}
