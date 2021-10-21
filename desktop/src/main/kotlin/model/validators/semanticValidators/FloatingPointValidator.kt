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
import model.validators.ValidationResult

/**
 * [FloatingPointValidator] is a [SemanticValidator] to check floating point numbers to have a maximum digits after the
 * . (point) in the number.
 *
 * @param decimalPlaces: maximal decimal places for the number
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
class FloatingPointValidator<T, L>(private var decimalPlaces   : Int = 10,
                                   validationMessage           : L? = null

) : SemanticValidator<T, L>(validationMessage = validationMessage) where T : Number, T : Comparable<T>, L: ILabel, L: Enum<*>  {

    private var decimalPlacesCache      : Int?      = decimalPlaces
    private var validationMessageCache  : L?   = validationMessage

    init {
        init()
    }

    /**
     * This method can be used to overwrite a FloatingPointValidator that has already been set.
     * Only parameter that are not null will overwrite the old values.
     * CheckDevValues() is called to check if the parameters make sense. If yes the values are set.
     * The default validation message is adapted if no validation message has been set by the developer.
     * Finally the existing user inputs are checked again to see if they are still valid.
     *
     * @param decimalPlaces
     * @param validationMessage
     */
    fun overrideFloatingPointValidator(decimalPlaces: Int? = null, validationMessage: L? = null){
        if(decimalPlaces != null){
            this.decimalPlacesCache = decimalPlaces
        }
        if(validationMessage != null){
            this.validationMessageCache = validationMessage
        }
        checkAndSetDevValues()
        attributes.forEach{it.revalidate()}
    }

    //******************************************************************************************************
    //Validation

    override fun validateUserInput(value: T?, valueAsText : String?): ValidationResult<L> {
        val splittedNumber = valueAsText!!.split(".")
        val isValid = if(splittedNumber.size == 2) splittedNumber[1].length <= decimalPlaces else splittedNumber.size == 1
        val rightTrackValid = isValid

        return ValidationResult(isValid, rightTrackValid, validationMessage, defaultMessage = getDefaultValidationMessage())
    }

    override fun checkAndSetDevValues() {
        if(decimalPlacesCache != null && decimalPlacesCache!! < 1){
            deleteCaches()
            val e =  IllegalArgumentException("number of decimal places must be positive")
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
        setValues()
        deleteCaches()
    }

    //******************************************************************************************************
    //Protected

    override fun getDefaultValidationMessage(): String {
        return "Too many decimal places"
    }

    override fun setValues(){
        if(decimalPlacesCache != null){
            this.decimalPlaces = decimalPlacesCache!!
        }
        if(validationMessageCache != null){
            this.validationMessage = validationMessageCache!!
        }
    }

    override fun deleteCaches(){
        this.decimalPlacesCache = null
        this.validationMessageCache = null
    }

    //******************************************************************************************************
    //Getter

    fun getDecimalPlaces() : Int {
        return decimalPlaces
    }

}