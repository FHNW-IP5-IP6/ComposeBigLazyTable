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

/**
 * This file contains the different bigLazyTable.data transfer objects. Those are meant to be used for the composeForms.communication.
 * All DTO classes are marked with @Serializable annotation from the package kotlinx.serialization.
 * Additionally the types for command and attribtue are provided as enum class in this file.
 *
 * @author Louisa Reinger, Steve Vogel
 */

import composeForms.convertibles.CustomConvertible
import kotlinx.serialization.Serializable

@Serializable
class DTOAttribute(val id                   : Int,
                   val label                : String,
                   val attrType             : AttributeType             = AttributeType.OTHER,
                   val possibleSelections   : List<String>              = emptyList(),
                   val convertibles         : List<CustomConvertible>   = emptyList(),
                   val readOnly             : Boolean                   = false,
                   val required             : Boolean                   = false,
                   val meaningAsText        : String                    = ""
)

@Serializable
class DTOText(      val id  : Int,
                    val text: String)

@Serializable
class DTOValidation(val onRightTrack    : Boolean      = true,
                    val isValid         : Boolean      = true,
                    val errorMessages   : List<String> = emptyList(),
                    val isUndoable      : Boolean      = true,
                    val isRedoable      : Boolean      = true)

@Serializable
class DTOCommand(val command: Command)

/**
 * Commands for a DTOCommand
 */
enum class Command {
    NEXT,
    PREVIOUS,
    REQUEST,
    UNDO,
    REDO
}

/**
 * Attribute types for a DTOAttribute
 */
enum class AttributeType {
    STRING,
    INTEGER,
    SHORT,
    DOUBLE,
    FLOAT,
    LONG,
    SELECTION,
    DECISION,
    BOOLEAN,
    OTHER
}
