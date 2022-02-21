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

package composeForms.model.attributes

import composeForms.model.ILabel
import composeForms.model.IModel
import composeForms.model.formatter.IFormatter
import composeForms.model.meanings.SemanticMeaning
import composeForms.model.validators.ValidatorType
import composeForms.model.validators.semanticValidators.CustomValidator

/**
 * The [DualAttribute] is an abstract attribute. The value contains one of two decisions that can be chosen.
 *
 * @param model: the composeForms.model that the attribute belongs to
 * @param label: [L] (ILable enum entry) where a string for each language can be defined.
 * @param decision1Text: [L] for the first value
 * @param decision2Text: [L] for the second value
 * @param decision1SaveValue: Value that will be saved when first is selected
 * @param decision2SaveValue: Value that will be saved when second is selected
 * @param value: initial value
 * @param readOnly: if the value is read only or writeable
 * @param observedAttributes: List of functions that are executed if the values of the observed attributes change.
 * @param meaning: [SemanticMeaning] used to add a meaning to the value

 * @author Louisa Reinger, Steve Vogel
 */
abstract class DualAttribute<D,T,L>(
    //required parameters
    model                               : IModel<L>,
    label                               : L,
    val decision1Text                   : L,
    val decision2Text                   : L,
    val decision1SaveValue              : T,
    val decision2SaveValue              : T,

    //optional parameters
    value                               : T,
    readOnly                            : Boolean,
    observedAttributes                  : List<(a: Attribute<*, *, *>) -> Unit>,
    meaning                             : SemanticMeaning<T>

) : Attribute<DualAttribute<D, T, L>, T, L>(
    model = model,
    value = value,
    label = label,
    required = false,
    readOnly = readOnly,
    observedAttributes = observedAttributes,
    validators = listOf(
        object :
            CustomValidator<T, L>(validationMessage = model.getValidationMessageOfNonSemanticValidator(ValidatorType.DUALVALIDATOR),
                validationFunction = {
                    it == decision1SaveValue || it == decision2SaveValue
                }) {
            override fun getDefaultValidationMessage(): String {
                return "You must choose one of the two options given."
            }
        }
    ),
    convertibles = emptyList(),
    meaning = meaning,
    formatter = IFormatter { it.toString() },
    canBeFiltered = false,
    databaseField = null
) where D : DualAttribute<D, T, L>, L : ILabel, L: Enum<*> {


    init{
        checkDevValues()
    }


    /**
     * This function checks if the value matches one of the two possible choices.
     * If yes, it calls setValueAsText with the currently not selected choice to select it.
     */
    fun changeDecision(){
        if(getValue() === null || getValue()!! != decision1SaveValue && getValue()!! != decision2SaveValue){
            val e = IllegalArgumentException("Value must match one of the two possible choices")
            getModel().setException(e)
            throw e
        }
        else if(getValue()!! == decision1SaveValue){
            setValueAsText(decision2SaveValue.toString())
        }else if(getValue()!! == decision2SaveValue){
            setValueAsText(decision1SaveValue.toString())
        }
    }

    //******************************+
    //Internal functions

    /**
     * Function that checks if the given initial value is one of the two values that can be selected.
     * This function is necessary because the syntax validator only checks the type, not the concrete choices.
     *
     * @throws IllegalArgumentException
     */
    private fun checkDevValues(){
        if(getValue() != decision1SaveValue && getValue() != decision2SaveValue){
            val e = IllegalArgumentException("Default value is not in the possible values")
            getModel().setException(e)
            throw e
        }
    }

    //******************************+
    //Getter
    /**
     * This method returns a set containing the displayed and stored decision values.
     * @return Set of values ( The first two are the displayed values, the last two are the stored values.)
     */
    override fun getPossibleSelections(): List<T> {
        val d1T = decision1Text.getLanguageStringFromLabel(decision1Text, getModel().getCurrentLanguage())
        val d2T = decision2Text.getLanguageStringFromLabel(decision2Text, getModel().getCurrentLanguage())
        return listOf(d1T as T, d2T as T, decision1SaveValue, decision2SaveValue)
    }
}