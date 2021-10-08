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

package model.validators.semanticValidators

import model.ILabel
import model.attributes.SelectionAttribute
import model.validators.ValidationResult

/**
 * [SelectionValidator] is a SemanticValidator that checks the selected items that the count is within the bounds of
 * [minNumberOfSelections] and [maxNumberOfSelections]
 * @param minNumberOfSelections: minimum amount of selected values
 * @param maxNumberOfSelections: maximum amount of selected values
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
class SelectionValidator<L>(private var minNumberOfSelections  : Int = 0,
                            private var maxNumberOfSelections  : Int = Int.MAX_VALUE,
                            validationMessage                  : L? = null)

    : SemanticValidator<Set<L>, L>(validationMessage = validationMessage) where L: ILabel, L: Enum<*>{

    private var minNumberOfSelectionsCache : Int?       = minNumberOfSelections
    private var maxNumberOfSelectionsCache : Int?       = maxNumberOfSelections
    private var validationMessageCache     : L?    = validationMessage

    init{
        init()
    }

    /**
     * This method can be used to overwrite a SelectionValidator that has already been set.
     * Only parameter that are not null will overwrite the old values.
     * CheckDevValues() is called to check if the parameters make sense. If yes the values are set.
     * The default validation message is adapted if no validation message has been set by the developer.
     * Finally the existing user inputs are checked again to see if they are still valid.
     *
     * @param minNumberOfSelections
     * @param maxNumberOfSelections
     * @param validationMessage
     */
    fun overrideSelectionValidator(minNumberOfSelections: Int? = null, maxNumberOfSelections: Int? = null, validationMessage: L? = null){
        if(minNumberOfSelections != null){
            this.minNumberOfSelectionsCache = minNumberOfSelections
        }
        if(maxNumberOfSelections != null){
            this.maxNumberOfSelectionsCache = maxNumberOfSelections
        }
        if(validationMessage != null){
            this.validationMessageCache = validationMessage
        }
        checkAndSetDevValues()
        attributes.forEach{it.revalidate()}
    }

    //******************************************************************************************************
    //Validation

    override fun validateUserInput(value: Set<L>?, valueAsText: String?): ValidationResult<L> {
        val isValid = value!!.size in minNumberOfSelections..maxNumberOfSelections
        val rightTrackValid = value!!.size <= maxNumberOfSelections
        return ValidationResult(isValid, rightTrackValid, validationMessage, defaultMessage = getDefaultValidationMessage())
    }

    override fun checkAndSetDevValues() {
        checkMinMaxNumberOfSelection()
        checkMinMaxCompared()
        checkMinIsPossibleForAllAttribute()
        checkPossibleSelectionsChange()

        setValues()
        deleteCaches()
    }

    //******************************************************************************************************
    //Protected

    override fun getDefaultValidationMessage(): String {
        return "Between $minNumberOfSelections and $maxNumberOfSelections elements must be selected."
    }

    override fun setValues(){
        if(minNumberOfSelectionsCache != null){
            this.minNumberOfSelections = minNumberOfSelectionsCache!!
        }
        if(maxNumberOfSelectionsCache != null){
            this.maxNumberOfSelections = maxNumberOfSelectionsCache!!
        }
        if(validationMessageCache != null){
            this.validationMessage = validationMessageCache!!
        }
    }

    override fun deleteCaches(){
        this.minNumberOfSelectionsCache = null
        this.maxNumberOfSelectionsCache = null
        this.validationMessageCache = null
    }

    //******************************************************************************************************
    //Getter

    fun getMinNumberOfSelections() : Int {
        return minNumberOfSelections
    }

    fun getMaxNumberOfSelections() : Int {
        return maxNumberOfSelections
    }

    /**
     * Check cache of min and max values
     * @throws IllegalArgumentException
     */
    private fun checkMinMaxNumberOfSelection(){
        if(maxNumberOfSelectionsCache != null && maxNumberOfSelectionsCache!! < 1){
            deleteCaches()
            val e = IllegalArgumentException("MaxNumberOfSelections must at least 1")
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
        if(minNumberOfSelectionsCache != null && minNumberOfSelectionsCache!! < 0) {
            deleteCaches()
            val e = IllegalArgumentException("MinNumberOfSelections must be positive")
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
    }

    /**
     * Checks if min selection is lower than max selection.
     * @throws IllegalArgumentException
     */
    private fun checkMinMaxCompared(){
        if(    (minNumberOfSelectionsCache != null && maxNumberOfSelectionsCache != null && minNumberOfSelectionsCache!! > maxNumberOfSelectionsCache!!)
            || (minNumberOfSelectionsCache != null && maxNumberOfSelectionsCache == null && minNumberOfSelectionsCache!! > maxNumberOfSelections)
            || (minNumberOfSelectionsCache == null && maxNumberOfSelectionsCache != null && minNumberOfSelections > maxNumberOfSelectionsCache!!)){
            deleteCaches()
            val e = IllegalArgumentException("MinNumberOfSelections is higher than MaxNumberOfSelections")
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
    }

    /**
     * Checks if all attribute has equals or more possible selections.
     * @throws IllegalArgumentException
     */
    private fun checkMinIsPossibleForAllAttribute(){
        for(attr in attributes) {
            attr as SelectionAttribute
            if (minNumberOfSelectionsCache != null && minNumberOfSelectionsCache!! > attr.getPossibleSelections().flatten().size) {
                deleteCaches()
                val e = IllegalArgumentException("MinNumberOfSelections is higher than the number of possible elements to select")
                if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
                throw e
            }
        }
    }

    /**
     * Check if PossibleSelections of attribute have been changed
     * @throws IllegalArgumentException
     */
    private fun checkPossibleSelectionsChange(){
        if(minNumberOfSelectionsCache == null && maxNumberOfSelectionsCache == null && validationMessageCache == null){
            for(attr in attributes) {
                attr as SelectionAttribute
                if ( minNumberOfSelections > attr.getPossibleSelections().flatten().size) {
                    val e = IllegalArgumentException("MinNumberOfSelections is now higher than the number of possible elements to select! Change minNumberOfSelection or add an element to possibleSelections again.")
                    if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
                    throw e
                }
            }
        }
    }
}