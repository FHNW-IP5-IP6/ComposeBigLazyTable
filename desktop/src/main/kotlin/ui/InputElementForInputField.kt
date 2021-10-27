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

package ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDownCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import model.ILabel
import model.IModel
import model.attributes.Attribute
import model.attributes.BooleanAttribute
import model.attributes.DualAttribute
import model.attributes.SelectionAttribute
import model.meanings.Default
import model.modelElements.Group
import model.modelElements.HeaderGroup
import composeForms.ui.theme.ColorsUtil
import composeForms.ui.theme.DropdownColors
import composeForms.ui.theme.FormColors
import ui.util.CustomSwitch

val normalChars = listOf('a','b','c','d','e','f','g','h','i','j','k','l','n','o','p','q','r','s','t','u','v','x','y','z')
val bigChars = listOf('m','w')

/**
 * Modifiable input element for an input field. The Input Element is the UI element where the user can make an input.
 *
 * @param model: [IModel] that is the form made for
 * @param attr: [Attribute] that the input field shows
 * @param group: [Group] that is the input field for
 * @param color: [Color] that the input element has
 * @param focused: Flag that indicates if the attribute is focused
 *
 * @author Louisa Reinger, Steve Vogel
 */
@ExperimentalMaterialApi
@Composable
fun InputElement(model: IModel<*>, attr: Attribute<*, *, *>, group: Group<*>, color: Color, focused: Boolean, keyEvent: (KeyEvent) -> Boolean, keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>> = mutableMapOf()){

    val isHeaderGroup : Boolean = group is HeaderGroup
    val isSelectionField : Boolean = attr is SelectionAttribute
    val showReadOnly = attr.isReadOnly() || isHeaderGroup
    val dropDownIsOpen : MutableState<Boolean> = remember { mutableStateOf(false) }
    var selectionString : MutableState<String> = remember { mutableStateOf("") }

    with(model){
        if(isSelectionField) {
            selectionString = mutableStateOf((attr as SelectionAttribute).convertStringToType(attr.getValueAsText())
                .map{ it.getLanguageStringFromLabel(it as Enum<*>, getCurrentLanguage())}.toString().removePrefix("[").removeSuffix("]"))
        }

        var trailingIconWidth: Dp = 0.dp

        Column {
            Row(modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)){

                //Calculate Space for Trailing Icon
                val charsNumber = calculateTrailingIconSpace(attr.meaning.addMeaning(attr.getValueAsText()), isSelectionField)
                val noOfBigChars = charsNumber.first
                val noOfNormalChars = charsNumber.second

                trailingIconWidth = ((noOfBigChars * 20)+(noOfNormalChars * 13)).dp


                //Field
                BoxWithConstraints() {
                    if(attr is DualAttribute<*, *, *>) {
                        DualField(attr, model, group)
                    }else {
                        DefaultField(attr, selectionString.value, focused, showReadOnly, maxWidth, trailingIconWidth, keyEvent)
                    }
                }

                TrailingIcon(attr, showReadOnly, trailingIconWidth)

                DropDownIcon(model, attr, group, color, trailingIconWidth, dropDownIsOpen)
            }

            SelectionAttributeDropDownMenu(model, attr, dropDownIsOpen, keyListener)
        }
        //Line
        val lineColor = if(showReadOnly) Color.Transparent else color
        val lineThickness = if(focused) 2.dp else 1.dp
        Row(modifier = Modifier.height(2.dp)){
            Divider(color = lineColor, thickness = lineThickness)
        }
    }
}

//**********************************************************************************************************************
//Internal functions


//*********************************************
//Icons

/**
 * Creating TrailingIcon for [attr] if [showReadOnly] is false, attr has a meaning and is not a [SelectionAttribute]
 */
@Composable
private fun TrailingIcon(attr: Attribute<*,*,*>,
                         showReadOnly: Boolean,
                         trailingIconWidth: Dp){

    if(!showReadOnly && attr !is SelectionAttribute<*> && attr.meaning !== Default<Any>()){
        Row(modifier = Modifier.width(trailingIconWidth),
            horizontalArrangement = Arrangement.End){
            Text(attr.meaning.addMeaning(attr.getValueAsText()), color = ColorsUtil.get(FormColors.RIGHTTRACK))
        }
    }
}

/**
 * Creating DropDownIcon if it is a selection attribute
 *
 * @param model: Model that is the attribute in
 * @param attr: Attribute the dropdown icon is created for
 * @param group: Group that the attribute is in
 * @param color: Color that the icon has
 * @param trailingIconWidth: Width of the element has
 * @param dropDownIsOpen: flag that indicates if the drop down is shown
 */
