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

package server

import communication.AttributeType
import communication.DTOAttribute
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class DTOAttributeTest{

    @Test
    fun testValuesDefault(){
        //when
        val dtoAttribute = DTOAttribute(1, "label")

        //then
        assertEquals(1, dtoAttribute.id)
        assertEquals("label", dtoAttribute.label)
        assertEquals(AttributeType.OTHER, dtoAttribute.attrType)
        assertEquals(emptyList<String>(), dtoAttribute.possibleSelections)
    }

    @Test
    fun testValues(){
        //given
        val list = listOf("hallo", "123")
        val attributeType = AttributeType.SELECTION

        //when
        val dtoAttribute = DTOAttribute(1, "label", attributeType, list)

        //then
        assertEquals(1, dtoAttribute.id)
        assertEquals("label", dtoAttribute.label)
        assertEquals(attributeType, dtoAttribute.attrType)
        assertEquals(list, dtoAttribute.possibleSelections)
    }
}