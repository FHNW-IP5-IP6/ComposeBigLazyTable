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

import composeForms.convertibles.CustomConvertible
import composeForms.convertibles.ReplacementPair
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import composeForms.model.BaseModel
import composeForms.model.Labels
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * @author Louisa Reinger
 * @author Steve Voge
 */
abstract class AttributeTest<T : Any>{

    var model = object: BaseModel<Labels>(title = Labels.TEST) {
        override val displayedAttributesInTable: List<Attribute<*, *, *>>?
            get() = TODO("Not yet implemented")
        override val id: Attribute<*, *, *>
            get() = TODO("Not yet implemented")
    }

    lateinit var validValue1Uneven : T
    lateinit var validValue2 : T
    lateinit var validValue3 : T
    lateinit var validValue4 : T
    lateinit var validValue1AsText : String
    lateinit var validValue2AsText : String
    lateinit var validValue3AsText : String
    lateinit var validValue4AsText : String
    lateinit var notValidValueAsText : String

    lateinit var attribute : Attribute<*, *, *>

    protected abstract fun provideAttribute(model: BaseModel<Labels>, value: T?) : Attribute<*, Any, *>

    @BeforeEach
    fun setUp(){
        //given
        attribute = provideAttribute(model, validValue1Uneven)
    }

    @Test
    fun testSave() {
        //when
        attribute.setValueAsText(validValue2AsText)
        attribute.save()

        //then
        assertEquals(     validValue2,          attribute.getValue())
        assertEquals(     validValue2,          attribute.getSavedValue())
        assertEquals(   validValue2AsText,    attribute.getValueAsText())
        assertFalse(attribute.isChanged())


        if(attribute !is StringAttribute) { //type string is always correct, if no validator is used

            //when
            attribute.setValueAsText(notValidValueAsText)
            attribute.save()

            //then
            assertEquals(notValidValueAsText, attribute.getValueAsText())
            assertEquals(validValue2, attribute.getValue())
            assertEquals(validValue2, attribute.getSavedValue())
            assertTrue(attribute.isChanged()) //because attr. was not saved because invalid
        }

        //when
        attribute.setValueAsText(validValue3AsText)

        //then
        assertTrue(attribute.isChanged())
    }

    @Test
    fun testReset() {
        //when
        attribute.setValueAsText(validValue2AsText)
        attribute.reset()

        //then
        assertEquals(validValue1Uneven,         attribute.getValue())
        assertEquals(validValue1Uneven,         attribute.getSavedValue())
        assertEquals(validValue1Uneven.toString(), attribute.getValueAsText())
        assertFalse(attribute.isChanged())

        //when
        attribute.setValueAsText(validValue2AsText)
        attribute.save()
        attribute.setValueAsText(validValue3AsText)
        attribute.setValueAsText(validValue4AsText)
        attribute.reset()

        //then
        assertEquals(validValue2,         attribute.getValue())
        assertEquals(validValue2,         attribute.getSavedValue())
        assertEquals(validValue2.toString(), attribute.getValueAsText())
        assertFalse(attribute.isChanged())
    }

    @Test
    fun testSetCurrentLanguage() {
        //when
        attribute.setCurrentLanguage("test")

        //then
        assertEquals(Labels.TEST.test, attribute.getLabel())

        //when
        attribute.setCurrentLanguage("eng")

        //then
        assertEquals(Labels.TEST.eng, attribute.getLabel())
    }

    @Test
    fun testSetValueAsText() {
        //when
        attribute.setValueAsText(validValue2AsText)

        //then
        assertEquals(validValue2AsText, attribute.getValueAsText())
        assertEquals(validValue2,attribute.getValue())

        if(attribute !is StringAttribute) { //type string is always correct
            //when
            attribute.setValueAsText(notValidValueAsText)

            //then
            assertFalse(attribute.isValid())
            if(attribute is DecisionAttribute){
                assertEquals("You must choose one of the two options given.", attribute.getErrorMessages()[0])
            }else{
                assertEquals("This is not the correct input type", attribute.getErrorMessages()[0])
            }
            assertEquals(validValue2,              attribute.getValue())
            assertEquals(notValidValueAsText,   attribute.getValueAsText())
        }
    }
    @Test
    fun testSetRequired() {
        //when
        attribute.setRequired(true)

        //then
        assertTrue(attribute.isRequired())

        //when
        attribute.setRequired(false)

        //then
        assertFalse(attribute.isRequired())
    }

