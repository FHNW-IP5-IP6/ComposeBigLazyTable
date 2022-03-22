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

import androidx.compose.ui.unit.dp
import composeForms.communication.AttributeType
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import composeForms.model.attributes.*
import composeForms.model.formatter.IFormatter
import composeForms.model.meanings.Default
import composeForms.model.modelElements.Field
import composeForms.model.modelElements.Group
import composeForms.model.modelElements.HeaderGroup
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class BaseModelTest {

    var model = object: composeForms.model.BaseModel<composeForms.model.BaseModelTest.Label>(_root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE){
        override val displayedAttributesInTable: List<Attribute<*, *, *>>?
            get() = TODO("Not yet implemented")
        override val id: Attribute<*, *, *>
            get() = TODO("Not yet implemented")
    }

    val AGE = 50
    val NO_OF_CHILDREN = 3

    lateinit var age : Attribute<*, Int, *>
    lateinit var noOfChildren : Attribute<*, Int, *>
    lateinit var group : Group<*>
    lateinit var group1: Group<*>
    lateinit var group2: Group<*>

    enum class Label(val test: String): _root_ide_package_.composeForms.model.ILabel {
        AGE("Age"),
        NO_OF_CHILDREN("Number of children"),
        TEST(""),
        TITLE("The Application"),
        G1("Group 1"),
        G2("Group 2"),
        G3("Group 3"),
        GHeader("Group Header");
    }

    @BeforeEach
    fun setUp(){
        //reset
        Attribute.resetId()
        model = object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE
        ){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }

        //given
        age = IntegerAttribute(model = model, value = AGE, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.AGE)

        noOfChildren = IntegerAttribute(model = model, value = NO_OF_CHILDREN, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)

        group = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(age), Field(noOfChildren))
        group1 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G2, Field(age))
        group2 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G3)
    }

    @Test
    fun testSaveAll() {
        //when
        age.setValueAsText("a")

        //then
        assertTrue(model.changesExist())
        assertFalse(model.allAttributesAreValid())

        //when
        age.setValueAsText("61")

        //then
        assertTrue(model.changesExist())
        assertTrue(model.allAttributesAreValid())

        //when
        model.save()

        //then
        assertFalse(model.changesExist())
        assertTrue(model.allAttributesAreValid())
        assertEquals("61", age.getValueAsText())
        assertSame(61, age.getValue())
        assertEquals(61,age.getSavedValue())
        assertEquals(NO_OF_CHILDREN.toString(), noOfChildren.getValueAsText())
        assertSame(NO_OF_CHILDREN, noOfChildren.getValue())
        assertEquals(NO_OF_CHILDREN,noOfChildren.getSavedValue())

        //when
        age.setValueAsText("40")

        //then
        assertEquals("40", age.getValueAsText())
        assertSame(40, age.getValue())
        assertEquals(61,age.getSavedValue())


        //when
        age.setValueAsText("a")
        assertFalse(model.save())
    }

    @Test
    fun testResetAll() {

        //when
        age.setValueAsText("61")
        noOfChildren.setValueAsText("2")
        model.reset()

        //then
        assertSame(NO_OF_CHILDREN, noOfChildren.getValue())
        assertSame(AGE, age.getValue())

        //when
        age.setValueAsText("61")
        noOfChildren.setValueAsText("2")
        model.save()
        model.reset()

        //then
        assertSame(61, age.getValue())
        assertSame(2, noOfChildren.getValue())
    }

    @Test
    fun testResetWizardMode(){
        //given
        model = object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE, wizardMode = true){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }
        val attr = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)
        age = IntegerAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, value = AGE)
        val g1 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(age))
        Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G2, Field(attr))
        model.setCurrentFocusedAttribute(age, g1)

        age.setValueAsText("123")
        attr.setValueAsText("test")

        //when
        model.reset()

        //then
        assertEquals(AGE, age.getValue())
        assertEquals("test", attr.getValueAsText())
    }

    @Test
    fun testCreateIntegerAttribute() {
        //when
        val attribute = IntegerAttribute(model = model, value = 5, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertEquals(5,attribute.getValue())
        assertEquals("5",attribute.getValueAsText())
        assertEquals(5,attribute.getSavedValue())

        //when
        val attributeDefaultVal = IntegerAttribute(model = model, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertEquals(null, attributeDefaultVal.getValue())
    }

    @Test
    fun testCreateShortAttribute() {
        //when
        val attribute = ShortAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, 5)

        //then
        assertEquals(5,attribute.getValue())
        assertEquals("5",attribute.getValueAsText())
        assertEquals(5,attribute.getSavedValue())

        //when
        val attributeDefaultVal = ShortAttribute(model, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertEquals(null, attributeDefaultVal.getValue())
    }

    @Test
    fun testCreateLongAttribute() {
        //when
        val attribute = LongAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, 5)

        //then
        assertEquals(5,attribute.getValue())
        assertEquals("5",attribute.getValueAsText())
        assertEquals(5,attribute.getSavedValue())

        //when
        val attributeDefaultVal = LongAttribute(model, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, tableColumnWidth = 150.dp)

        //then
        assertEquals(null, attributeDefaultVal.getValue())
    }

    @Test
    fun testCreateDoubleAttribute() {
        //when
        val attribute = DoubleAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, 5.5)

        //then
        assertEquals(5.5,attribute.getValue())
        assertEquals("5.5",attribute.getValueAsText())
        assertEquals(5.5,attribute.getSavedValue())

        //when
        val attributeDefaultVal = DoubleAttribute(model, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertEquals(null, attributeDefaultVal.getValue())
    }

    @Test
    fun testCreateFloatAttribute() {
        //when
        val attribute = FloatAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, 5.5f)

        //then
        assertEquals(5.5f,attribute.getValue())
        assertEquals("5.5",attribute.getValueAsText())
        assertEquals(5.5f,attribute.getSavedValue())

        //when
        val attributeDefaultVal = FloatAttribute(model, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertEquals(null, attributeDefaultVal.getValue())
    }

    @Test
    fun testCreateStringAttribute() {
        //when
        val attribute = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, "a")

        //then
        assertEquals("a", attribute.getValue())
        assertEquals("a", attribute.getValueAsText())
        assertEquals("a", attribute.getSavedValue())

        //when
        val attributeDefaultVal = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertEquals(null, attributeDefaultVal.getValue())

    }

    @Test
    fun testChanges() {
        //when
        model.updateChanges()

        //then
        assertFalse(model.changesExist())

        //when
        noOfChildren.setValueAsText("5")

        //then
        assertTrue(model.changesExist())

        //when
        model.updateChanges()

        //then
        assertTrue(model.changesExist())
    }

    @Test
    fun testSetValidForAll() {
        //when
        model.setValid()

        //then
        assertTrue(model.allAttributesAreValid())

        //when
        noOfChildren.setValueAsText("a")

        //then
        assertFalse(model.allAttributesAreValid())

        //when
        age.setValueAsText("5")

        //then
        assertFalse(model.allAttributesAreValid())
    }

    @Test
    fun testSetCurrentLanguageForAll() {
        //when
        model.setCurrentLanguage("test")

        //then
        assertTrue(model.isCurrentLanguage("test"))
        assertEquals("Number of children", noOfChildren.getLabel())

        //then
        assertThrows(IllegalArgumentException::class.java) {
            model.setCurrentLanguage("de")
        }
    }

    @Test
    fun testGetGroupsAndAttributes() {
        //then
        assertEquals(3, model.getAllGroups().flatMap{it.getAttributes()}.size)
        assertEquals(2, model.getAllGroups()[0].getAttributes().size)
        assertEquals(1, model.getAllGroups()[1].getAttributes().size)
        assertEquals(0, model.getAllGroups()[2].getAttributes().size)
        assertEquals(3, model.getAllGroups().size)
        assertEquals("Group 1", model.getAllGroups()[0].getTitle())
        assertEquals("Group 2", model.getAllGroups()[1].getTitle())
        assertEquals("Group 3", model.getAllGroups()[2].getTitle())
    }


    @Test
    fun testGetTitle() {
        //then
        assertEquals("The Application", model.getTitle())
    }

    @Test
    fun testIsChangedForAll() {
        //then
        assertFalse(model.changesExist())

        //when
        age.setValueAsText("3")

        //then
        assertTrue(model.changesExist())

        //when
        model.save()

        //then
        assertFalse(model.changesExist())

        //when
        age.setValueAsText("3")

        //then
        assertFalse(model.changesExist())
    }

    @Test
    fun testIsValidForAll() {
        //then
        assertTrue(model.allAttributesAreValid())

        //when
        age.setValueAsText("10")

        //then
        assertTrue(model.allAttributesAreValid())

        //when
        age.setValueAsText("b")

        //then
        assertFalse(model.allAttributesAreValid())
    }

    @Test
    fun testIsCurrentLanguageForAll() {
        //when
        model.setCurrentLanguage("test")
        //then
        assertFalse(model.isCurrentLanguage("eng"))
        assertTrue(model.isCurrentLanguage("test"))

    }

    @Test
    fun testGetAttributeType(){
        //when
        var attr: Attribute<*, *, *> = StringAttribute(model,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN
        )
        //then
        assertEquals(AttributeType.STRING, model.getAttributeType(attr))

        //when
        attr = DoubleAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        //then
        assertEquals(AttributeType.DOUBLE, model.getAttributeType(attr))

        //when
        attr = FloatAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        //then
        assertEquals(AttributeType.FLOAT, model.getAttributeType(attr))

        //when
        attr = ShortAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        //then
        assertEquals(AttributeType.SHORT, model.getAttributeType(attr))

        //when
        attr = IntegerAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        //then
        assertEquals(AttributeType.INTEGER, model.getAttributeType(attr))

        //when
        attr= LongAttribute(model,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN, tableColumnWidth = 150.dp)
        //then
        assertEquals(AttributeType.LONG, model.getAttributeType(attr))

        //when
        attr = SelectionAttribute(model,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN, emptyList())
        //then
        assertEquals(AttributeType.SELECTION, model.getAttributeType(attr))


        //when
        attr = object : Attribute<StringAttribute<_root_ide_package_.composeForms.model.BaseModelTest.Label>, String, _root_ide_package_.composeForms.model.BaseModelTest.Label>(model,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN, null, false, false, emptyList(), emptyList(),
            emptyList(), Default(), IFormatter{it?:""}, false, null, 0.dp){
            override val typeT: String
                get() = ""
        }
        //then
        assertEquals(AttributeType.OTHER, model.getAttributeType(attr))

    }

    @Test
    fun testOnChangeListeners(){
        //given
        val taxNumber = StringAttribute(
            model = model,
            label = _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST,
            observedAttributes = listOf(age addOnChangeListener { a, v -> a.setRequired( v?:0 >= 18)})
        )
        Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(taxNumber))


        //then
        assertTrue(taxNumber.isRequired())

        //when
        age.setValueAsText("17")
        //then
        assertFalse(taxNumber.isRequired())

        //when
        age.setValueAsText("18")
        //then
        assertTrue(taxNumber.isRequired())

        //when
        age.setValueAsText("19")
        //then
        assertTrue(taxNumber.isRequired())

        //when
        age.setValueAsText("")
        //then
        assertFalse(taxNumber.isRequired())
    }

    @Test
    fun testSetCurrentFocusIndex(){
        //when
        model.setCurrentFocusedAttribute(age, group)
        //then
        assertEquals(0, model.getCurrentFocusedAttribute()?.getId())
        assertEquals(age, model.getCurrentFocusedAttribute())
        assertTrue(group.getAttributes().contains(age))

        //when
        model.setCurrentFocusedAttribute(noOfChildren, group)
        //then
        assertEquals(1, model.getCurrentFocusedAttribute()?.getId())
        assertEquals(noOfChildren, model.getCurrentFocusedAttribute())


        //given
        val notInModelAttr = IntegerAttribute(object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE
        ){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        //then
        assertThrows(java.lang.IllegalArgumentException::class.java) {
            //when
            model.setCurrentFocusedAttribute(notInModelAttr, group)
        }
        assertEquals(noOfChildren, model.getCurrentFocusedAttribute())
        assertFalse(group.getAttributes().contains(notInModelAttr))
    }

    @Disabled("org.opentest4j.AssertionFailedError: expected: <0> but was: <2>")
    @Test
    fun testFocus(){
        assertEquals(null, model.getCurrentFocusedAttribute())
        //when
        model.focusNext()
        //then
        assertEquals(0, model.getCurrentFocusedAttribute()?.getId())

        //when
        model.focusNext()
        //then
        assertEquals(1, model.getCurrentFocusedAttribute()?.getId())

        //when
        model.focusNext()
        //then
        assertEquals(0,  model.getCurrentFocusedAttribute()?.getId())

        //when
        model.focusPrevious()
        //then
        assertEquals(1,  model.getCurrentFocusedAttribute()?.getId())

        //when
        model.focusPrevious()
        //then
        assertEquals(0,  model.getCurrentFocusedAttribute()?.getId())
    }

    @Test
    fun testFocusWithHeaderGroup(){
        //given
        val attr1 = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)
        val attr2 = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)
        val attr3 = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)

        val hg = HeaderGroup(model,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.GHeader, Field(attr1), Field(attr2), Field(attr3))

        //when
        model.focusNext()
        //then
        assertEquals(age, model.getCurrentFocusedAttribute())

        //when
        attr1.setValueAsText("hallo")
        model.reset()
        //then
        assertEquals("", attr1.getValueAsText())

    }

    @Test
    fun testWizardGroupChanges(){
        //given
        model = object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE, wizardMode = true){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }
        val attr1 = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)
        val attr2 = IntegerAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, value = AGE)
        val attr3 = DoubleAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, value = 0.0)
        val g1 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(attr1))
        val g2 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G2, Field(attr2))
        val g3 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G3, Field(attr3))
        model.setCurrentFocusedAttribute(attr1, g1)
        model.setCurrentFocusedAttribute(attr1, g1)

        //when
        val res = model.previousWizardGroup()

        //then
        assertFalse(res)
        assertEquals(g1, model.getCurrentWizardGroup())

        //when
        val res1 = model.save()

        //then
        assertTrue(res1)
        assertEquals(g2, model.getCurrentWizardGroup())


        //when
        val res2 = model.previousWizardGroup()
        //then
        assertTrue(res2)
        assertEquals(g1, model.getCurrentWizardGroup())

        //when
        model.save()
        val res3 = model.save()

        assertTrue(res3)
        assertEquals(g3, model.getCurrentWizardGroup())
        assertTrue(model.isLastWizardGroup())

    }

    @Test
    fun testNextPreviousInWizardMode(){
        //given
        model = object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE, wizardMode = true){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }
        val attr1 = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST)
        val attr2 = IntegerAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, value = AGE)
        val attr3 = DoubleAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, value = 0.0)
        val attr4 = DoubleAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.TEST, value = 0.0)
        val g1 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(attr1), Field(attr2))
        val g2 = Group(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.G2, Field(attr3), Field(attr4))
        model.setCurrentFocusedAttribute(attr1, g1)

        //when
        model.focusNext()
        //then
        assertEquals(attr2, model.getCurrentFocusedAttribute())
        assertEquals(g1, model.getCurrentWizardGroup())
        //when
        model.focusNext()
        //then
        assertEquals(attr1, model.getCurrentFocusedAttribute())
        assertEquals(g1, model.getCurrentWizardGroup())
        //when
        model.focusPrevious()
        //then
        assertEquals(attr2, model.getCurrentFocusedAttribute())
        assertEquals(g1, model.getCurrentWizardGroup())
        //when
        model.focusPrevious()
        //then
        assertEquals(attr1, model.getCurrentFocusedAttribute())
        assertEquals(g1, model.getCurrentWizardGroup())
    }

    @Test
    fun testOnReceivedText(){
        //given
        val string = "{ \"id\":0, \"text\":\"123\" }"
        //when
        model.onReceivedText(string)
        //then
        assertEquals("123", age.getValueAsText())


        //given
        val string2 = "{ \"id\":2, \"text\":\"123\" }"
        //when
        model.onReceivedText(string)
        //then
        assertEquals("123", age.getValueAsText())
    }

    @Disabled("org.opentest4j.AssertionFailedError: expected: <1> but was: <12>")
    @Test
    fun testOnReceivedCommand(){
        //given
        val attr = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        group.addAttribute(attr)

        model.setCurrentFocusedAttribute(age, group)
        assertEquals(0, model.getCurrentFocusedAttribute()?.getId())

        val start = "{ \"command\" :"
        val end = " }"
        var mid = ""

        //when
        mid = "\"NEXT\""
        val command1 = start + mid + end
        model.onReceivedCommand(command1)
        //then
        assertEquals(1, model.getCurrentFocusedAttribute()?.getId())

        model.onReceivedCommand(command1)
        //then
        assertEquals(2, model.getCurrentFocusedAttribute()?.getId())

        //when
        mid = "\"PREVIOUS\""
        val command2 = start + mid + end
        model.onReceivedCommand(command2)
        //then
        assertEquals(1, model.getCurrentFocusedAttribute()?.getId())


        //given
        model.getCurrentFocusedAttribute()?.setValueAsText("1")
        runBlocking {
            delay(500L)
        }
        model.getCurrentFocusedAttribute()?.setValueAsText("12")
        runBlocking {
            delay(500L)
        }
        //when
        mid = "\"UNDO\""
        val command3 = start + mid + end
        model.onReceivedCommand(command3)
        //then
        assertEquals("1", model.getCurrentFocusedAttribute()?.getValueAsText())
    }

    @Test
    fun testOnReceivedCommandNull(){
        //given
        val attr = StringAttribute(model, _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        group.addAttribute(attr)

        assertEquals(null, model.getCurrentFocusedAttribute())

        val start = "{ \"command\" :"
        val end = " }"
        var mid = ""

        //when
        mid = "\"REQUEST\""
        val command1 = start + mid + end
        model.onReceivedCommand(command1)
        //then
        assertEquals(null, model.getCurrentFocusedAttribute()?.getId())

        //when
        mid = "\"NEXT\""
        val command2 = start + mid + end
        model.onReceivedCommand(command2)
        //then
        assertEquals(0, model.getCurrentFocusedAttribute()?.getId())

    }

    @Test
    fun testAutoSave(){

        //then
        assertFalse(model.isAutoSave())

        //when
        model.changeAutoSave()                  //auto save = on
        //then
        assertTrue(model.isAutoSave())
        assertFalse(age.isChanged())
        assertFalse(noOfChildren.isChanged())
        assertFalse(model.changesExist())

        //when
        age.setValueAsText("1")     //valid input (both attr. are valid)
        //then
        assertFalse(model.changesExist())       //because it was auto saved

        //when
        model.changeAutoSave()                  //auto save = off
        age.setValueAsText("a")      //invalid input (1 attr. is valid, 1 invalid)
        //then
        assertFalse(model.isAutoSave())
        assertTrue(age.isChanged())
        assertFalse(age.isValid())
        assertFalse(model.allChangedAttributesAreValid())
        assertFalse(model.allAttributesAreValid())
        assertTrue(model.changesExist())

        //when
        noOfChildren.setValueAsText("3") //valid input (1 attr. is valid, 1 invalid)
        model.changeAutoSave()                      //auto save = on
        //then
        assertFalse(noOfChildren.isChanged())   //was auto saved because valid
        assertTrue(age.isChanged())             //was not auto saved because invalid

        //when
        age.setValueAsText("2")     //valid input (both attr. are valid)

        //then
        assertFalse(age.isChanged())
        assertFalse(noOfChildren.isChanged())   //was auto saved because valid
        assertTrue(model.allChangedAttributesAreValid())
        assertTrue(model.allAttributesAreValid())
        assertFalse(model.changesExist())
    }

    @Test
    fun testWizardMode(){
        //given
        val wizardModel = object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE, wizardMode = true){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }
        age             = IntegerAttribute(model = wizardModel, value = AGE, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.AGE)
        noOfChildren    = IntegerAttribute(model = wizardModel, value = NO_OF_CHILDREN, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN)
        group           = Group(wizardModel,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(age), Field(noOfChildren))
        group1          = Group(wizardModel, _root_ide_package_.composeForms.model.BaseModelTest.Label.G2, Field(age))

        //then
        assertTrue(wizardModel.isWizardMode())
        assertEquals(0, wizardModel.getCurrentGroupIndex())
        assertEquals(wizardModel.getAllGroups()[0], wizardModel.getCurrentWizardGroup())
        assertEquals(2, wizardModel.getAllGroups().size)

        //when
        age.setValueAsText("a")             //invalid input
        noOfChildren.setValueAsText("4")    //valid input
        wizardModel.save()

        //then
        assertFalse(wizardModel.isLastWizardGroup())
        assertFalse(wizardModel.isValidForWizardGroup())
        assertEquals("a", age.getValueAsText())
        assertEquals("4", noOfChildren.getValueAsText())
        assertEquals(50, age.getSavedValue())           //because nothing was saved
        assertEquals(3, noOfChildren.getSavedValue())   //because nothing was saved
        assertTrue(wizardModel.isWizardMode())
        assertEquals(0, wizardModel.getCurrentGroupIndex())
        assertEquals(wizardModel.getAllGroups()[0], wizardModel.getCurrentWizardGroup())

        //when
        age.setValueAsText("40")            //valid input
        wizardModel.save() //all attr. are valid

        //then
        assertTrue(wizardModel.isValidForWizardGroup())
        assertTrue(wizardModel.isLastWizardGroup())
        assertTrue(wizardModel.isWizardMode())
        assertEquals(1, wizardModel.getCurrentGroupIndex())
        assertEquals(wizardModel.getAllGroups()[1], wizardModel.getCurrentWizardGroup())

        //when
        age.setValueAsText("5")
        wizardModel.save() //all attr. are valid

        //then
        assertTrue(wizardModel.isLastWizardGroup())
        assertEquals("5", age.getValueAsText())
        assertEquals(5, age.getSavedValue())
        assertTrue(wizardModel.isWizardMode())
        assertEquals(1, wizardModel.getCurrentGroupIndex())
        assertEquals(wizardModel.getAllGroups()[1], wizardModel.getCurrentWizardGroup())
    }

    @Test
    fun testWizardModeWithInitiallyInvalidAttribute(){
        //given
        val wizardModel = object: _root_ide_package_.composeForms.model.BaseModel<_root_ide_package_.composeForms.model.BaseModelTest.Label>(
            _root_ide_package_.composeForms.model.BaseModelTest.Label.TITLE, wizardMode = true){
            override val displayedAttributesInTable: List<Attribute<*, *, *>>?
                get() = TODO("Not yet implemented")
            override val id: Attribute<*, *, *>
                get() = TODO("Not yet implemented")
        }
        age             = IntegerAttribute(model = wizardModel, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.AGE, required = true) //invalid
        noOfChildren    = IntegerAttribute(model = wizardModel, value = NO_OF_CHILDREN, label = _root_ide_package_.composeForms.model.BaseModelTest.Label.NO_OF_CHILDREN) //valid
        group           = Group(wizardModel,
            _root_ide_package_.composeForms.model.BaseModelTest.Label.G1, Field(age), Field(noOfChildren))
        group1          = Group(wizardModel, _root_ide_package_.composeForms.model.BaseModelTest.Label.G2, Field(age))

        //then
        assertTrue(wizardModel.allFocusedAttributesOfCurrentViewAreValid())
        assertFalse(wizardModel.isValidForWizardGroup())

        //when
        wizardModel.setCurrentFocusedAttribute(age, group)

        //then
        assertFalse(wizardModel.allFocusedAttributesOfCurrentViewAreValid())
        assertFalse(wizardModel.isValidForWizardGroup())

        //when
        age.setValueAsText("3")

        //then
        assertTrue(wizardModel.allFocusedAttributesOfCurrentViewAreValid())
        assertTrue(wizardModel.isValidForWizardGroup())
    }
}