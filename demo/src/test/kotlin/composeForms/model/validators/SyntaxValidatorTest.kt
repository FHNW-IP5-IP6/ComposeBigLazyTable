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

import composeForms.model.Labels
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class SyntaxValidatorTest{

    @Test
    fun testSyntaxValidatorForDifferentTypes(){
        //given
        val intSyntaxValidator = SyntaxValidator<Int, Labels>()

        //then
        assertFalse(intSyntaxValidator.validateUserInput(0, "14.3").result)
        assertFalse(intSyntaxValidator.validateUserInput(0, "").result)
        assertTrue(intSyntaxValidator.validateUserInput(0, "14").result)
        assertTrue(intSyntaxValidator.validateUserInput(0, "-14").result)


        //given
        val stringSyntaxValidator = SyntaxValidator<String, Labels>()

        //then
        assertTrue(stringSyntaxValidator.validateUserInput("", "Test").result)
        assertTrue(stringSyntaxValidator.validateUserInput("", "").result)
        assertTrue(stringSyntaxValidator.validateUserInput("", "14").result)
        assertTrue(stringSyntaxValidator.validateUserInput("", "-14").result)

        //given
        val doubleSyntaxValidator = SyntaxValidator<Double, Labels>()

        //then
        assertTrue(doubleSyntaxValidator.validateUserInput(0.0, "4.23").result)
        assertTrue(doubleSyntaxValidator.validateUserInput(0.0, "-4.12").result)
        assertTrue(doubleSyntaxValidator.validateUserInput(0.0, "+14").result)
        assertFalse(doubleSyntaxValidator.validateUserInput(0.0, "").result)
        assertFalse(doubleSyntaxValidator.validateUserInput(0.0, "True").result)

    }

    @Test
    fun testValidationMessage(){
        //when
        val text = Labels.WRONGINPUT
        val syntaxValidator = SyntaxValidator<Int, Labels>(text)

        //then
        assertEquals(text, syntaxValidator.validationMessage)
        assertEquals(text, syntaxValidator.validateUserInput(0, "ABC").validationMessage)

        //when
        val defaultMsg = "This is not the correct input type"
        val defaultMsgSyntaxValidator = SyntaxValidator<Int, Labels>()

        //then
        assertEquals(defaultMsg, defaultMsgSyntaxValidator.getDefaultValidationMessage())
        assertEquals(defaultMsg, defaultMsgSyntaxValidator.validateUserInput(0, "ABC").defaultMessage)
        assertEquals(null, defaultMsgSyntaxValidator.validateUserInput(0, "ABC").validationMessage)

    }

}