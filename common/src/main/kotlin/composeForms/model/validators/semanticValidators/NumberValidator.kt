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
import composeForms.util.Utilities
import java.lang.Math.abs

/**
 * [NumberValidator] is a SemanticValidator that checks the number to be withing the bounds of [lowerBound] and
 * [upperBound], as well as the value is [stepSize] more or less than the [stepStart] if [onlyStepValuesAreValid] is true
 *
 * @param lowerBound: minimum number value (including)
 * @param upperBound: maximum number value (including)
 * @param stepSize: how big one step is
 * @param stepStart: from where starting the steps
 * @param onlyStepValuesAreValid: flag if only steps of step size is allowed
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
class NumberValidator<T, L>(private var lowerBound              : T? = null,
                            private var upperBound              : T? = null,
                            private var stepSize                : T? = null,
                            private var stepStart               : T? = null,
                            private var onlyStepValuesAreValid  : Boolean = false,
                            validationMessage                   : L? = null

    ) : SemanticValidator<T, L>(validationMessage = validationMessage) where T : Number, T : Comparable<T>, L: ILabel, L: Enum<*>{

    private var lowerBoundCache             : T?        = lowerBound
    private var upperBoundCache             : T?        = upperBound
    private var stepSizeCache               : T?        = stepSize
    private var stepStartCache              : T?        = stepStart
    private var onlyStepValuesAreValidCache : Boolean?  = onlyStepValuesAreValid
    private var validationMessageCache      : L?   = validationMessage

    private lateinit var typeT : T

    init {
        initializeTypeT()
        initializeLowerAndUpperBound(lowerBound = lowerBound, upperBound = upperBound)
        initializeStepStartAndSize(stepSize = stepSize, stepStart = stepStart)
        init()
    }

    /**
     * This method can be used to overwrite a NumberValidator that has already been set.
     * Only parameter that are not null will overwrite the old values.
     * CheckDevValues() is called to check if the parameters make sense. If yes the values are set.
     * The default validation message is adapted if no validation message has been set by the developer.
     * Finally the existing user inputs are checked again to see if they are still valid.
     *
     * @param lowerBound
     * @param upperBound
     * @param stepSize
     * @param stepStart
     * @param onlyStepValuesAreValid
     * @param validationMessage
     */
    fun overrideNumberValidator(lowerBound: T? = null, upperBound: T? = null, stepSize: T? = null,
                                stepStart: T? = null, onlyStepValuesAreValid: Boolean? = null, validationMessage: L? = null){
        if(lowerBound != null){
            this.lowerBoundCache = lowerBound
        }
        if(upperBound != null){
            this.upperBoundCache = upperBound
        }
        if(stepSize != null){
            this.stepSizeCache = stepSize
        }
        if(stepStart != null){
            this.stepStartCache = stepStart
        }
        if(onlyStepValuesAreValid != null){
            this.onlyStepValuesAreValidCache = onlyStepValuesAreValid
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
        var isValid = value!! in lowerBound!!..upperBound!!
        var rightTrackValid = false
        if(isValid){
            rightTrackValid = true
        }else if(upperBound!!.toDouble() < 0.0 && lowerBound!!.toDouble() < 0.0) { // case if both bounds are negative numbers
            rightTrackValid = value!!.toDouble() < 0.0 && value!! >= lowerBound!! && ( value!!.toString().length < upperBound!!.toString().length || value!!.toDouble() * (10.0) >= lowerBound!!.toDouble())
        }else if(upperBound!!.toDouble() >= 0.0 && lowerBound!!.toDouble() >= 0.0) {
            rightTrackValid = value!!.toDouble() > 0.0 && value!! <= upperBound!! && ( value!!.toString().length < lowerBound!!.toString().length || value!!.toDouble() * (10.0) <= upperBound!!.toDouble())
        }else{
            if(value!!.toDouble() >= 0.0){
                rightTrackValid = value!! <= upperBound!!
            }else{
                rightTrackValid = value!! >= lowerBound!!
            }
        }

        if(onlyStepValuesAreValid && isValid){
            isValid = isOnStepSize(value)
        }

        return ValidationResult(isValid, rightTrackValid, validationMessage, defaultMessage = getDefaultValidationMessage())
    }

    /**
     *
     */
    fun isOnStepSize(value: T): Boolean{
        val epsilon = 0.00000001
        var dif = 0.0
        if(value.toDouble() < 0){
            dif = -(value.toDouble() * 2)
        }
        val moduloOfNewVal      = ((value.toDouble() + dif +(epsilon/10)) %  stepSize!!.toDouble())
        val moduloOfStepStart   = abs(((stepStart!!.toDouble() + dif +(epsilon/10)) % stepSize!!.toDouble()))

        return moduloOfNewVal in moduloOfStepStart-epsilon..moduloOfStepStart+epsilon

    }

    override fun checkAndSetDevValues() {
        if(    (lowerBoundCache != null && upperBoundCache != null && lowerBoundCache!! > upperBoundCache!!)
            || (lowerBoundCache != null && upperBoundCache == null && lowerBoundCache!! > upperBound!!)
            || (lowerBoundCache == null && upperBoundCache != null && lowerBound!! > upperBoundCache!!)){
            deleteCaches()
            val e = IllegalArgumentException("LowerBound is greater than upperBound")
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
        if(stepSizeCache != null && stepSizeCache!! <= Utilities<T>().toDataType("0", typeT)){
            deleteCaches()
            val e = IllegalArgumentException("stepSize must be positive")
            if(attributes.isNotEmpty())attributes.first().getModel().setException(e)
            throw e
        }
        setValues()
        deleteCaches()
    }

    //******************************************************************************************************
    //Protected

    override fun getDefaultValidationMessage(): String {
            var firstPart = ""
            var secondPart = ""
            if(lowerBound != Utilities<T>().getMinValueOfNumberTypeT(typeT) && upperBound != Utilities<T>().getMaxValueOfNumber(typeT)){
                firstPart = "The number must be between " + lowerBound + " and " + upperBound + ". "
            }
            else if(lowerBound != Utilities<T>().getMinValueOfNumberTypeT(typeT)){
                firstPart = "The number must be at least " + lowerBound + ". "
            }
            else if(upperBound != Utilities<T>().getMaxValueOfNumber(typeT)){
                firstPart = "The number must not be more than " + upperBound + ". "
            }

            if(onlyStepValuesAreValid){
                secondPart = "Only steps of " + stepSize + " are permitted, starting at " + stepStart + ". "
            }

            return firstPart + secondPart
    }

    override fun setValues(){
        if(lowerBoundCache != null){
            this.lowerBound = lowerBoundCache
        }
        if(upperBoundCache != null){
            this.upperBound = upperBoundCache
        }
        if(stepSizeCache != null){
            this.stepSize = stepSizeCache
        }
        if(stepStartCache != null){
            this.stepStart = stepStartCache
        }
        if(onlyStepValuesAreValidCache != null){
            this.onlyStepValuesAreValid = onlyStepValuesAreValidCache!!
        }
        if(validationMessageCache != null){
            this.validationMessage = validationMessageCache!!
        }
    }

    override fun deleteCaches(){
        this.lowerBoundCache = null
        this.upperBoundCache = null
        this.stepSizeCache = null
        this.stepStartCache = null
        this.onlyStepValuesAreValidCache = null
        this.validationMessageCache = null
    }


    //******************************************************************************************************
    //Extra functions to initialize lower/upperBound

    /**
     *
     */
    private fun initializeTypeT(){
        if(lowerBound != null){
            typeT = lowerBound!!
        }
        else if(upperBound != null){
            typeT = upperBound!!
        }
        else if(stepStart != null){
            typeT = stepStart!!
        }
        else if(stepSize != null){
            typeT = stepSize!!
        }
    }

    /**
     *
     */
    private fun initializeStepStartAndSize(stepSize : T?, stepStart : T?){
        if(stepSize === null){
            this.stepSize = Utilities<T>().toDataType("1", typeT)
        }
        if(stepStart === null){
            this.stepStart = Utilities<T>().toDataType("0", typeT)
        }
    }


    /**
     * If no values have been passed for lower/upperBound,
     * default values are set for them depending on the type of the NumberAttribute.
     */
    private fun initializeLowerAndUpperBound(lowerBound: T?, upperBound : T?){
        if(lowerBound === null){
            this.lowerBound = Utilities<T>().getMinValueOfNumberTypeT(typeT)
        }else{
            this.lowerBound = lowerBound
        }

        if(upperBound === null){
            this.upperBound = Utilities<T>().getMaxValueOfNumber(typeT)
        }else{
            this.upperBound = upperBound
        }
    }

    //******************************************************************************************************
    //Getter

    fun getLowerBound() : T {
        return lowerBound!!
    }

    fun getUpperBound() : T {
        return upperBound!!
    }

    fun getStepSize() : T {
        return stepSize!!
    }

    fun getStepStart() : T {
        return stepStart!!
    }

    fun isOnlyStepValuesAreValid() : Boolean {
        return onlyStepValuesAreValid
    }
}