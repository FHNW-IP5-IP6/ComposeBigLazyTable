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

package composeForms.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
class ILabelTest {

    enum class Testenum(val x : String, val y : String) : ILabel {
        TEST("x", "y"),
    }

    enum class Labels(val eng: String, val de: String, private val fr: String) : ILabel {
        TEST("eng", "de", "fr"),
        T2("me", "ich", "je")
    }

    @Test
    fun testGetLabel(){
        //given
        val value = Testenum.TEST

        //then
        assertEquals("x", value.getLanguageStringFromLabel(value, "x"))
        assertEquals("y", value.getLanguageStringFromLabel(value, "y"))
    }

    @Test
    fun testGetLanguagesDynamic(){
        //then
        assertTrue(Testenum.TEST.getLanguagesDynamic().contains("x"))
        assertTrue(Testenum.TEST.getLanguagesDynamic().contains("y"))

        //then
        assertTrue(Labels.TEST.getLanguagesDynamic().contains("eng"))
        assertTrue(Labels.TEST.getLanguagesDynamic().contains("de"))
        assertFalse(Labels.TEST.getLanguagesDynamic().contains("fr"))
    }
}