@Composable
private fun DropDownIcon(model: IModel<*>,
                         attr: Attribute<*,*,*>,
                         group: Group<*>,
                         color: Color,
                         trailingIconWidth: Dp,
                         dropDownIsOpen: MutableState<Boolean>){
    if(attr is SelectionAttribute<*>){
        Row(modifier = Modifier.width(trailingIconWidth),
            horizontalArrangement = Arrangement.End){
            IconButton(
                onClick = { dropDownIsOpen.value = true; model.setCurrentFocusedAttribute(attr, group) },
                modifier = Modifier.clip(CircleShape).size(20.dp)
            ) {
                Icon(Icons.Filled.ArrowDropDownCircle, "DropDown", tint = color)
            }
        }
    }
}

//*********************************************
//Fields

/**
 * Create a DefaultField.
 *
 * @param attr: Attribute which the field is created for
 * @param selectionString: String reprensentation of the Selection Attribute
 * @param focused: flag that indicates if the field has focus
 * @param showReadOnly: if it is read only field
 * @param maxWidth: maximum width that the element can get
 * @param trailingIconWidth: width of the trailing icon
 * @param keyEvent: function that get triggered by a key event on the input field
 */
@Composable
private fun DefaultField(
    attr: Attribute<*,*,*>,
    selectionString: String,
    focused: Boolean,
    showReadOnly: Boolean,
    maxWidth: Dp,
    trailingIconWidth: Dp,
    keyEvent: (KeyEvent) -> Boolean
){
    val isSelectionField = attr is SelectionAttribute
    var text = if (!isSelectionField) attr.getValueAsText() else selectionString
    if(!focused && (!isSelectionField || attr.isValid())){
        text = attr.getFormattedValue()
    }

    if(showReadOnly){ //if readOnly show trailing icon at the end of the text
        text = text + " " + attr.meaning.addMeaning(attr.getValueAsText())
    }

    //State is used to set cursor to the end for a new manual focus
    var textFieldValueState by remember {
        mutableStateOf(
            TextFieldValue(
                text = text,
                selection = TextRange(text.length)
            )
        )
    }
    val textFieldValue = textFieldValueState.copy(text = text)

    val keyEventConsumed = remember { mutableStateOf(false) }
    BasicTextField(
        value = textFieldValue,
        onValueChange = {
            if(focused && attr !is SelectionAttribute) {
                attr.setValueAsText(it.text)
                textFieldValueState = it
            }
        },
        singleLine = true,
        modifier = Modifier
            .height(20.dp)
            .width(if(showReadOnly) maxWidth else maxWidth - trailingIconWidth)
            .onPreviewKeyEvent {
                onPreviewKeyEventDefaultField(attr, it, keyEventConsumed, keyEvent)
            },
        readOnly = showReadOnly || isSelectionField,
        textStyle = TextStyle(color = ColorsUtil.get(FormColors.NORMALTEXT))
    )
}


/**
 * Creating the UI for a [DualAttribute] that can have two states.
 *
 * @param dualAttribute: [DualAttribute] that the UI is created for
 * @param model: [IModel] that the form is created for
 * @param group: [Group] that the attribute is created in
 */
@ExperimentalMaterialApi
@Composable
private fun DualField(dualAttribute: DualAttribute<*, *, *>, model: IModel<*>, group: Group<*>){
    val isHeaderGroup = group is HeaderGroup

    val decision1IsSelected = dualAttribute.getValue() == dualAttribute.decision1SaveValue
    val colorDecision1 = if(!decision1IsSelected) ColorsUtil.get(FormColors.RIGHTTRACK) else ColorsUtil.get(FormColors.NORMALTEXT)
    val colorDecision2 = if(decision1IsSelected) ColorsUtil.get(FormColors.RIGHTTRACK) else ColorsUtil.get(FormColors.NORMALTEXT)

    if(model.getCurrentFocusedAttribute() == dualAttribute){
        model.setFocusBlocked(true)
        LocalFocusManager.current.clearFocus(true)
        model.setFocusBlocked(false)
    }

    val switchWidth = 40.dp

    fun onClick(){
        if(!isHeaderGroup) {
            model.setCurrentFocusedAttribute(dualAttribute, group)
        }
    }

    Row(verticalAlignment       = Alignment.CenterVertically,
        modifier                = Modifier.fillMaxWidth()){
        BoxWithConstraints {
            Row(modifier        = Modifier.width(((maxWidth/2)-(switchWidth/2)))
                .clickable {onClick(); if(!isHeaderGroup)dualAttribute.setValueAsText(dualAttribute.decision1SaveValue.toString())},
                horizontalArrangement = Arrangement.Start){
                Text(text       = dualAttribute.decision1Text.getLanguageStringFromLabel(dualAttribute.decision1Text as Enum<*>, model.getCurrentLanguage()),
                    color       = colorDecision1)
            }
        }

        CustomSwitch(
            modifier         = Modifier.width(switchWidth),
            checked          = !decision1IsSelected,
            onCheckedChange  = {onClick(); dualAttribute.changeDecision()},
            enabled          = !dualAttribute.isReadOnly() && !isHeaderGroup,
            booleanSwitch    = dualAttribute is BooleanAttribute
        )

        BoxWithConstraints {
            Row(modifier     = Modifier.width(maxWidth)
                .clickable {onClick(); if(!isHeaderGroup)dualAttribute.setValueAsText(dualAttribute.decision2SaveValue.toString())},
                horizontalArrangement = Arrangement.End){
                Text(text    = dualAttribute.decision2Text.getLanguageStringFromLabel(dualAttribute.decision2Text as Enum<*>, model.getCurrentLanguage()),
                    color    = colorDecision2)
            }
        }
    }
}

