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
internal class CustomValidatorTest {

    @Test
    fun overrideCustomValidator() {
        //given
        val f : (String?) -> Boolean = {it!!.length > 3}
        val f2: (String?) -> Boolean = {it!!.length > 1}
        val msg = Labels.CUSTOMVALIDATORMSG
        val msg1 = Labels.CUSTOMVALIDATORMSG1

        //when
        val cv = CustomValidator(f, validationMessage = msg)
        cv.overrideCustomValidator(f2)

        //then
        assertTrue(cv.validateUserInput("Hallo", "Hallo").result)
        assertTrue(cv.validateUserInput("Hallo", "Hallo").rightTrackResult)
        assertTrue(cv.validateUserInput("Ha", "Ha").result)
        assertTrue(cv.validateUserInput("Ha", "Ha").rightTrackResult)

        cv.overrideCustomValidator(null, validationMessage = msg1)

        //then
        assertTrue(cv.validateUserInput("Hallo", "Hallo").result)
        assertTrue(cv.validateUserInput("Hallo", "Hallo").rightTrackResult)
        assertTrue(cv.validateUserInput("Ha", "Ha").result)
        assertTrue(cv.validateUserInput("Ha", "Ha").rightTrackResult)
        assertEquals(msg1, cv.validateUserInput("H", "H").validationMessage)
    }

    @Test
    fun validateUserInput() {
        //given
        val f : (String?) -> Boolean = {it!!.length > 3}
        val msg = Labels.CUSTOMVALIDATORMSG

        //when
        val cv = CustomValidator(f, validationMessage = msg)

        //then
        assertTrue(cv.validateUserInput("Hallo", "Hallo").result)
        assertTrue(cv.validateUserInput("Hallo", "Hallo").rightTrackResult)
        assertFalse(cv.validateUserInput("Ha", "Ha").result)
        assertFalse(cv.validateUserInput("Ha", "Ha").rightTrackResult)


        //when
        val cv2 = CustomValidator(f, null, msg)

        //then
        assertTrue(cv2.validateUserInput("Hallo", "Hallo").result)
        assertTrue(cv2.validateUserInput("Hallo", "Hallo").rightTrackResult)
        assertFalse(cv2.validateUserInput("Ha", "Ha").result)
        assertFalse(cv2.validateUserInput("Ha", "Ha").rightTrackResult)

        //given
        val f3 : (String?) -> Boolean = {it!!.length > 1}

        //when
        val cv3 = CustomValidator(f, f3,validationMessage = msg)

        //then
        assertTrue(cv3.validateUserInput("Hallo", "Hallo").result)
        assertTrue(cv3.validateUserInput("Hallo", "Hallo").rightTrackResult)
        assertFalse(cv3.validateUserInput("Ha", "Ha").result)
        assertTrue(cv3.validateUserInput("Ha", "Ha").rightTrackResult)


    }

    @Test
    fun getValidationFunction() {
        //given
        val f : (String?) -> Boolean = {it!!.length > 3}
        val msg = Labels.CUSTOMVALIDATORMSG

        //when
        val cv = CustomValidator(f, validationMessage = msg)

        //then
        assertEquals(f, cv.getValidationFunction())
        assertEquals(msg, cv.validationMessage)
    }

}