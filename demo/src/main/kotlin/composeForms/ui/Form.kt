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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import composeForms.model.IModel
import composeForms.model.modelElements.HeaderGroup
import composeForms.ui.theme.BodyBackground
import demo.bigLazyTable.model.AppState


/**
 * [Form] is the main class for generating a form.
 *
 * @author Louisa Reinger, Steve Vogel
 */
class Form {

    /**
     * Entry point to generate the UI for a form.
     * @param model: [IModel] that the UI is generated for
     */
    @ExperimentalMaterialApi
    @ExperimentalFoundationApi
    @Composable
    fun of(model: IModel<*>, appState: AppState? = null) {
        val keyEventsFromUIElement = remember { mutableMapOf<Key, MutableMap<Int, () -> Unit>>() }
        val showValidations = remember { mutableStateOf(false) }
        addDefaultKeyBehaviour(model, keyEventsFromUIElement) { showValidations.value = it }

        Column(modifier = Modifier.fillMaxSize().background(BodyBackground)) {
            Header(model = model, appState = appState) { showValidations.value = it }
            StickyBody(model)
            Body(model, showValidations, keyEventsFromUIElement)
        }

        ExceptionWindow(model)
    }
}


//**********************************************************************************************************************
//Internal functions

/**
 * Creating the body part of the form
 * @param model: [IModel] that is used for the generation
 * @param showValidationMessages: flag that indicates if the validation messages should be shown
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun Body(
    model: IModel<*>,
    showValidationMessages: MutableState<Boolean>,
    keyListener: MutableMap<Key, MutableMap<Int, () -> Unit>>
) {
    with(model) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                if (getAllGroups().isEmpty()) {
                    model.setException(IllegalArgumentException("No Group added"))
                }
                if (isWizardMode()) { // current group
                    if (getCurrentWizardGroup() != null) {
                        Group(model, getCurrentWizardGroup()!!, showValidationMessages, keyListener)
                    }
                } else { // all groups
                    getAllGroups().filter { it !is HeaderGroup }.forEach {
                        Group(model, it, showValidationMessages, keyListener)
                    }
                }
            }
        }
    }
}

/**
 * Creating the top of the body that stays on the screen even with scrolling.
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
private fun StickyBody(model: IModel<*>) {
    with(model) {
        getAllGroups().filterIsInstance<HeaderGroup<*>>().forEach {
            Group(model, it, mutableStateOf(false))
        }
    }
}


/**
 * Adds default behaviour for key events. Therefore the key events have to be passed further if some field specific
 * events are made.
 * @param composeForms.model: [IModel] that receives the command clicked on the keyboard.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun addDefaultKeyBehaviour(
    model: IModel<*>, keyListener: Map<Key, MutableMap<Int, () -> Unit>>,
    changeShowError: (Boolean) -> Unit
) {

    val key: Key = if (System.getProperty("os.name") == "Mac OS X") Key.MetaLeft else Key.CtrlLeft

    /*
    Unluckily Compose API has changed in this part a lot & it is not anymore possible to access LocalAppWindow.current
    because this one doesn't exist anymore! There is a LocalWindowInfo.current but only with 1 method isWindowFocused.

    The new way & more information can be found here: https://github.com/JetBrains/compose-jb/blob/master/tutorials/Keyboard/README.md

    In Short:
    There are two ways to handle key events in Compose for Desktop:
    - By setting up an event handler based on the element that is in focus
    - By setting up an event handler in the scope of the window
    */

//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(key, Key.S))){
//        changeShowError(!model.save())
//    }
//    LocalAppWindow.current.keyboard.setShortcut(Key.Tab){
//        model.focusNext()
//        keyListener[Key.Tab]?.map { it.value }?.forEach{ it()}
//    }
//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(Key.Tab, Key.ShiftLeft))){
//        model.focusPrevious()
//        keyListener[Key.Tab]?.forEach {
//            it.value()
//        }
//    }
//    LocalAppWindow.current.keyboard.setShortcut(Key.Spacebar){
//        if(model.getCurrentFocusedAttribute() is DualAttribute<*, *, *>){
//            (model.getCurrentFocusedAttribute() as DualAttribute<*,*,*>).changeDecision()
//        }
//        keyListener[Key.Spacebar]?.map { it.value }?.forEach{ it()}
//    }
//    LocalAppWindow.current.keyboard.setShortcut(Key.Enter){
//        keyListener[Key.Enter]?.map { it.value }?.forEach{ it()}
//    }
//    LocalAppWindow.current.keyboard.setShortcut(Key.DirectionUp){
//        keyListener[Key.DirectionUp]?.map { it.value }?.forEach{ it()}
//    }
//    LocalAppWindow.current.keyboard.setShortcut(Key.DirectionDown){
//        keyListener[Key.DirectionDown]?.map { it.value }?.forEach{ it()}
//    }
//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(key, Key.R))){
//        model.reset()
//    }
//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(key, Key.B))){
//        if(model.getCurrentGroupIndex() != 0){
//            model.previousWizardGroup()
//        }
//    }
//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(key, Key.N))){
//        changeShowError(!model.save())
//    }
//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(key, Key.E))){
//        if(model.isWizardMode() && model.isLastWizardGroup() && model.isValidForWizardGroup()) {
//            AppManager.focusedWindow?.close()
//        }
//    }
//    LocalAppWindow.current.keyboard.setShortcut(KeysSet(setOf(key, Key.M))){
//        keyListener[Key.M]?.map { it.value }?.forEach{ it()}
//    }

}
