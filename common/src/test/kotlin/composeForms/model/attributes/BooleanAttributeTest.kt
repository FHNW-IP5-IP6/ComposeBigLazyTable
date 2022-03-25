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
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class BooleanAttributeTest: AttributeTest<Boolean>(){

    init{
        validValue1Uneven = false
        validValue1AsText  = "false"

        validValue2        = true
        validValue2AsText  = "true"

        validValue3        = false
        validValue3AsText  = "false"

        validValue4        = true
        validValue4AsText  = "true"

        notValidValueAsText = "invalid"

    }

    override fun provideAttribute(model: BaseModel<Labels>, value: Boolean?): Attribute<*, Any, *> {
        return BooleanAttribute(model = model, label = Labels.TEST) as Attribute<*, Any, *>
    }

    override fun testNullValues() {
        //given
        val attr = BooleanAttribute(model, Labels.TEST) //null can not be set here because not allowed in DualAttributes

        //when
        attr.setValueAsText("")

        //then
        assertFalse(attr.isValid())
        assertEquals(false, attr.getValue())
    }

    @Test
    fun testChangeDecision(){
        //given
        val attr = BooleanAttribute(model, Labels.TEST, falseText = Labels.FALSETEXT, trueText = Labels.TRUETEXT)

        //then
        assertFalse(attr.getValue()!!)
        assertEquals("false", attr.getValueAsText())
        assertEquals("No", attr.decision1Text.test)
        assertEquals("Yes", attr.decision2Text.test)
        assertTrue(attr.isValid())
        assertEquals(attr.getValue(), attr.decision1SaveValue)

        //when
        attr.changeDecision()

        //then
        assertTrue(attr.getValue()!!)
        assertEquals("true", attr.getValueAsText())
        assertEquals("No", attr.decision1Text.test)
        assertEquals(4, attr.getPossibleSelections().size)
        assertEquals("Yes", attr.decision2Text.test)
        assertTrue(attr.isValid())
        assertEquals(attr.getValue(), attr.decision2SaveValue)

        //when
        attr.changeDecision()

        //then
        assertFalse(attr.getValue()!!)
        assertEquals("false", attr.getValueAsText())
        assertEquals(attr.getValue(), attr.decision1SaveValue)
    }
}