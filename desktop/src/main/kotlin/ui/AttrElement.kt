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

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import model.IModel
import model.attributes.*
import model.modelElements.Group
import model.validators.semanticValidators.NumberValidator

/**
 * @author Louisa Reinger, Steve Vogel
 */


/**
 * Create an [AttributeElement] of the correct type (depending on the attribute).
 *
 * @param model: [IModel] that is the form for
 * @param attr: [Attribute] to create UI Element for
 * @param group: [Group] that is this attribute in
 * @param showValidationMessages: flag indicating if the validation messages should be shown
 */
@ExperimentalMaterialApi
@Composable
fun AttributeElement(model: IModel<*>, attr: Attribute<*, *, *>, group: Group<*>, showValidationMessages: MutableState<Boolean>, keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
        when (attr) {
        is StringAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is LongAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is IntegerAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is ShortAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is DoubleAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is FloatAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is SelectionAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is DecisionAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
        is BooleanAttribute -> AttributeElement(model, attr, group, showValidationMessages, keyListener)
    }
}

//**********************************************************************************************************************
//Internal functions

@ExperimentalMaterialApi
@Composable
private fun AttributeElement(model: IModel<*>,
                             strAttr: StringAttribute<*>,
                             group: Group<*>,
                             showValidationMessages: MutableState<Boolean>,
                             keyListener : MutableMap<Key,
                             MutableMap<Int, () -> Unit>>){
    InputField(model, strAttr, group, showValidationMessages = showValidationMessages, keyListener = keyListener){return@InputField true}
}

@ExperimentalMaterialApi
@Composable
private fun AttributeElement(model: IModel<*>,
                             longAttr: LongAttribute<*>,
                             group: Group<*>,
                             showValidationMessages: MutableState<Boolean>,
                             keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, longAttr, group, showValidationMessages = showValidationMessages, keyListener = keyListener) {
        numberKeyEventHandler(longAttr, it)
        return@InputField true
    }
}

@ExperimentalMaterialApi
@Composable
private fun AttributeElement(model: IModel<*>,
                             intAttr: IntegerAttribute<*>,
                             group: Group<*>,
                             showValidationMessages: MutableState<Boolean>,
                             keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, intAttr, group, showValidationMessages = showValidationMessages, keyListener = keyListener){
        numberKeyEventHandler(intAttr, it)
        return@InputField true
    }
}

@ExperimentalMaterialApi
@Composable
private fun AttributeElement(model: IModel<*>,
                             shortAttr: ShortAttribute<*>,
                             group: Group<*>,
                             showValidationMessages: MutableState<Boolean>,
                             keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, shortAttr, group, showValidationMessages = showValidationMessages, keyListener = keyListener){
        numberKeyEventHandler(shortAttr, it)
        return@InputField true
    }
}

@ExperimentalMaterialApi
@Composable
private fun AttributeElement(model: IModel<*>,
                             floatAttr: FloatAttribute<*>,
                             group: Group<*>,
                             showValidationMessages: MutableState<Boolean>,
                             keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, floatAttr, group, showValidationMessages = showValidationMessages, keyListener = keyListener){
        return@InputField true}
}

@ExperimentalMaterialApi
@Composable
private fun AttributeElement(model: IModel<*>,
                             doubleAttr: DoubleAttribute<*>,
                             group: Group<*>,
                             showValidationMessages: MutableState<Boolean>,
                             keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, doubleAttr, group, showValidationMessages = showValidationMessages, keyListener = keyListener){
        return@InputField true
    }
}

@ExperimentalMaterialApi
@Composable fun AttributeElement(model: IModel<*>,
                                 selectionAttribute: SelectionAttribute<*>,
                                 group: Group<*>,
                                 showValidationMessages: MutableState<Boolean>,
                                 keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, selectionAttribute, group, showValidationMessages = showValidationMessages, keyListener){
        return@InputField true
    }
}

