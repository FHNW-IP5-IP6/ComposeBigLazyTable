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
import composeForms.model.attributes.Attribute

/**
 * [Validator] is for validating values and string representation of values. Providing a [validationMessage] to
 * personalize the message for the form user.
 *
 * @param validationMessage: [L] for specific message
 *
 * @author Louisa Reinger, Steve Vogel
 */
abstract class Validator<T, L>(var validationMessage : L?) where L: ILabel, L: Enum<*>  {

    protected val attributes : MutableList<Attribute<*, *, *>> = mutableListOf()

    /**
     * This method adds an attribute to the validator.
     */
    fun addAttribute(attr : Attribute<*, *, *>){
        attributes.add(attr)
    }

    /**
     * This method checks if the user input is valid regarding the limits in the Validator.
     *
     * @param value
     * @param valueAsText
     * @return [ValidationResult]
     */
    abstract fun validateUserInput(value : T?, valueAsText : String?) : ValidationResult<L>


    /**
     * This method sets a default validation message depending on the set limits in the validator
     * if no validation message has been set by the developer.
     */
    abstract fun getDefaultValidationMessage(): String



}