    @Test
    fun testSetReadOnly() {
        //given
        val readOnly = true
        val notReadOnly = false

        //when
        attribute.setReadOnly(readOnly)

        //then
        assertEquals(readOnly, attribute.isReadOnly())

        //when
        attribute.setReadOnly(notReadOnly)

        //then
        assertEquals(notReadOnly, attribute.isReadOnly())
    }

    @Test
    fun testSetValue() {
        //then
        assertEquals(validValue1Uneven, attribute.getValue())

        //when
        attribute.setValueAsText(validValue2AsText)

        //then
        assertEquals(validValue2, attribute.getValue())
    }

    @Test
    fun testSetValidationMessage() {
        //when
        attribute.setValueAsText(validValue2AsText)

        //then
        assertEquals(0, attribute.getErrorMessages().size)

        if(attribute !is StringAttribute) { //type string is always correct

            //when
            attribute.setValueAsText(notValidValueAsText)

            //then
            if(attribute is DecisionAttribute){
                assertEquals("You must choose one of the two options given.", attribute.getErrorMessages()[0])
            }else{
                assertEquals("This is not the correct input type", attribute.getErrorMessages()[0])
            }

        }
    }

    @Test
    fun testGetValue() {
        //then
        assertEquals(validValue1Uneven, attribute.getValue())
    }

    @Test
    fun testGetSavedValue() {
        //when
        attribute.setValueAsText(validValue2AsText)
        attribute.save()

        //then
        assertEquals(validValue2,attribute.getSavedValue())

        //when
        attribute.setValueAsText(validValue3AsText)

        //then
        assertEquals(validValue2,attribute.getSavedValue())
    }

    @Test
    fun testGetValueAsText() {
        //then
        assertEquals(validValue1Uneven.toString(), attribute.getValueAsText())
    }

    @Test
    fun testIsCurrentLanguage() {
        //given
        val lang = "eng"

        //when
        attribute.setCurrentLanguage(lang)

        //then
        assertEquals(Labels.TEST.eng, attribute.getLabel())
    }

    @Test
    fun testIsRequired() {
        //given
        val required = true
        val notRequired = false

        //when
        attribute.setRequired(required)

        //then
        assertEquals(required, attribute.isRequired())

        //when
        attribute.setRequired(notRequired)

        //then
        assertEquals(notRequired, attribute.isRequired())
    }

    @Test
    fun testIsReadOnly() {
        //given
        val readOnly = true
        val notReadOnly = false

        //when
        attribute.setReadOnly(readOnly)
        attribute.setValueAsText(validValue2AsText)

        //then
        assertEquals(readOnly, attribute.isReadOnly())
        assertEquals(validValue1Uneven.toString(), attribute.getValueAsText())

        //when
        attribute.setReadOnly(notReadOnly)

        //then
        assertEquals(notReadOnly, attribute.isReadOnly())
    }

    @Test
    fun testGetValidationMessage() {
        if(attribute !is StringAttribute) { //type string is always correct
            //when
            attribute.setValueAsText(notValidValueAsText)

            //then
            if(attribute is DecisionAttribute){
                assertEquals("You must choose one of the two options given.", attribute.getErrorMessages()[0])
            }else{
                assertEquals("This is not the correct input type", attribute.getErrorMessages()[0])
            }
        }
    }

    @Test
    fun testIsChanged() {
        //then
        assertFalse(attribute.isChanged())

        //when
        attribute.setValueAsText(validValue2AsText)

        //then
        assertTrue(attribute.isChanged())

        //when
        attribute.save()

        //then
        assertFalse(attribute.isChanged())
    }

