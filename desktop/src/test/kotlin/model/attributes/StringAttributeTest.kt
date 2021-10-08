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

package model.attributes

import model.BaseModel
import model.Labels
import model.validators.semanticValidators.StringValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class StringAttributeTest: AttributeTest<String>() {

    override fun provideAttribute(model: BaseModel<Labels>, value: String?): Attribute<*, Any, *> {
        return StringAttribute(model, Labels.TEST, value) as Attribute<*, Any, *>
    }

    init{
        validValue1Uneven = "Hallo"
        validValue1AsText  = "Hallo"

        validValue2        = "789"
        validValue2AsText  = "789"

        validValue3        = "AEIOU"
        validValue3AsText  = "AEIOU"

        validValue4        = "Name"
        validValue4AsText  = "Name"

        notValidValueAsText  = "A".repeat(1_000_000 + 1)  //Not used (type String is always correct)

    }

    lateinit var stringAtr : StringAttribute<Labels>

    @BeforeEach
    fun setUpStringAtr(){
        //given
        stringAtr = StringAttribute(model, Labels.TEST, validValue1Uneven)
    }

    @Test
    fun testStringValidator_MinLength() {
        //given
        val validator = StringValidator(4, validationMessage = Labels.MIN4CHARS)

        stringAtr = StringAttribute(
            value = "123",
            model = model,
            validators = listOf(validator),
            label = Labels.TEST
        )

        //then
        assertFalse(stringAtr.isValid())
        assertEquals("Der Wert muss mindestens 4 Buchstaben haben.", stringAtr.getErrorMessages()[0])

        //when
        validator.overrideStringValidator(2)

        //then
        assertTrue(stringAtr.isValid())


        //given
        stringAtr = StringAttribute(
            value = "1234",
            model = model,
            validators = listOf(StringValidator(4)),
            label = Labels.TEST
        )

        //then
        assertTrue(stringAtr.isValid())


        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            stringAtr = StringAttribute(
                value = "1234",
                model = model,
                validators = listOf(StringValidator(-1)),
                label = Labels.TEST
            )
        }

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            validator.overrideStringValidator(5,3, Labels.EMTPY)
        }
    }

    @Test
    fun testStringValidator_MaxLength() {
        //given
        val validator = StringValidator(maxLength = 4, validationMessage = Labels.MAX4CHARS)

        val attr = StringAttribute(
            value = "12345",
            model = model,
            validators = listOf(validator),
            label = Labels.TEST
        )

        //then
        assertFalse(attr.isValid())
        assertEquals("Der Wert darf maximal 4 Buchstaben haben.", attr.getErrorMessages()[0])

        //when
        validator.overrideStringValidator(maxLength = 10)

        //then
        assertTrue(attr.isValid())


        //given
        val attr2 = StringAttribute(
            value = "1234",
            model = model,
            validators = listOf(StringValidator(4)),
            label = Labels.TEST
        )

        //then
        assertTrue(attr2.isValid())

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            val attr3 = StringAttribute(
                value = "1234",
                model = model,
                validators = listOf(StringValidator(maxLength = -1)),
                label = Labels.TEST
            )
        }

        //then
        assertThrows(IllegalArgumentException::class.java){
            //when
            validator.overrideStringValidator(5,3, Labels.EMTPY)
        }
    }

    @Test
    fun testRequiredInConstructor() {
        //given
        val attr = StringAttribute(
            value = "",
            model = model,
            required = true,
            label = Labels.TEST
        )

        //then
        assertFalse(attr.isValid())
        assertEquals("Input required", attr.getErrorMessages()[0])

        //when
        attr.setRequired(false)

        //then
        assertTrue(attr.isValid())


        //given
        val attr2 = StringAttribute(
            value = "1234",
            model = model,
            required = false,
            label = Labels.TEST
        )

        //then
        assertTrue(attr2.isValid())
    }

}