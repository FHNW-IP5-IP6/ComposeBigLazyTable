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

package model.validators

import model.ILabel

/**
 * [RequiredValidator] is a [Validator] that checks the value if it is empty or not
 *
 * @param isRequired: if the value has to be set (not null and not empty)
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
class RequiredValidator<T, L>(private var isRequired           : Boolean = false,
                              validationMessage                : L? = null)
    : Validator<T, L>(validationMessage = validationMessage) where L: ILabel, L: Enum<*>{

    /**
     * This method can be used to overwrite a RequiredValidator that has already been set.
     * Only parameters that are not null will overwrite old values.
     * The existing user inputs are checked again to see if they are still valid.
     *
     * @param isRequired
     * @param validationMessage
     */
    fun overrideRequiredValidator(isRequired: Boolean? = null, validationMessage: L? = null){
        if(isRequired != null){
            this.isRequired = isRequired
        }
        if(validationMessage != null){
            this.validationMessage = validationMessage
        }
        attributes.forEach{it.revalidate()}
    }

    //******************************************************************************************************
    //Validation

    override fun validateUserInput(value: T?, valueAsText : String?): ValidationResult<L> {
        val isValid : Boolean
        if(isRequired){
            isValid = (value != null && value != emptySet<String>() && value != "")
        }else{
            isValid = true
        }
        return ValidationResult(result = isValid, rightTrackResult = true, validationMessage = validationMessage, defaultMessage = getDefaultValidationMessage())
    }

    //******************************************************************************************************
    //Protected

    override fun getDefaultValidationMessage(): String {
        return "Input required"
    }

    //******************************************************************************************************
    //Getter

    fun isRequired() : Boolean{
        return isRequired
    }

}