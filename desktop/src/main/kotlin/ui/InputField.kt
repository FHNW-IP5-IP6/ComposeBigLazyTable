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

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusOrder
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import model.IModel
import model.attributes.Attribute
import model.attributes.DualAttribute
import model.modelElements.Group
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors

/**
 * Thi file contains the function for specific input field.
 *
 * @author Louisa Reinger, Steve Vogel
 */

/**
 * Creating the UI for an Input Field.
 *
 * @param model: The model that the form is for
 * @param attr: [Attribute] that the input field is for
 * @param group: [Group] that the attribtue is in
 * @param showValidationMessages: flag that indicate if the validations messages should be shown
 * @param keyEvent: function for custom key event on the input field
 */
@ExperimentalMaterialApi
@Composable
fun InputField(model: IModel<*>, attr: Attribute<*, *, *>, group: Group<*>, showValidationMessages: MutableState<Boolean>, keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>, keyEvent: (KeyEvent) -> Boolean){

    //*****************************
    //Focus Handling

    val focusRequester = remember { FocusRequester() }

    val focused = model.getCurrentFocusedAttribute() == attr && model.getCurrentFocusedGroup() == group
    if(focused){
        try {
            focusRequester.requestFocus()
        }catch(e: Exception){
            model.setFocusBlocked(true)
            CoroutineScope(Dispatchers.Default).launch{ delay(250); model.setFocusBlocked(false) }
            println("Focus request did crash. Blocking focus changes")
        }
    }

    val firstTimeUnfocused = remember { mutableStateOf(true) }

    if(firstTimeUnfocused.value && focused){
        firstTimeUnfocused.value = false
    }

    val inputFieldModifier = Modifier
        .padding(6.dp, 5.dp, 6.dp, 5.dp)
        .onFocusEvent { focS ->
            if (focused && !focS.isFocused && attr !is DualAttribute<*, *, *>) {
                attr.checkAndSetConvertibleBecauseUnfocusedAttribute()
                model.setCurrentFocusedAttribute(null, null)
            }
            if(focS.isFocused) {
                model.setCurrentFocusedAttribute(attr, group)
            }
        }
        .focusOrder(focusRequester)


    //*****************************
    //Variables
    val focusedColor    = getFocusedColor(attr)
    val unfocusedColor  = getUnfocusedColor(attr, firstTimeUnfocused.value, showValidationMessages.value)
    val color           = if(focused) focusedColor else unfocusedColor

    //*****************************
    // UI
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = inputFieldModifier){

        LabelAndUndoAndRedoButton(model, attr, color, focused)
        InputElement(model, attr, group, color, focused, keyEvent, keyListener)
        ErrorMessage(model, attr, focused, firstTimeUnfocused, showValidationMessages, keyListener)
    }
}

//**********************************************************************************************************************
//Internal functions

//*********************************************
//undo/redo

/**
 * Label and undo/redo button for an input-field
 *
 * @param model: [IModel] the form is for
 * @param attr: the [Attribute] the label and buttons are created for
 * @param color: used for text and icon
 * @param focused: flag that indicates if the attribute is focused
 */
@Composable
private fun LabelAndUndoAndRedoButton(model: IModel<*>, attr: Attribute<*, *, *>, color: Color, focused : Boolean) {
    val labelAreaHeight = if(attr is DualAttribute<*, *, *>) 28.dp else 32.dp
    val ctrlString = if(System.getProperty("os.name") == "Mac OS X") "CMD" else "CTRL"
    Row(modifier = Modifier
        .height(labelAreaHeight)
        .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            if (attr.isRequired()) attr.getLabel() + "*" else attr.getLabel(),
            fontSize = 12.sp,
            modifier = Modifier.align(Alignment.Top).padding(top = 8.dp),
            color = color,
            fontWeight = if (focused) FontWeight.Bold else FontWeight.Normal
        )
        if(focused){
            createUndoRedoButton(model, attr, ctrlString, color)
        }
    }
}

/**
 * Creating undo and redo buttons
 * @param model: Model to get hover texts
 * @param attr: [Attribute] the buttons are created for
 * @param ctrlString: String representing the operating systems control button
 * @param color: Color for the icon
 */
