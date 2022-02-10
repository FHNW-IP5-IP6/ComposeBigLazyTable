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

import composeForms.model.Labels
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class RegexValidatorTest {

    @Test
    fun overrideRegexValidator() {
        //given
        val pattern = "AbC"
        val regexValidator = RegexValidator<String, Labels>(pattern)

        //when
        val overWritePattern = "Test"
        regexValidator.overrideRegexValidator(overWritePattern)

        //then
        assertEquals(overWritePattern, regexValidator.getRegexPattern())


        //given
        val pattern1 = "AbC"
        val pattern2 = "hallo"
        val regexValidator1 = RegexValidator<String, Labels>(pattern1, pattern2)

        //when
        val overWritePattern1 = "Test"
        regexValidator1.overrideRegexValidator(overWritePattern1)

        //then
        assertEquals(overWritePattern1, regexValidator1.getRegexPattern())
    }

    @Test
    fun testRightTrackValidation(){
        val pattern = "abc"
        val regexValidator = RegexValidator<String, Labels>(pattern)

        assertFalse(regexValidator.validateUserInput("", "ab").result)
        assertTrue(regexValidator.validateUserInput("", "ab").rightTrackResult)


        val pattern1 = "hal*o"
        val regexValidator1 = RegexValidator<String, Labels>(pattern1)

        assertFalse(regexValidator1.validateUserInput("", "halllll").result)
        assertTrue(regexValidator1.validateUserInput("", "halllll").rightTrackResult)


        val pattern2 = "te(s|ss)t"
        val regexValidator2 = RegexValidator<String, Labels>(pattern2)

        assertFalse(regexValidator2.validateUserInput("", "tess").result)
        assertTrue(regexValidator2.validateUserInput("", "tess").rightTrackResult)
    }

    @Test
    fun validateUserInput() {
        //given
        val pattern = "AbC"
        val regexValidator = RegexValidator<String, Labels>(pattern)
        val validationMsg = regexValidator.validateUserInput(null, "hallo").validationMessage

        //then
        assertTrue(regexValidator.validateUserInput(null, "AbC").result)
        assertTrue(regexValidator.validateUserInput(null, "AbC").rightTrackResult)
        assertFalse(regexValidator.validateUserInput(null, "abc").result)
        assertFalse(regexValidator.validateUserInput(null, "").result)
        assertFalse(regexValidator.validateUserInput(null, "ABC").result)
        assertFalse(regexValidator.validateUserInput(null, "abcabc").result)
        assertEquals(null, regexValidator.validateUserInput(null, "hallo").validationMessage)
        assertEquals("Pattern does not match to AbC", regexValidator.validateUserInput(null, "hallo").defaultMessage)
    }

    @Test
    fun validateUserInputRightTrack(){
        val pattern = "AbC"
        val rightTrackPattern = "Ab"
        val regexValidator = RegexValidator<String, Labels>(pattern, rightTrackPattern)

        //then
        assertFalse(regexValidator.validateUserInput(null, "Ab").result)
        assertTrue(regexValidator.validateUserInput(null, "Ab").rightTrackResult)
    }

    @Test
    fun getRegexPattern() {
        //given
        val pattern = "AbC"
        val regexValidator = RegexValidator<String, Labels>(pattern)

        //then
        assertEquals(pattern, regexValidator.getRegexPattern())

        //given
        val pattern1 = "AbC"
        val pattern2 = "hallo"
        val regexValidator1 = RegexValidator<String, Labels>(pattern1, pattern2)

        //then
        assertEquals(pattern1, regexValidator1.getRegexPattern())
    }
}