//*********************************************
//Drop-Down

/**
 * Creating drop down if the [attr] is a [SelectionAttribute].
 *
 * @param model: Model that is the attribute in
 * @param attr: Attribute the dropdown is created for
 * @param dropDownIsOpen: flag that indicates if the drop down is shown
 * @param keyEvent: function for custom key event on the input field
 */
@Composable
private fun SelectionAttributeDropDownMenu(model: IModel<*>,
                                           attr: Attribute<*,*,*>,
                                           dropDownIsOpen: MutableState<Boolean>,
                                           keyListener: MutableMap<Key, MutableMap<Int, () -> Unit>>){
    if(attr is SelectionAttribute){
        val possibleSelection = attr.getPossibleSelections()
        val selectedValues = attr.convertStringToType(attr.getValueAsText())
        BoxWithConstraints {
            DropDownMenu(
                model, attr, dropDownIsOpen, possibleSelection, selectedValues!!,
                attr::addUserSelection, attr::removeUserSelection, maxWidth, keyListener
            )
        }
    }
}

/**
 * Creating a DropDownMenu für an attribute
 *
 * @param model: [IModel] that is the form made for
 * @param attr: [Attribute] that the input field shows
 * @param dropDownIsOpen: Flat that indicates if the drop down menu should show the selection list
 * @param selections: list with possible selections as Set. First set is used
 * @param currentSelectionValue: Set with the current selected items
 * @param add: function for adding an element
 * @param remove: function for removing an element
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DropDownMenu(
    model: IModel<*>, attr: Attribute<*, *, *>, dropDownIsOpen: MutableState<Boolean>, selections: List<Set<*>>, currentSelectionValue: Set<*>,
    add: (Any)-> Unit, remove: (Any) -> Unit, width: Dp, keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>> = mutableMapOf()){

    val selectedIndex = remember { mutableStateOf(-1)}



    addListener(model, attr, keyListener, Key.Tab, true){
        dropDownIsOpen.value = false
        selectedIndex.value = -1
    }

    addListener(model, attr, keyListener, Key.Enter){
        dropDownIsOpen.value = !dropDownIsOpen.value
        selectedIndex.value = if(dropDownIsOpen.value) 0 else -1
    }
    addListener(model, attr, keyListener, Key.DirectionDown){
        selectedIndex.value = (selectedIndex.value +1) % selections.first().size
    }

    addListener(model, attr, keyListener, Key.DirectionUp){
        selectedIndex.value = (selectedIndex.value -1 + selections.first().size) % selections.first().size
    }

    addListener(model, attr, keyListener, Key.Spacebar){
        val setOfPossibilities: List<*> = selections.first().toList()
        if(selectedIndex.value in setOfPossibilities.indices) {
            if (!currentSelectionValue.contains(setOfPossibilities[selectedIndex.value])) {
                add(setOfPossibilities[selectedIndex.value]!!)
            } else {
                remove(setOfPossibilities[selectedIndex.value]!!)
            }
        }
    }

    CustomDropdownMenu(
        expanded = dropDownIsOpen.value,
        onDismissRequest = { dropDownIsOpen.value = false},
        modifier = Modifier.width(width).wrapContentHeight().sizeIn(maxHeight = 200.dp),
        offsetTop = DpOffset(0f.dp, 50.dp),
        offsetBottom = DpOffset(0.dp, 3.dp),
        content = {
            selections.first().forEachIndexed { index, label ->
                DropDownElement(model, label, currentSelectionValue, index, selectedIndex, attr.isReadOnly(), add, remove)
            }
        }
    )
}

/**
 * Create DropDownElement with [label] as shown text.
 *
 * @param model: model to get the current language
 * @param index: index of this element
 * @param selectedIndex: index which element is currently selected (hovering oder key selection)
 * @param readOnly: if the element is read only
 * @param add: function that gets invoked for adding an element
 * @param remove: function that gets invoked for removing an element
 */