    @Test
    open fun testNullValues() {
        //given
        val attr = provideAttribute(model, null)

        //then
        assertEquals(null, attr.getValue())
        assertEquals(null, attr.getSavedValue())
        assertEquals("", attr.getValueAsText())
        assertTrue(attr.isValid())

        //when
        attr.setValueAsText(validValue1AsText)
        attr.setValueAsText("")

        //then
        assertEquals(0, attr.getErrorMessages().size)
        assertEquals(null, attr.getValue())
        assertEquals(null, attr.getSavedValue())
        assertEquals("", attr.getValueAsText())
        assertTrue(attr.isValid())

        //when
        attr.setRequired(true)

        attr.setValueAsText(validValue1AsText)
        attr.setValueAsText("")

        //then
        assertEquals("Input required", attr.getErrorMessages()[0])
        assertEquals(validValue1Uneven, attr.getValue(), "Non-valid values are not set in the value")
        assertEquals(null, attr.getSavedValue())
        assertEquals("", attr.getValueAsText())
        assertFalse(attr.isValid())
    }

    //***************************************************************************************
    //testConvertible functions

    @Test
    fun testConvertibleFunctionsInAttribute(){

        if(attribute is StringAttribute) {
            //given
            val attr = StringAttribute(
                model, label = Labels.TEST, convertibles = listOf(
                    CustomConvertible(
                        listOf(
                            ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")
                        ), convertUserView = true
                    )
                )
            )

            //when
            attr.setValueAsText("1,3")

            //then
            assertEquals(false, attr.getConvertImmediately()[0])
            assertEquals(true, attr.getConvertUserView()[0])
            assertEquals("1.3", attr.getConvertedValueAsText()[0])

            //when
            attr.checkAndSetConvertibleBecauseUnfocusedAttribute()

            //then
            assertEquals("1.3", attr.getValueAsText())

        }

        if(attribute is DoubleAttribute) {
            //given
            val attr = DoubleAttribute(
                model,label =  Labels.TEST, convertibles = listOf(
                    CustomConvertible(
                        listOf(
                            ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")
                        ), convertUserView = true
                    )
                )
            )

            //when
            attr.setValueAsText("1,3")

            //then
            assertEquals(false, attr.getConvertImmediately()[0])
            assertEquals(true, attr.getConvertUserView()[0])
            assertEquals("1.3", attr.getConvertedValueAsText()[0])

            //when
            attr.checkAndSetConvertibleBecauseUnfocusedAttribute()

            //then
            assertEquals("1.3", attr.getValueAsText())
        }

        if(attribute is FloatAttribute) {
            //given
            val attr = FloatAttribute(
                model,label =  Labels.TEST, convertibles = listOf(
                    CustomConvertible(
                        listOf(
                            ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")
                        ), convertUserView = true
                    )
                )
            )

            //when
            attr.setValueAsText("1,3")

            //then
            assertEquals(false, attr.getConvertImmediately()[0])
            assertEquals(true, attr.getConvertUserView()[0])
            assertEquals("1.3", attr.getConvertedValueAsText()[0])

            //when
            attr.checkAndSetConvertibleBecauseUnfocusedAttribute()

            //then
            assertEquals("1.3", attr.getValueAsText())
        }

        if(attribute is IntegerAttribute) {
            //given
            val attr = IntegerAttribute(
                model,label =  Labels.TEST, convertibles = listOf(
                    CustomConvertible(
                        listOf(
                            ReplacementPair("eins", "1")
                        ), convertUserView = true
                    )
                )
            )

            //when
            attr.setValueAsText("eins")

            //then
            assertEquals(false, attr.getConvertImmediately()[0])
            assertEquals(true, attr.getConvertUserView()[0])
            assertEquals("1", attr.getConvertedValueAsText()[0])

            //when
            attr.checkAndSetConvertibleBecauseUnfocusedAttribute()

            //then
            assertEquals("1", attr.getValueAsText())
        }
        if(attribute is LongAttribute) {
            //given
            val attr = LongAttribute(
                model, label =  Labels.TEST, convertibles = listOf(
                    CustomConvertible(
                        listOf(
                            ReplacementPair("eins", "1")
                        ), convertUserView = true
                    )
                )
            )

            //when
            attr.setValueAsText("eins")

            //then
            assertEquals(false, attr.getConvertImmediately()[0])
            assertEquals(true, attr.getConvertUserView()[0])
            assertEquals("1", attr.getConvertedValueAsText()[0])

            //when
            attr.checkAndSetConvertibleBecauseUnfocusedAttribute()

            //then
            assertEquals("1", attr.getValueAsText())
        }
        if(attribute is ShortAttribute) {
            //given
            val attr = ShortAttribute(
                model,label =  Labels.TEST, convertibles = listOf(
                    CustomConvertible(
                        listOf(
                            ReplacementPair("eins", "1")
                        ), convertUserView = true
                    )
                )
            )

            //when
            attr.setValueAsText("eins")

            //then
            assertEquals(false, attr.getConvertImmediately()[0])
            assertEquals(true, attr.getConvertUserView()[0])
            assertEquals("1", attr.getConvertedValueAsText()[0])

            //when
            attr.checkAndSetConvertibleBecauseUnfocusedAttribute()

            //then
            assertEquals("1", attr.getValueAsText())
        }

    }

//    @Test // Test not working on composeForms.server. Probably because of some timing issues
    open fun testUndoWithDelays(){
        //given
        attribute = provideAttribute(model, null)
        val initValue = attribute.getValueAsText()

        //when
        attribute.undo()
        //then
        assertEquals(initValue, attribute.getValueAsText())

        //when
        attribute.setValueAsText(validValue1AsText)
        runBlocking {
            delay(500L)
        }
        attribute.undo()
        //then
        assertEquals(initValue, attribute.getValueAsText())

        //when
        attribute.setValueAsText(validValue1AsText)
        runBlocking {
            delay(500L)
        }
        attribute.setValueAsText(validValue2AsText)
        runBlocking {
            delay(500L)
        }
        attribute.undo()

        //then
        assertEquals(validValue1AsText, attribute.getValueAsText())


        //when
        attribute.setValueAsText(validValue2AsText)
        runBlocking {
            delay(500L)
        }
        attribute.undo()
        attribute.setValueAsText(validValue3AsText)
        runBlocking {
            delay(500L)
        }
        attribute.undo()
        //then
        assertEquals(validValue1AsText, attribute.getValueAsText())

        //when
        attribute.setValueAsText(validValue2AsText)
        runBlocking {
            delay(500L)
        }
        attribute.reset()
        attribute.undo()
        //then
        assertEquals(validValue2AsText, attribute.getValueAsText())
    }