@ExperimentalMaterialApi
@Composable fun AttributeElement(model: IModel<*>,
                                 decisionAttribute: DecisionAttribute<*>,
                                 group: Group<*>,
                                 showValidationMessages: MutableState<Boolean>,
                                 keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, decisionAttribute, group, showValidationMessages = showValidationMessages, keyListener = keyListener){
        return@InputField true
    }
}

@ExperimentalMaterialApi
@Composable fun AttributeElement(model: IModel<*>,
                                 booleanAttribute: BooleanAttribute<*>,
                                 group: Group<*>, showValidationMessages: MutableState<Boolean>,
                                 keyListener : MutableMap<Key, MutableMap<Int, () -> Unit>>){
    InputField(model, booleanAttribute, group, showValidationMessages = showValidationMessages, keyListener = keyListener){
        return@InputField true
    }
}

//*******************************************
//special functions for number attributes


/**
 * Key handler for number attributes. Going one step up or down
 *
 * @param attr: Attribute that is used on
 * @param it: KeyEvent that contains which key is pressed
 */
@OptIn(ExperimentalComposeUiApi::class)
private fun numberKeyEventHandler(attr: NumberAttribute<*,*,*>, it: KeyEvent){
    if(attr.getValueAsText() != "" && (it.key == Key.DirectionUp || it.key == Key.DirectionDown)) {
        try {
            val valueAsText = attr.getValueAsText()
            val longValue = valueAsText.toLong()

            val valIsOnStep = if(attr is IntegerAttribute) {
                attr.validators.filterIsInstance<NumberValidator<Int, *>>().first().isOnStepSize(longValue.toInt())
            } else if(attr is ShortAttribute){
                attr.validators.filterIsInstance<NumberValidator<Short, *>>().first().isOnStepSize(longValue.toShort())
            }else{
                attr.validators.filterIsInstance<NumberValidator<Long, *>>().first().isOnStepSize(longValue)
            }
            if(valIsOnStep){
                doValidStep(attr, it.key)
            }else{
                doInvalidStep(attr, it.key, longValue)
            }
        }catch(e: Exception){
            println("Could not parse to get next number")
        }
    }
}

/**
 * Do a step on a [attr] depending on [key] if up or down
 */
/**
 * This function is for valid values.
 * It increases / decreases (depending on [key]) the number ([valueAsText]) by the step size given in the
 * NumberValidator (optional) of the attribute.
 *
 * @param attr
 * @param key
 *
 */
@OptIn(ExperimentalComposeUiApi::class)
private fun doValidStep(attr: NumberAttribute<*,*,*>, key: Key){
    if (key == Key.DirectionUp) {
        attr.setValueAsText((attr.getValueAsText().toLong() + attr.validators.filterIsInstance<NumberValidator<*, *>>().first().getStepSize().toLong()).toString())
    }
    if (key == Key.DirectionDown) {
        attr.setValueAsText((attr.getValueAsText().toLong() - attr.validators.filterIsInstance<NumberValidator<*, *>>().first().getStepSize().toLong()).toString())
    }
}

/**
 * This function is for invalid values.
 * It increases / decreases (depending on [key]) the number ([valueAsText]) by the step size given in the
 * NumberValidator (optional) of the attribute.
 *
 * @param attr
 * @param key
 */
@OptIn(ExperimentalComposeUiApi::class)
private fun doInvalidStep(attr: NumberAttribute<*,*,*>, key: Key, longValue: Long){
    val stepStart = attr.validators.filterIsInstance<NumberValidator<*,*>>().first().getStepStart().toLong()
    val stepSize = attr.validators.filterIsInstance<NumberValidator<Int,*>>().first().getStepSize().toLong()
    val multiplier = (longValue - stepStart) / stepSize
    var res = multiplier * stepSize
    if(key == Key.DirectionUp && multiplier >= 0 && longValue !in (stepStart - stepSize) until stepStart){
        res += stepSize
    }else if(key == Key.DirectionDown && multiplier <= 0 && longValue !in stepStart until (stepStart + stepSize) ){
        res -= stepSize
    }
    attr.setValueAsText((res+stepStart).toString())
}