@Composable
private fun DropDownElement(model: IModel<*>,
                            label: Any?, currentSelectionValue: Set<*>,
                            index: Int,
                            selectedIndex: MutableState<Int>,
                            readOnly: Boolean,
                            add: (Any)-> Unit,
                            remove: (Any) -> Unit){

    val elementIsSelected       = currentSelectionValue.contains(label)
    val elementIsSelectedBackgroundColor  = if(elementIsSelected) ColorsUtil.get(DropdownColors.BACKGROUND_ELEMENT_SEL)
    else ColorsUtil.get(DropdownColors.BACKGROUND_ELEMENT_NOT_SEL)
    val elementIsSelectedTextColor = if(elementIsSelected) {
        ColorsUtil.get(DropdownColors.TEXT_ELEMENT_SEL)
    } else {
        ColorsUtil.get(DropdownColors.TEXT_ELEMENT_NOT_SEL)
    }

    val borderStroke = if(index == selectedIndex.value){
        BorderStroke(2.dp, ColorsUtil.get(FormColors.BACKGROUND_COLOR_GROUPS))
    }else{
        BorderStroke(0.dp, Color.Transparent)
    }

    val onClick = {
        if (!elementIsSelected) {
            add(label!!)
        } else { remove(label!!)}
    }

    DropdownMenuItem(
        modifier = Modifier.background(elementIsSelectedBackgroundColor)
            .border(border = borderStroke, shape = RoundedCornerShape(4.dp))
            .pointerMoveFilter(onEnter = { selectedIndex.value = index; true }),
        onClick = onClick,
        content = { Text( text = (label as ILabel).getLanguageStringFromLabel(label as Enum<*>, model.getCurrentLanguage()),
            modifier = Modifier.background(elementIsSelectedBackgroundColor),
            color = elementIsSelectedTextColor) },
        enabled = !readOnly
    )
}

//*********************************************
//util

/**
 * Add [function] to the [keyListener].
 * If [withoutCurrentAttributeCheck] is true the function will always be called and with false the function is only called
 * when the [model]'s current focused attribute is the [attr]
 */
private fun addListener(model: IModel<*>,
                        attr: Attribute<*,*,*>,
                        keyListener: MutableMap<Key, MutableMap<Int, () -> Unit>>, key: Key,
                        withoutCurrentAttributeCheck: Boolean = false,
                        function: ()->Unit){
    if (keyListener.containsKey(key)) {
        keyListener[key]?.put(attr.getId()) {
            if(model.getCurrentFocusedAttribute() == attr || withoutCurrentAttributeCheck){
                function()
            }
        }
    } else {
        keyListener[key] = mutableMapOf(Pair(attr.getId(), {
            if(model.getCurrentFocusedAttribute() == attr || withoutCurrentAttributeCheck){
                function()
            }
        }))
    }
}

/**
 * Calculating the space needed for the text in [text] and counts additional space for [isSelectionField]
 *
 * @return Pair<Int,Int> with <noOfBigChars, noOfNormalChars>
 */
private fun calculateTrailingIconSpace(text: String, isSelectionField: Boolean): Pair<Int,Int>{
    var noOfBigChars = 0
    var noOfNormalChars = 0

    text.forEach {
        if(normalChars.contains(it.lowercaseChar())){
            noOfNormalChars ++
        } else{
            noOfBigChars ++
        }
    }

    if(isSelectionField){
        noOfNormalChars ++
        noOfBigChars ++
    }

    return Pair(noOfBigChars, noOfNormalChars)
}

/**
 * Function defining what happens with the key event on default field
 *
 * @param attr: [Attribute] that is the event for
 * @param keyEvent: KeyEvent that is triggered
 * @param keyEventConsumed: flag that indicates if the event is already consumed
 * @param keyEventFunction: function which will be invoked for a event
 */
@OptIn(ExperimentalComposeUiApi::class)
private fun onPreviewKeyEventDefaultField(attr: Attribute<*,*,*>,
                                          keyEvent: KeyEvent,
                                          keyEventConsumed: MutableState<Boolean>,
                                          keyEventFunction: (KeyEvent) -> Boolean): Boolean{
    if(keyEvent.isShiftPressed && keyEvent.isCtrlPressed && keyEvent.key == Key.Z && keyEvent.type == KeyEventType.KeyDown ){
        if(!keyEventConsumed.value){
            keyEventConsumed.value = true
            attr.redo()
        }
        return true
    }else if (keyEvent.isCtrlPressed && keyEvent.key == Key.Z && keyEvent.type == KeyEventType.KeyDown){
        if(!keyEventConsumed.value) {
            keyEventConsumed.value = true
            attr.undo()
        }
        return true
    }else if(keyEvent.type == KeyEventType.KeyUp){
        keyEventConsumed.value = false
    }
    if(keyEvent.type == KeyEventType.KeyDown) {
        keyEventFunction(keyEvent)
    }
    return false
}