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

package composeForms.model.validators.semanticValidators

import composeForms.model.ILabel
import composeForms.model.validators.ValidationResult

/**
 * [RegexValidator] is a semantic validator that checks the input according to [regexPattern]. The [rightTrackRegexPattern]
 * is used to check for a pattern that describes a value that is going to be correct.
 *
 * @param regexPattern: String pattern representing the regex pattern
 * @param rightTrackRegexPattern: String pattern representing the regex for right track
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
class RegexValidator<T, L>(private var regexPattern       : String,
                           private var rightTrackRegexPattern: String? = null,
                           validationMessage                 : L? = null)

    : SemanticValidator<T, L>(validationMessage = validationMessage) where L: ILabel, L: Enum<*>{

    init{
        init()
    }

    /**
     * Overrides the properties with the given non null values
     *
     * @param regexPattern
     * @param rightTrackRegexPattern
     * @param validationMessage
     */
    fun overrideRegexValidator(regexPattern: String? = null, rightTrackRegexPattern: String? = null, validationMessage: L? = null){
        if(regexPattern != null){
            this.regexPattern = regexPattern
        }
        if(rightTrackRegexPattern != null){
            this.rightTrackRegexPattern = rightTrackRegexPattern
        }
        if(validationMessage != null){
            this.validationMessage = validationMessage
        }
        attributes.forEach{it.revalidate()}
    }

    //******************************************************************************************************
    //Validation

    override fun validateUserInput(value: T?, valueAsText : String?): ValidationResult<L> {
        val regex = try {
            regexPattern.toRegex()
        }catch(e : Exception){
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
        val isValid = if(valueAsText != null) regex.matches(valueAsText) else regex.matches("")

        val isValidSoft = if(rightTrackRegexPattern != null) {
            val softRegex = rightTrackRegexPattern!!.toRegex()
            if (valueAsText != null) softRegex.matches(valueAsText) else regex.matches("")
        }else{
            regexOnRightTrackChecker(isValid, valueAsText)
        }
        return ValidationResult(result = isValid, rightTrackResult = isValidSoft, validationMessage = validationMessage, defaultMessage = getDefaultValidationMessage())
    }

    /**
     * Check if the valueAsText is a sub element of the regex pattern, starting from the left side and increasing pattern
     * length.
     * @param isValid : Boolean if the full pattern check was valid. If true then the function immediately returns true
     * @param valueAsText : String that will be checked against the sub patterns
     */
    private fun regexOnRightTrackChecker(isValid: Boolean, valueAsText: String?): Boolean {
        if (isValid) {
            return true
        } else {
            //try to go through regexString till all values are checked
            var tempString = ""
            for (char in regexPattern) {
                tempString += char
                try {
                    val tempRegex = tempString.toRegex()
                    val tempResult = tempRegex.matches(valueAsText ?: "")

                    if (tempResult) {
                        return true
                    }
                } catch (e: Exception) {
                    println("Regex not working")
                }
            }
        }
        return false
    }

    override fun checkAndSetDevValues(){
        //There is no check for regex and therefore it will be set directly
    }

    //******************************************************************************************************
    //Protected

    override fun getDefaultValidationMessage(): String{
        return "Pattern does not match to $regexPattern"
    }

    override fun setValues() {
        //There is no check for regex and therefore it will be set directly
    }

    override fun deleteCaches() {
        //There is no check for regex and therefore there is no cache
    }


    //******************************************************************************************************
    //Getter

    fun getRegexPattern() : String {
        return regexPattern
    }

}

