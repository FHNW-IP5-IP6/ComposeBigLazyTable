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
internal class DecisionAttributeTest: AttributeTest<String>() {

    override fun provideAttribute(model: BaseModel<Labels>, value: String?): Attribute<*, Any, *> {
        return DecisionAttribute(model, Labels.TEST, Labels.DECISIONTEXT1, Labels.DECISIONTEXT2, value ?: "val1")
    }

    init{
        validValue1Uneven = "val1"
        validValue1AsText = "val1"

        validValue2        = "val2"
        validValue2AsText  = "val2"

        validValue3        = "val1"
        validValue3AsText  = "val1"

        validValue4        = "val2"
        validValue4AsText  = "val2"

        notValidValueAsText  = "invalid"  //Not used (type String is always correct)

    }

    override fun testNullValues() {
        //given
        val attr = DecisionAttribute(model, Labels.TEST,Labels.DEC1, Labels.DEC2) //null can not be set here because not allowed in DualAttributes

        //when
        attr.setValueAsText("")

        //then
        assertFalse(attr.isValid())
        assertEquals("Dec1", attr.getValue())
    }

    @Test
    fun testChangeDecision(){
        //given
        val attr = DecisionAttribute(model, Labels.TEST, Labels.DEC1, Labels.DEC2)

        //then
        assertEquals("Dec1",    attr.getValue()!!)
        assertEquals("Dec1",    attr.getValueAsText())
        assertEquals(Labels.DEC1,    attr.decision1Text)
        assertEquals(Labels.DEC2,    attr.decision2Text)
        assertTrue(attr.isValid())
        assertEquals(attr.getValue(),    attr.decision1SaveValue)

        //when
        attr.changeDecision()

        //then
        assertEquals("Dec2",    attr.getValue()!!)
        assertEquals("Dec2",    attr.getValueAsText())
        assertEquals("Dec1",    attr.decision1Text.getLanguageStringFromLabel(attr.decision1Text, attr.getModel().getCurrentLanguage()))
        assertEquals(4,         attr.getPossibleSelections().size)
        assertTrue(attr.getPossibleSelections().contains("Dec1"))
        assertEquals("Dec2",    attr.decision2Text.getLanguageStringFromLabel(attr.decision2Text, attr.getModel().getCurrentLanguage()))
        assertTrue(attr.getPossibleSelections().contains("Dec2"))
        assertTrue(attr.isValid())
        assertEquals(attr.getValue(),    attr.decision2SaveValue)

        //when
        attr.changeDecision()

        //then
        assertEquals("Dec1",    attr.getValue()!!)
        assertEquals("Dec1",    attr.getValueAsText())
        assertEquals(attr.getValue(), attr.decision1SaveValue)
    }
}