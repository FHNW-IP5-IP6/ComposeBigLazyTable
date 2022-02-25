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

import composeForms.convertibles.CustomConvertible
import composeForms.model.ILabel
import composeForms.model.IModel
import composeForms.model.formatter.IFormatter
import composeForms.model.meanings.Default
import composeForms.model.meanings.SemanticMeaning
import composeForms.model.validators.semanticValidators.SelectionValidator
import composeForms.model.validators.semanticValidators.SemanticValidator
import org.jetbrains.exposed.sql.Column

/**
 * The [SelectionAttribute] is the implementation of type set of Labels.
 * The value contains selections of the possible selections list that can be chosen.
 *
 * @param model: the composeForms.model that the attribute belongs to
 * @param label: [L] (ILable enum entry) where a string for each language can be defined.
 * @param possibleSelections: List[L] with possibilities that has to be chosen from
 * @param value: initial value
 * @param required: if a value is required
 * @param readOnly: if the value is read only or writeable
 * @param observedAttributes: List of functions that are executed if the values of the observed attributes change.
 * @param validators: List of [SemanticValidator]s that are used for the validation of the user input ([valueAsText]).
 * @param convertibles: List of [CustomConvertible]s that are used to convert a not type-matching [valueAsText] (String)
 * into a type-matching String (that can be converted into the type [T] of the attribute).
 * @param meaning: [SemanticMeaning] used to add a meaning to the value
 * @param formatter: [IFormatter] that formats the value into a different view
 *
 * @author Louisa Reinger, Steve Vogel
 */
class SelectionAttribute<L>(
    //required parameters
    model               : IModel<L>,
    label               : L,
    private var possibleSelections  : List<L>,

    //optional parameters
    value               : Set<L>                                = emptySet(),
    required            : Boolean                               = false,
    readOnly            : Boolean                               = false,
    observedAttributes  : List<(Attribute<*,*,*>) -> Unit>      = emptyList(),
    validators          : List<SelectionValidator<L>>           = mutableListOf(),
    convertibles        : List<CustomConvertible>               = emptyList(),
    meaning             : SemanticMeaning<Set<L>>               = Default(),
    formatter           : IFormatter<Set<L>>                    = IFormatter{
        it?.map{ it.getLanguageStringFromLabel(it, model.getCurrentLanguage())}.toString().removePrefix("[").removeSuffix("]")
    },

    canBeFiltered       : Boolean                               = true,
    databaseField       : Column<*>?                            = null

) : Attribute<SelectionAttribute<L>, Set<L>, L>(
    model = model,
    value = value,
    label = label,
    required = required,
    readOnly = readOnly,
    observedAttributes = observedAttributes,
    validators = validators,
    convertibles = convertibles,
    meaning = meaning,
    formatter = formatter,
    canBeFiltered = canBeFiltered,
    databaseField = databaseField
) where L : ILabel, L: Enum<*> {

    override val typeT: Set<L>
        get() = setOf()

    //******************************************************************************************************
    //Functions that are called on user actions

    /**
     * This function checks if the value is in the set of possible selections.
     * If yes, it creates a new user set containing all the values already selected by the user plus the new value.
     * The newly formed set is then passed to the setValueAsText function.
     * @param value : String
     * @throws IllegalArgumentException : If element is not a possible selection
     */
    fun addUserSelection(value: Any?){
        if(possibleSelections.contains(value)){
            val newSet = convertStringToType(getValueAsText()).toMutableSet()
            newSet.add(value as L)
            setValueAsText(newSet.toString())
        }
        else {
            setValueAsText(getValue().toString())
            val e = IllegalArgumentException("There was no such selection to choose")
            getModel().setException(e)
            throw e
        }
    }


    /**
     * This function creates a new user set containing all the values already selected by the user minus the new value.
     * The newly formed set is then passed to the setValueAsText function.
     * @param value : String
     */
    fun removeUserSelection(value: Any?){
        val newSet : MutableSet<L> = convertStringToType(getValueAsText()).toMutableSet()
        newSet.remove(value)
        setValueAsText(newSet.toString())
    }

    //******************************************************************************************************
    //Setter

    /**
     * This method turns the passed list into a set and checks if the set of selections is not empty.
     * Further the attribute's validators are checked if there are limits like minNumberOfSelections that make no sense anymore.
     * If so, the possibleSelections-set is set and the current textValue is checked to see if it is still valid.
     *
     * @param selections : List<L>
     * @throws IllegalArgumentException
     */
    fun setPossibleSelections(selections : List<L>){
        val uniqueSelections = selections.toSet()
        if(uniqueSelections.isNotEmpty()){
            this.possibleSelections = uniqueSelections.toList()
            validators.forEach{it.checkAndSetDevValues()}
            checkAndSetValue(getValue().toString())
        }else{
            val e = IllegalArgumentException("There are no selections in the set")
            getModel().setException(e)
            throw e
        }
    }

    /**
     * This method adds a new selection to the possibleSelections-set.
     * @param selection : L
     */
    fun addANewPossibleSelection(selection: L){
        possibleSelections = possibleSelections + listOf(selection)
    }

    /**
     * This method deletes a selection of the possibleSelections-set.
     * The attribute's validators are checked if there are limits like minNumberOfSelections that make no sense anymore.
     * It is also checked whether this element is already selected by the user.
     * If so, it is removed from the user value list and checked whether the newly created user value list is still valid.
     *
     * @param selection : L
     */
    fun removeAPossibleSelection(selection: L){
        if(possibleSelections.contains(selection)){
            val mutableList = possibleSelections.toMutableList()
            mutableList.remove(selection)
            this.possibleSelections = mutableList.toList()
            validators.forEach{it.checkAndSetDevValues()}
            if(getValue()!!.contains(selection)){
                removeUserSelection(selection)
                checkAndSetValue(getValue().toString())
            }
        }
    }

    override fun convertStringToType(newValAsText: String): Set<L>{
        try {
            var list: List<String> = emptyList()
            if (newValAsText.length > 1) {
                val str = newValAsText.removePrefix("[").removeSuffix("]")
                list = str.split(",")
            }
            val listConverted: List<L?> = list.map { string -> possibleSelections.find { it.name == string.trim() } }
            val setConvertedNonNull: Set<L> = listConverted.filterNotNull().toSet()
            return setConvertedNonNull
        }catch(e: NullPointerException){
            e.printStackTrace()
            return emptySet()
        }
    }

    //******************************************************************************************************
    //Public Getter

    override fun getPossibleSelections(): List<Set<L>> {
        return listOf(possibleSelections.toSet())
    }

}