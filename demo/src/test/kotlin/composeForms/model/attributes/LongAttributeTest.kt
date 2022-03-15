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

import composeForms.model.BaseModel
import composeForms.model.Labels

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class LongAttributeTest : NumberAttributeTest<Long>(){

    override fun provideAttribute(model: BaseModel<Labels>, value: Long?): Attribute<*, Any, *> {
        return LongAttribute(model, Labels.TEST, value, tableColumnWidth = 150.dp) as Attribute<*, Any, *>
    }
    override fun provideNumberAttribute(model: BaseModel<Labels>, value: Long?): NumberAttribute<*, Long, Labels> {
        return LongAttribute(model, Labels.TEST, value, tableColumnWidth = 150.dp)
    }

    init{
        validValue1Uneven        = 5
        validValue1AsText         = "5"

        validValue2        = 7
        validValue2AsText  = "7"

        validValue3        = 12
        validValue3AsText  = "12"

        validValue4        = 14
        validValue4AsText  = "14"

        notValidValueAsText     = "a"


        //For NumberAttribute
        upperBound          = 10
        lowerBoundWrong_becauseGreaterThanUpperBound    = 11
        lowerBoundCorrect   = 0

        lowerBound          = 10
        upperBoundWrong_becauseLowerThanLowerBound    = 9
        upperBoundCorrect   = 14

        stepSizeCorrect_even = 2
        stepSizeWrong_because0       = 0
        stepSizeWrong_becauseNegative       = -1

        notValidValueBecauseWrongStepAsText = "8"

        valueWithCorrectStepSize = (validValue1Uneven-stepSizeCorrect_even).toLong()
    }
}