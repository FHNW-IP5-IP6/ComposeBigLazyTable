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

package model.convertibles

import composeForms.convertibles.CustomConvertible
import composeForms.convertibles.ReplacementPair
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class CustomConvertibleTest {

    @Test
    fun convertUserInput_noGivenConvertibles() {
        //when
        val convertible = CustomConvertible(listOf())
        val valAsText = "Hallo"

        //then
        assertEquals(false, convertible.convertUserInput(valAsText).isConvertible)
        assertEquals("",    convertible.convertUserInput(valAsText).convertedValueAsText)
        assertEquals(true,  convertible.convertUserInput(valAsText).convertUserView)
        assertEquals(false, convertible.convertUserInput(valAsText).convertImmediately)

    }

    @Test
    fun convertUserInput_convertOnUnfocussing() {
        //when
        val convertible = CustomConvertible(listOf(
            ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")
        ))

        val valAsText = "1,3"

        //then
        assertEquals(true,  convertible.convertUserInput(valAsText).isConvertible)
        assertEquals("1.3", convertible.convertUserInput(valAsText).convertedValueAsText)
        assertEquals(true,  convertible.convertUserInput(valAsText).convertUserView)
        assertEquals(false, convertible.convertUserInput(valAsText).convertImmediately)

        assertEquals(false, convertible.convertUserInput("1,2,3").isConvertible)
    }

    @Test
    fun convertUserInput_convertImmediately() {
        //when
        val convertible = CustomConvertible(listOf(
            ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")
        ), convertImmediately = true)

        val valAsText = "1,3"

        //then
        assertEquals(true,  convertible.convertUserInput(valAsText).isConvertible)
        assertEquals("1.3", convertible.convertUserInput(valAsText).convertedValueAsText)
        assertEquals(true,  convertible.convertUserInput(valAsText).convertUserView)
        assertEquals(true,  convertible.convertUserInput(valAsText).convertImmediately)

        assertEquals(false, convertible.convertUserInput("1,2,3").isConvertible)
    }

    @Test
    fun convertUserInput_DontConvertUserView() {
        //when
        val convertible = CustomConvertible(listOf(
            ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")
        ), false)

        val valAsText = "1,3"

        //then
        assertEquals(true,  convertible.convertUserInput(valAsText).isConvertible)
        assertEquals("1.3", convertible.convertUserInput(valAsText).convertedValueAsText)
        assertEquals(false, convertible.convertUserInput(valAsText).convertUserView)
        assertEquals(false, convertible.convertUserInput(valAsText).convertImmediately)

        assertEquals(false, convertible.convertUserInput("1,2,3").isConvertible)
    }

}