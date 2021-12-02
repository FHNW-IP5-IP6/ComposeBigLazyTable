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

package composeForms.server

import composeForms.communication.MqttConnector
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import composeForms.model.BaseModel
import composeForms.model.ILabel
import composeForms.model.attributes.Attribute
import composeForms.model.attributes.IntegerAttribute
import composeForms.model.attributes.StringAttribute
import composeForms.model.modelElements.Field
import composeForms.model.modelElements.Group
import composeForms.model.validators.semanticValidators.StringValidator
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
class CommunicationITest {

    enum class testLabels(val test: String): ILabel {
        TEST("test")
    }

    var mqttConnector: MqttConnector = MqttConnector("test","")

    var model: BaseModel<testLabels>? = null

    var attribute1 : Attribute<*, *, *>? = null
    var attribute2 : Attribute<*, *, *>? = null
    var group      : Group<*>? = null

    @BeforeEach
    fun setUp(){
        Attribute.resetId()
        clearAllMocks()
        initObjects()
    }

    @BeforeAll
    private fun initMocks(){
        mqttConnector          = mockk(relaxed = true)
    }

    private fun initObjects(){
        model = null
        model = object: BaseModel<testLabels>(testLabels.TEST){
            override val mqttConnector: MqttConnector          = this@CommunicationITest.mqttConnector
        }

        //when
        attribute1 = StringAttribute<testLabels>(model!!, testLabels.TEST, value = "", validators = listOf(StringValidator(2,5)))
        attribute2 = StringAttribute(model!!, testLabels.TEST, value = "")

        group = Group(model!!, testLabels.TEST, Field(attribute1!!), Field(attribute2!!))

    }


    @Test
    fun testSendAmount(){
        //given
        model?.setCurrentFocusedAttribute(attribute1, group)
        clearAllMocks()

        //then
        verify(exactly = 0) {
            mqttConnector.publish(any(), "validation", any(), any())
            mqttConnector.publish(any(), "text", any(), any())
            mqttConnector.publish(any(), "attribute", any(), any())
            mqttConnector.publish(any(), "command", any(), any())
        }

        //when
        attribute1!!.setValueAsText("123")

        //then
        verify(exactly = 0) {
            mqttConnector.publish(any(), "command", any(), any())
            mqttConnector.publish(any(), "attribute", any(), any())
        }
        verify(exactly = 1) {
            mqttConnector.publish(any(), "text", any(), any())
        }
        verify(exactly = 3) { //2 times, since multiple validators
            mqttConnector.publish(any(), "validation", any(), any())
        }
    }

    @Test
    fun testChangeSelection(){

        verify(exactly = 0) {
            mqttConnector.publish(any(), "validation", any(), any())
            mqttConnector.publish(any(), "text", any(), any())
            mqttConnector.publish(any(), "attribute", any(), any())
            mqttConnector.publish(any(), "command", any(), any())
        }

        //when
        model!!.setCurrentFocusedAttribute(attribute2, group)

        //then
        verify(exactly = 0) {
            mqttConnector.publish(any(), "command")
        }
        verify(exactly = 1) {
            mqttConnector.publish(any(), "attribute", any(), any())
            mqttConnector.publish(any(), "text", any(), any())
            mqttConnector.publish(any(), "validation", any(), any())
        }
    }


    @Test
    fun testOnReceive(){
        //given
        model!!.setCurrentFocusedAttribute(attribute1, group)
        clearAllMocks() //This test is only interested on the workflow of the command received. Therefore the mocks are cleared before the test

        val start = "{ \"command\" :"
        val end = " }"
        var mid = ""

        //when
        mid = "\"REQUEST\""
        val command1 = start + mid + end
        model!!.onReceivedCommand(command1)
        //then
        verify(exactly = 1){
            mqttConnector.publish(any(), "attribute", any(), any())
            mqttConnector.publish(any(), "text", any(), any())
            mqttConnector.publish(any(), "validation", any(), any())
        }
    }

    @Test
    fun testOnReceiveOutOfBoundsSelection(){
        //given
        clearAllMocks() //This test is only interested on the workflow of the command received. Therefore the mocks are cleared before the test

        try {
            model!!.setCurrentFocusedAttribute(IntegerAttribute(object: BaseModel<testLabels>(testLabels.TEST){}, testLabels.TEST), group)
        }catch(e: Exception){}

        val start = "{ \"command\" :"
        val end = " }"
        var mid = ""

        //when
        mid = "\"REQUEST\""
        val command1 = start + mid + end
        model!!.onReceivedCommand(command1)
        //then
        verify(exactly = 0){
            mqttConnector.publish(any(), "attribute", any(), any())
            mqttConnector.publish(any(), "text", any(), any())
            mqttConnector.publish(any(), "validation", any(), any())
        }
    }

    @Test
    fun testConnectAndSubscribeConnectingAllChannels(){
        clearAllMocks() //Only interested in interactions from connect and subscribe
        //when
        model!!.connectAndSubscribe()
        //then
        verify(exactly = 1) {
            mqttConnector.connect(any(), any())
        }

    }
}