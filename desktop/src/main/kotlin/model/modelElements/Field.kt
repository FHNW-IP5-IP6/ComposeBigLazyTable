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

package model.modelElements

import model.attributes.Attribute

/**
 * [Field] is a class to connect an attribute with a [FieldSize]
 *
 * @author Louisa Reinger, Steve Vogel
 */
class Field(private val attribute : Attribute<*, *, *>, private val fieldSize : FieldSize = FieldSize.NORMAL) {

    fun getAttribute(): Attribute<*, *, *> {
        return attribute
    }

    fun getFieldSize(): FieldSize {
        return fieldSize
    }
}
