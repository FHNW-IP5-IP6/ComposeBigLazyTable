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

package composeForms.communication

import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * This class provides a connection to a mqtt broker. On initialisation a client will be created but not connected.
 * The method [connect] is there to establish a connection. Afterwards [subscribe] and [publish] can be used to receive
 * and send messages.
 * [Disconnect] can be used to shut down the connection (not needed if the application closes).
 *
 * @param mqttBroker: IP or URL of the broker
 * @param mainTopic: main topic for all subscriptions and publications
 * @param qos: Quality of Service level
 *
 * Documentation of the client:
 * https://hivemq.github.io/hivemq-mqtt-client/
 * https://github.com/hivemq/hivemq-mqtt-client
 *
 * @author Louisa Reinger, Steve Vogel
 */

class MqttConnector (val mqttBroker: String,
                     val mainTopic: String,
                     val qos: MqttQos = MqttQos.EXACTLY_ONCE){

    private val client = Mqtt5Client.builder()
        .serverHost(mqttBroker)
        .serverPort(1883)
        .identifier(UUID.randomUUID().toString())
        .buildAsync()

    /**
     * Connect to the composeForms.server
     *
     * @param onConnectionFailed: function that is invoked when the connection could not be established
     * @param onConnected: function that is invoked when the connection has established
     */
    fun connect(onConnectionFailed: () -> Unit  = {},
                onConnected: () -> Unit         = {}) {

        client.connectWith()
            .cleanStart(true)
            .keepAlive(30)
            .send()
            .whenComplete { _, throwable ->
                if (throwable != null) { //connection failed
                    throwable.printStackTrace()
                    onConnectionFailed.invoke()
                } else { //connected
                    onConnected()
                }
            }
    }

    /**
     * Subscribe to a subtopic to receive messages from there.
     *
     * @param subtopic: subtopic that is subscribed to. Will be added behind the main topic
     * @param onNewMessage: function that is invoked when a new message arrived
     */
    fun subscribe(subtopic: String = "#",
                  onNewMessage: (String) -> Unit){

        client.subscribeWith()
            .topicFilter(mainTopic + subtopic)
            .qos(qos)
            .noLocal(true)
            .callback {
                onNewMessage.invoke(it.payloadAsString())
            }
            .send()
    }

    /**
     * Publish a message to a subtopic.
     *
     * @param message: String that will be sent
     * @param subtopic: Subtopic / channel to which it will be sent. Will be added behind the main topic
     * @param onPublished: function that is invoked after sending was successful
     * @param onError: function that is invoked when an error occurs during sending
     */
    fun publish(message: String,
                subtopic: String        = "",
                onPublished: () -> Unit = {},
                onError: () -> Unit     = {}) {

        client.publishWith()
            .topic(mainTopic + subtopic)
            .payload(message.asPayload())
            .qos(qos)
            .retain(false)
            .messageExpiryInterval(120)
            .send()
            .whenComplete{_, throwable ->
                if(throwable != null){
                    onError.invoke()
                }
                else {
                    onPublished.invoke()
                }
            }
    }

    /**
     * Disconnect from the composeForms.server
     */
    fun disconnect() {
        client.disconnectWith()
            .sessionExpiryInterval(0)
            .send()
    }
}

//Extension functions for transforming strings
private fun String.asPayload() : ByteArray = toByteArray(StandardCharsets.UTF_8)
private fun Mqtt5Publish.payloadAsString() : String = String(payloadAsBytes, StandardCharsets.UTF_8)