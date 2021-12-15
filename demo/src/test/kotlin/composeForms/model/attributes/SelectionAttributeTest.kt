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
import composeForms.model.validators.semanticValidators.SelectionValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
class SelectionAttributeTest {

    lateinit var selAtr : SelectionAttribute<Labels>
    var model = object: BaseModel<Labels>(Labels.TEST) {}

    @BeforeEach
    fun setUpSelectionAttribute(){
        //given
        model = object: BaseModel<Labels>(Labels.TEST) {}
        selAtr = SelectionAttribute(model = model, possibleSelections = listOf(Labels.ELEMENT1, Labels.ELEMENT2), label = Labels.TEST)
    }

    @Test
    fun testAddUserSelection(){
        //then
        assertEquals("[]", selAtr.getValueAsText())

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertEquals("[ELEMENT1]", selAtr.getValueAsText())

        //when
        selAtr.addUserSelection(Labels.ELEMENT2)

        //then
        assertEquals("[ELEMENT1, ELEMENT2]", selAtr.getValueAsText())

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            selAtr.addUserSelection("NotExistingElement")
        }

        //when
        selAtr.save()
        selAtr.removeUserSelection(Labels.ELEMENT1)
        selAtr.removeUserSelection(Labels.ELEMENT2)
        selAtr.addUserSelection(Labels.ELEMENT2)
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertEquals("[ELEMENT2, ELEMENT1]", selAtr.getValueAsText())
        assertEquals(setOf(Labels.ELEMENT2, Labels.ELEMENT1), selAtr.getValue())
        assertFalse(selAtr.isChanged(), "Same elements, different order")
    }

    @Test
    fun testRemoveUserSelection(){
        //then
        assertEquals("[]", selAtr.getValueAsText())

        //when
        selAtr.removeUserSelection("ElementNotInList")

        //then
        assertEquals("[]", selAtr.getValueAsText())

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)
        selAtr.addUserSelection(Labels.ELEMENT2)

        //then
        assertEquals("[ELEMENT1, ELEMENT2]", selAtr.getValueAsText())

        //when
        selAtr.removeUserSelection(Labels.ELEMENT1)

        //then
        assertEquals("[ELEMENT2]", selAtr.getValueAsText())
    }

    @Test
    fun testSelectionValidator_MinNumberOfSelections() {
        //given
        val selVal = SelectionValidator<Labels>(0)
        selAtr.addValidator(selVal)

        //then
        assertEquals(0, selVal.getMinNumberOfSelections())
        assertTrue(selAtr.isValid())
        assertEquals(emptySet<String>(), selAtr.getValue())

        //when
        selVal.overrideSelectionValidator(1)

        //then
        assertEquals(1, selVal.getMinNumberOfSelections())
        assertEquals(0, selAtr.getValue()!!.size)
        assertTrue(selAtr.isValid()) //If not required: 0 elements are valid
        assertTrue(selAtr.isRightTrackValid())

        //when
        selAtr.setRequired(true)

        //then
        assertEquals(1, selVal.getMinNumberOfSelections())
        assertEquals(0, selAtr.getValue()!!.size)
        assertFalse(selAtr.isValid()) //If required: 0 elements are not valid
        assertTrue(selAtr.isRightTrackValid())

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertTrue(selAtr.isValid())

        //when
        selVal.overrideSelectionValidator(maxNumberOfSelections = 1)

        //then
        assertThrows(IllegalArgumentException ::class.java){
            //when
            selVal.overrideSelectionValidator(minNumberOfSelections = 2)
        }

        //then
        assertThrows(IllegalArgumentException ::class.java){
            //when
            selVal.overrideSelectionValidator(minNumberOfSelections = -1)
        }

        //when
        selAtr.addUserSelection(Labels.ELEMENT2)

        //then
        assertFalse(selAtr.isValid())

        //when
        selVal.overrideSelectionValidator(maxNumberOfSelections = 10)

        //then
        assertThrows(IllegalArgumentException ::class.java){
            //when
            selVal.overrideSelectionValidator(4)  //minSel > Number of possible elements
        }
    }

    @Test
    fun testSetMaxNumberOfSelections() {
        //given
        val selVal = SelectionValidator<Labels>()
        selAtr.addValidator(selVal)

        //then
        assertEquals(Int.MAX_VALUE, selVal.getMaxNumberOfSelections())
        assertTrue(selAtr.isValid())

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)
        selAtr.addUserSelection(Labels.ELEMENT2)
        selAtr.addANewPossibleSelection(Labels.ELEMENT3)
        selAtr.addUserSelection(Labels.ELEMENT3)
        selVal.overrideSelectionValidator(maxNumberOfSelections = 2)

        //then
        assertEquals(2, selVal.getMaxNumberOfSelections())
        assertFalse(selAtr.isValid())

        //when
        selAtr.removeUserSelection(Labels.ELEMENT3)

        //then
        assertTrue(selAtr.isValid())

        //when
        selVal.overrideSelectionValidator(maxNumberOfSelections = 2)

        //then
        assertThrows(IllegalArgumentException ::class.java){
            //when
            selVal.overrideSelectionValidator(maxNumberOfSelections = 0)
        }

        //then
        assertThrows(IllegalArgumentException ::class.java){
            //when
            selVal.overrideSelectionValidator(-1)
        }

        //when
        selAtr.addUserSelection(Labels.ELEMENT3)

        //then
        assertFalse(selAtr.isValid())
    }

    @Test
    fun testSetPossibleSelections() {
        //given
        val selVal = SelectionValidator<Labels>()
        selAtr.addValidator(selVal)

        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.setPossibleSelections(listOf(Labels.A, Labels.B, Labels.C, Labels.D))

        //then
        assertEquals(listOf(Labels.A, Labels.B, Labels.C, Labels.D), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.addANewPossibleSelection(Labels.E)

        //then
        assertEquals(listOf(Labels.A, Labels.B, Labels.C, Labels.D, Labels.E), selAtr.getPossibleSelections().flatten())

        //when
        selVal.overrideSelectionValidator(5)
        selAtr.addUserSelection(Labels.A)
        selAtr.addUserSelection(Labels.B)
        selAtr.addUserSelection(Labels.C)
        selAtr.addUserSelection(Labels.D)
        selAtr.addUserSelection(Labels.E)

        //then
        assertEquals(5, selVal.getMinNumberOfSelections())
        assertEquals(5, selAtr.getValue()!!.size)
        assertTrue(selAtr.isValid())

        //then
        assertThrows(IllegalArgumentException ::class.java){
            //when
            selAtr.setPossibleSelections(listOf(Labels.A, Labels.B, Labels.C, Labels.D)) //minSel > Number of possible elements
        }
    }

    @Test
    fun testAddANewPossibleSelection() {
        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.addANewPossibleSelection(Labels.ELEMENT1)

        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.addANewPossibleSelection(Labels.ELEMENT3)

        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2, Labels.ELEMENT3), selAtr.getPossibleSelections().flatten())
    }

    @Test
    fun testRemoveAPossibleSelection() {
        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.removeAPossibleSelection(Labels.ELEMENT3)


        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.removeAPossibleSelection(Labels.ELEMENT2)

        //then
        assertEquals(listOf(Labels.ELEMENT1), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.addANewPossibleSelection(Labels.ELEMENT2)
        val selVal = SelectionValidator<Labels>(2)
        selAtr.addValidator(selVal)

        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())
        assertEquals(2, selVal.getMinNumberOfSelections())

        //then
        assertThrows(IllegalArgumentException :: class.java){
            //when
            selAtr.removeAPossibleSelection(Labels.ELEMENT2) //minSel > Number of possible elements
        }

    }

    @Test
    fun testRemoveSelectedPossibleSelection(){
        //given
        selAtr.addUserSelection(Labels.ELEMENT1)
        selAtr.addUserSelection(Labels.ELEMENT2)

        //when
        selAtr.removeAPossibleSelection(Labels.ELEMENT2)

        //then
        assertEquals(listOf(Labels.ELEMENT1), selAtr.getPossibleSelections().flatten())
        assertEquals(setOf(Labels.ELEMENT1), selAtr.getValue())
    }

    @Test
    fun testGetMinNumberOfSelections() {
        //given
        val selVal = SelectionValidator<Labels>()
        selAtr.addValidator(selVal)

        //then
        assertEquals(0, selVal.getMinNumberOfSelections())

        //when
        selVal.overrideSelectionValidator(1)

        //then
        assertEquals(1, selVal.getMinNumberOfSelections())
    }

    @Test
    fun testGetMaxNumberOfSelections() {
        //given
        val selVal = SelectionValidator<Labels>()
        selAtr.addValidator(selVal)

        //then
        assertEquals(Int.MAX_VALUE, selVal.getMaxNumberOfSelections())

        //when
        selVal.overrideSelectionValidator(maxNumberOfSelections = 100)

        //then
        assertEquals(100, selVal.getMaxNumberOfSelections())
    }

    @Test
    fun testGetPossibleSelections() {
        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2), selAtr.getPossibleSelections().flatten())

        //when
        selAtr.addANewPossibleSelection(Labels.ELEMENT3)

        //then
        assertEquals(listOf(Labels.ELEMENT1, Labels.ELEMENT2, Labels.ELEMENT3), selAtr.getPossibleSelections().flatten())
    }

    //****************************************************************************************************************
    //Attribute-Tests

    @Test
    fun testSave() {
        //when
        selAtr.addUserSelection(Labels.ELEMENT1)
        selAtr.save()

        //then
        assertEquals( setOf(Labels.ELEMENT1), selAtr.getValue())
        assertEquals( setOf(Labels.ELEMENT1), selAtr.getSavedValue())
        assertEquals( "[ELEMENT1]", selAtr.getValueAsText())
        assertFalse(selAtr.isChanged())

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            selAtr.addUserSelection("notValidElement")
        }
        //when
        selAtr.save()

        //then
        assertEquals( setOf(Labels.ELEMENT1), selAtr.getValue())
        assertEquals( setOf(Labels.ELEMENT1), selAtr.getSavedValue())
        assertEquals( "[ELEMENT1]", selAtr.getValueAsText())
        assertFalse(selAtr.isChanged())

        //when
        selAtr.addUserSelection(Labels.ELEMENT2)

        //then
        assertEquals( setOf(Labels.ELEMENT1,Labels.ELEMENT2), selAtr.getValue())
        assertEquals( setOf(Labels.ELEMENT1), selAtr.getSavedValue())
        assertEquals( "[ELEMENT1, ELEMENT2]", selAtr.getValueAsText())
        assertTrue(selAtr.isChanged())
    }

    @Test
    fun testReset() {
        //given
        selAtr.addANewPossibleSelection(Labels.ELEMENT3)
        selAtr.addANewPossibleSelection(Labels.ELEMENT4)

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)
        selAtr.reset()

        //then
        assertEquals( emptySet<String>(), selAtr.getValue())
        assertEquals( emptySet<String>(), selAtr.getSavedValue())
        assertEquals( "[]", selAtr.getValueAsText())

        assertFalse(selAtr.isChanged())

        //when
        selAtr.addUserSelection(Labels.ELEMENT2)
        selAtr.save()
        selAtr.addUserSelection(Labels.ELEMENT3)
        selAtr.addUserSelection(Labels.ELEMENT4)
        selAtr.reset()

        //then
        assertEquals( setOf(Labels.ELEMENT2), selAtr.getValue())
        assertEquals( setOf(Labels.ELEMENT2), selAtr.getSavedValue())
        assertEquals( "[ELEMENT2]", selAtr.getValueAsText())
        assertFalse(selAtr.isChanged())
    }

    @Test
    fun testSetCurrentLanguage() {
        //when
        selAtr.setCurrentLanguage("test")

        //then
        assertEquals(Labels.TEST.test, selAtr.getLabel())

        //when
        selAtr.setCurrentLanguage("eng")

        //then
        assertEquals(Labels.TEST.eng, selAtr.getLabel())
    }

    @Test
    fun testSetValueAsText() {
        //when
        selAtr.addUserSelection(Labels.ELEMENT1) //add userSelection calls setValueAsText

        //then
        assertEquals("[ELEMENT1]", selAtr.getValueAsText())
        assertEquals(setOf(Labels.ELEMENT1), selAtr.getValue())

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            selAtr.addUserSelection("There was no such selection to choose")
        }

        //then
        assertTrue(selAtr.isValid()) //because setValueAsText(newVal) was not executed and there is still the old value in the inputfield
        assertEquals("[ELEMENT1]", selAtr.getValueAsText())
        assertEquals(setOf(Labels.ELEMENT1),selAtr.getValue())
    }

    @Test
    fun testSetRequired() {

        //when
        selAtr.setRequired(true)
        selAtr.setCurrentLanguage("test")

        //then
        assertEquals(true, selAtr.isRequired())

        //when
        selAtr.setRequired(false)

        //then
        assertFalse(selAtr.isRequired())
    }

    @Test
    fun testSetReadOnly() {
        //given
        val readOnly = true
        val notReadOnly = false

        //when
        selAtr.setReadOnly(readOnly)

        //then
        assertEquals(readOnly, selAtr.isReadOnly())

        //when
        selAtr.setReadOnly(notReadOnly)

        //then
        assertEquals(notReadOnly, selAtr.isReadOnly())
    }

    @Test
    fun testSetValue() {
        //then
        assertEquals(emptySet<String>(), selAtr.getValue())

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertEquals(setOf(Labels.ELEMENT1), selAtr.getValue())
    }

    @Test
    fun testSetValidationMessage() {
        //given
        val selVal = SelectionValidator<Labels>()
        selAtr.addValidator(selVal)

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertEquals(0, selAtr.getErrorMessages().size)

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            selAtr.addUserSelection("notValidElement")
        }

        //then
        assertEquals(0, selAtr.getErrorMessages().size)
    }

    @Test
    fun testGetValue() {
        //then
        assertEquals(emptySet<String>(), selAtr.getValue())
    }

    @Test
    fun testGetSavedValue() {
        //when
        selAtr.addUserSelection(Labels.ELEMENT1)
        selAtr.save()

        //then
        assertEquals(setOf(Labels.ELEMENT1),selAtr.getSavedValue())

        //when
        selAtr.addUserSelection(Labels.ELEMENT2)

        //then
        assertEquals(setOf(Labels.ELEMENT1),selAtr.getSavedValue())
    }

    @Test
    fun testGetValueAsText() {
        //then
        assertEquals("[]", selAtr.getValueAsText())
    }

    @Test
    fun testIsCurrentLanguage() {
        //given
        val lang = "eng"

        //when
        selAtr.setCurrentLanguage(lang)

        //then
        assertEquals(Labels.TEST.eng, selAtr.getLabel())
    }

    @Test
    fun testIsRequired() {
        //given
        val required = true
        val notRequired = false

        //when
        selAtr.setRequired(required)

        //then
        assertEquals(required, selAtr.isRequired())

        //when
        selAtr.setRequired(notRequired)

        //then
        assertEquals(notRequired, selAtr.isRequired())
    }

    @Test
    fun testIsReadOnly() {
        //given
        val readOnly = true
        val notReadOnly = false

        //when
        selAtr.setReadOnly(readOnly)
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertEquals(readOnly, selAtr.isReadOnly())

        //then
        assertEquals("[]", selAtr.getValueAsText())


        //when
        selAtr.setReadOnly(notReadOnly)

        //then
        assertEquals(notReadOnly, selAtr.isReadOnly())
    }

    @Test
    fun testGetValidationMessage() {
        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            selAtr.addUserSelection("notValidValueAsText")
        }

        //then
        assertEquals(0, selAtr.getErrorMessages().size) // "There was no such selection to choose" will be overwritten
    }

    @Test
    fun testIsChanged() {
        //then
        assertFalse(selAtr.isChanged())

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)

        //then
        assertTrue(selAtr.isChanged())

        //when
        selAtr.save()

        //then
        assertFalse(selAtr.isChanged())
    }

    @Test
    fun testNullValues() {
        //given
        val attr = SelectionAttribute(model = model, possibleSelections = emptyList(), label = Labels.TEST)

        //then
        assertEquals(emptySet<String>(), attr.getValue())
        assertEquals(emptySet<String>(), attr.getSavedValue())
        assertEquals("[]", attr.getValueAsText())
        assertTrue(attr.isValid())

        //given
        selAtr

        //when
        selAtr.addUserSelection(Labels.ELEMENT1)
        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            selAtr.addUserSelection("")
        }

        //then
        assertEquals(0, selAtr.getErrorMessages().size)
        assertEquals(setOf(Labels.ELEMENT1), selAtr.getValue())
        assertEquals(emptySet<String>(), selAtr.getSavedValue())
        assertEquals("[ELEMENT1]", selAtr.getValueAsText())
        assertTrue(selAtr.isValid())

        //when
        selAtr.removeUserSelection(Labels.ELEMENT1)

        //then
        assertEquals(emptySet<String>(), selAtr.getValue())
        assertEquals(emptySet<String>(), selAtr.getSavedValue())
        assertEquals("[]", selAtr.getValueAsText())

        //when
        selAtr.setRequired(true)

        //then
        assertEquals("Input required", selAtr.getErrorMessages()[0])
        assertEquals(emptySet<String>(), selAtr.getValue())
        assertEquals(emptySet<String>(), selAtr.getSavedValue())
        assertEquals("[]", selAtr.getValueAsText())
        assertFalse(selAtr.isValid())
    }
}