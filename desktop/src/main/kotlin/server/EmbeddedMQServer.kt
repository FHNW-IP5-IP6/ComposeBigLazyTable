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

import com.hivemq.embedded.EmbeddedHiveMQ
import com.hivemq.embedded.EmbeddedHiveMQBuilder
import server.EmbeddedMqtt.start


/**
 * [EmbeddedMqtt] builds a [EmbeddedHiveMQ]-Server. Builds the server on object creation. [start] function will start the
 * server.
 *
 * @author Louisa Reinger, Steve Vogel
 */
object EmbeddedMqtt {

    private val hiveMQ: EmbeddedHiveMQ?

    private var started = false

    init{
        val embeddedHiveMQBuilder : EmbeddedHiveMQBuilder = EmbeddedHiveMQ.builder()
        hiveMQ = embeddedHiveMQBuilder.build()
    }

    fun start(){
        try {
            if(!started) {
                started = true
                hiveMQ?.start()?.join()
            }
        } catch (ex : Exception) {
            ex.printStackTrace()
        }
    }

}