@Composable
private fun createUndoRedoButton(model: IModel<*>, attr: Attribute<*,*,*>, ctrlString: String, color: Color){
    Row(modifier = Modifier.width(56.dp)) {
        Box(modifier = Modifier.width(28.dp)) {
            if (attr.isUndoable()) {
                BoxWithTooltip(
                    tooltip = {
                        Surface(modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
                            Column(modifier = Modifier.wrapContentWidth().background(get(FormColors.BACKGROUND_COLOR_LIGHT)),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = model.getTooltipUndo(),
                                    modifier = Modifier.padding(4.dp),
                                    color = get(FormColors.NORMALTEXT)
                                )
                                Text(
                                    text = "($ctrlString+Z)",
                                    modifier = Modifier.padding(4.dp),
                                    color = get(FormColors.LABEL)
                                )
                            }
                        }
                    }, delay = 600
                ) {
                    IconButton(
                        onClick = { attr.undo() },
                        modifier = Modifier
                            .offset(y = (-1).dp)
                            .size(28.dp)
                            .padding(end = 1.dp)
                    ) {
                        Icon(Icons.Filled.Undo, "Undo", tint = color, modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
        Box(modifier = Modifier.width(28.dp)) {
            if (attr.isRedoable()) {
                BoxWithTooltip(
                    tooltip = {
                        Surface(modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
                            Column(modifier = Modifier.wrapContentWidth().background(get(FormColors.BACKGROUND_COLOR_LIGHT)),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = model.getTooltipRedo(),
                                    modifier = Modifier.padding(4.dp),
                                    color = get(FormColors.NORMALTEXT)
                                )
                                Text(
                                    text = "($ctrlString+SHIFT+Z)",
                                    modifier = Modifier.padding(4.dp),
                                    color = get(FormColors.LABEL)
                                )
                            }
                        }
                    }, delay = 600
                ) {
                    IconButton(
                        onClick = { attr.redo() },
                        modifier = Modifier
                            .offset(y = (-1).dp)
                            .size(28.dp)
                            .padding(end = 1.dp)
                    ) {
                        Icon(Icons.Filled.Redo, "Redo", tint = color, modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
}

//*********************************************
//error message

/**
 * Creating UI Element for showing error message
 *
 * @param model: [IModel] that is the form made for
 * @param attr: [Attribute] in which  the error occurred
 * @param focused: Flag indicates if the attribute is focused
 * @param firstTimeUnfocused: Flag that indicates if the attribute is focused for the first time
 * @param showValidationMessage: Flag indicating if the message should be shown
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ErrorMessage(model: IModel<*>,
                         attr: Attribute<*, *, *>,
                         focused: Boolean,
                         firstTimeUnfocused : MutableState<Boolean>,
                         showValidationMessage: MutableState<Boolean>,
                         keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){

    val ctrlString = if(System.getProperty("os.name") == "Mac OS X") "CMD" else "CTRL"
    val showErrorMsg = remember { mutableStateOf(false) }

    fun addListener(key: Key, function: ()->Unit){
        if (keyListener.containsKey(key)) {
            keyListener[key]?.put(attr.getId()) {
                if(model.getCurrentFocusedAttribute() == attr){
                    function()
                }
            }
        } else {
            keyListener[key] = mutableMapOf(Pair(attr.getId(), {
                if(model.getCurrentFocusedAttribute() == attr){
                    function()
                }
            }))
        }
    }

    addListener(Key.M){
        showErrorMsg.value = !showErrorMsg.value
    }


    val error = hasError(attr, focused, firstTimeUnfocused.value, showValidationMessage.value)

    //Error-Message
    val errorIconWidth = 20.dp

    Spacer(modifier = Modifier.height(2.dp))
    Row(modifier = Modifier.fillMaxWidth().height(20.dp).padding(end = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically){

        if (error && showErrorMsg.value) {
            BoxWithConstraints {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(maxWidth-errorIconWidth-4.dp),
                    horizontalArrangement = Arrangement.End) {
                    Card(modifier = Modifier
                        .border(0.dp, get(FormColors.ERROR), RoundedCornerShape(33))
                        .fillMaxHeight(),
                        backgroundColor = get(FormColors.ERROR)
                    ) {
                        val scrollState = rememberScrollState(0)
                        ErrorTexts(attr.getErrorMessages(), scrollState)

                        Box {
                            VerticalScrollbar(
                                modifier = Modifier.padding(start = 1.dp).align(TopEnd)
                                    .fillMaxHeight().width(8.dp),
                                adapter = rememberScrollbarAdapter(scrollState),
                                style = ScrollbarStyle(
                                    minimalHeight = 8.dp,
                                    thickness = 8.dp,
                                    shape = RectangleShape,
                                    hoverDurationMillis = 0,
                                    unhoverColor = get(FormColors.ERRORCONTRAST).copy(alpha = 0.3f),
                                    hoverColor = get(FormColors.ERRORCONTRAST).copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }
            }
        }

        ErrorIcon(model, ctrlString, error, showErrorMsg, errorIconWidth)
    }
}

/**
 * Creating the ErrorIcon.
 *
 * @param model: for getting the tooltip
 * @param ctrlString: String representing the operating systems control button
 * @param error: flag if it has an error
 * @param showErrorMsg: flag that indicates if the error is shown
 * @param errorIconWidth: width of the error icon
 */
@Composable
private fun ErrorIcon(model: IModel<*>,
                      ctrlString: String,
                      error: Boolean,
                      showErrorMsg: MutableState<Boolean>,
                      errorIconWidth: Dp){
    Row(modifier = Modifier.width(errorIconWidth),
        horizontalArrangement = Arrangement.End){
        if (error) {
            BoxWithTooltip(tooltip = {
                Surface(modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
                    Column(modifier = Modifier.wrapContentWidth().background(get(FormColors.BACKGROUND_COLOR_LIGHT)),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = model.getTooltipMessage(),
                            modifier = Modifier.padding(4.dp),
                            color = get(FormColors.NORMALTEXT)
                        )
                        Text(
                            text = "($ctrlString+M)",
                            modifier = Modifier.padding(4.dp),
                            color = get(FormColors.LABEL)
                        )
                    }
                }
            }, delay = 600
            ) {
                IconButton(
                    onClick = { showErrorMsg.value = !showErrorMsg.value },
                    modifier = Modifier.clip(CircleShape).size(20.dp)
                ) {
                    Icon(Icons.Filled.Error, "Error", tint = get(FormColors.ERROR))
                }
            }
        }
    }
}

@Composable
private fun ErrorTexts(messages: List<String>, scrollState: ScrollState){

    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        Column {
            for (msg in messages) {
                Text(
                    msg,
                    color = get(FormColors.ERRORCONTRAST),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(
                        start = if (scrollState.maxValue == 0) 4.dp else 12.dp,
                        end = 4.dp,
                        top = 1.dp
                    )
                )
            }
        }
    }
}

//*********************************************
// util

/**
 * Returning [Color] for focused [attr]
 */
private fun getFocusedColor(attr: Attribute<*,*,*>): Color{
    return if(attr.isValid()) get(FormColors.VALID) else if(attr.isRightTrackValid()) get(FormColors.RIGHTTRACK) else get(FormColors.ERROR)
}

/**
 * Returning [Color] for unfocused [attr]
 */
private fun getUnfocusedColor(attr: Attribute<*,*,*>, firstTimeUnfocused: Boolean, showValidationMessages: Boolean): Color{
    return if((attr.isValid() || (firstTimeUnfocused && !showValidationMessages))) get(FormColors.RIGHTTRACK) else {get(FormColors.ERROR)}
}

/**
 * Checking if the [attr] has an error based on if its [focused], [firstTimeUnfocused], [showValidationMessage] and
 * the validation of [attr]
 */
private fun hasError(attr: Attribute<*,*,*>,
                     focused: Boolean,
                     firstTimeUnfocused: Boolean,
                     showValidationMessage: Boolean): Boolean{
    return if(!focused) {
        if(firstTimeUnfocused){
            if(showValidationMessage){
                !attr.isValid()
            }else {
                false
            }
        }else {
            !attr.isValid()
        }
    } else {
        !attr.isRightTrackValid()
    }
}