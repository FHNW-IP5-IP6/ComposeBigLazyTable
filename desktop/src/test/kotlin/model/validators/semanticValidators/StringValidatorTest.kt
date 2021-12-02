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

import model.Labels
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class StringValidatorTest{

    @Test
    fun testStringValidation(){
        //given
        val validator = StringValidator<Labels>(minLength = 3, maxLength = 5)
        //then
        assertFalse(validator.validateUserInput("", "").result)
        assertFalse(validator.validateUserInput("12", "12").result)
        assertTrue(validator.validateUserInput("123", "123").result)
        assertTrue(validator.validateUserInput("1234", "1234").result)
        assertTrue(validator.validateUserInput("12345", "12345").result)
        assertFalse(validator.validateUserInput("123456", "123456").result)
    }


    @Test
    fun testOverrideValidator(){
        //given
        val validator = StringValidator<Labels>()

        //when
        validator.overrideStringValidator(minLength = 3)
        //then
        assertFalse(validator.validateUserInput("12", "12").result)
        assertTrue(validator.validateUserInput("123", "123").result)
        assertTrue(validator.validateUserInput("1234", "1234").result)

        //when
        validator.overrideStringValidator(maxLength = 5)

        //then
        assertFalse(validator.validateUserInput("", "").result)
        assertFalse(validator.validateUserInput("12", "12").result)
        assertTrue(validator.validateUserInput("123", "123").result)
        assertTrue(validator.validateUserInput("1234", "1234").result)
        assertTrue(validator.validateUserInput("12345", "12345").result)
        assertFalse(validator.validateUserInput("123456", "123456").result)

    }


    @Test
    fun testGetter(){
        //given
        val validator = StringValidator<Labels>()

        //then
        assertEquals(0, validator.getMinLength())
        assertEquals(1_000_000, validator.getMaxLength())


        //when
        validator.overrideStringValidator(2,4)

        //then
        assertEquals(2, validator.getMinLength())
        assertEquals(4, validator.getMaxLength())
    }


}