    //    @Test // Test not working on composeForms.server. Probably because of some timing issues
    fun testUndoDirect(){
        //given
        attribute = provideAttribute(model, null)
        val initValue = attribute.getValueAsText()

        //when
        attribute.setValueAsText(validValue2AsText)
        attribute.setValueAsText(validValue3AsText)
        //then
        assertEquals(false, attribute.isUndoable())
        assertEquals(validValue3AsText, attribute.getValueAsText())

        runBlocking { delay(500L) }

        //when
        attribute.setValueAsText(validValue4AsText)
        runBlocking {
            delay(500L)
        }
        attribute.reset()
        runBlocking {
            delay(500L)
        }
        attribute.undo()

        //then
        assertEquals(validValue4AsText, attribute.getValueAsText())
    }

//    @Test
    fun testRedo(){
        //given
        attribute = provideAttribute(model, null)
        //when
        attribute.setValueAsText(validValue2AsText)
        runBlocking { delay(500L) }
        attribute.undo()
        attribute.redo()
        //then
        assertEquals(validValue2AsText, attribute.getValueAsText())

        //when
        attribute.setValueAsText(validValue3AsText)
        attribute.undo()
        attribute.setValueAsText(validValue4AsText)
        //then
        assertFalse(attribute.isRedoable())

        //when
        attribute.redo()
        //then
        assertEquals(validValue4AsText, attribute.getValueAsText())
    }


    @Test
    fun testDefaultFormatter(){
        //when
        attribute.setValueAsText(validValue1AsText)
        //then
        assertEquals(validValue1AsText.lowercase(Locale.getDefault()),
            attribute.getValueAsText().lowercase(Locale.getDefault())
        )
        assertEquals(validValue1AsText.lowercase(Locale.getDefault()),
            attribute.getFormattedValue().lowercase(Locale.getDefault())
        )
    }

}