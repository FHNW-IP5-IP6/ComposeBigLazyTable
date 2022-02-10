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

package composeForms.model.validators

import composeForms.model.ILabel
import composeForms.util.Utilities

/**
 * [SyntaxValidator] is an implementation of [Validator] for checking the type of a value. That the string is able to be
 * cast in any way to the type of value.
 *
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
class SyntaxValidator<T, L>(validationMessage           : L? = null)
        : Validator<T, L>(validationMessage = validationMessage)
        where L: ILabel, L: Enum<*> {

    //******************************************************************************************************
    //Validation

    override fun validateUserInput(value: T?, valueAsText: String?): ValidationResult<L> {
        //value is here typeT (only used for type checks)
        var isValid : Boolean
        var isRightTrack: Boolean
        try {
            Utilities<T>().toDataType(valueAsText!!, value!!)
            isValid = true
            isRightTrack = true
        }catch(e : IllegalArgumentException){
            isValid = false
            isRightTrack = value is Number && valueAsText == "-"
        }
        return ValidationResult(isValid, isRightTrack, validationMessage, defaultMessage = getDefaultValidationMessage() )
    }

    //******************************************************************************************************
    //Protected

    override fun getDefaultValidationMessage(): String {
        return "This is not the correct input type"
    }


}