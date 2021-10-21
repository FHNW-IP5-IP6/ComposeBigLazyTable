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

package model.meanings

/**
 * Interface for adding a meaning to a value
 *
 * @author Louisa Reinger, Steve Vogel
 */
interface SemanticMeaning<T> {

    /**
     * Returns the meaning for a String.
     * @param valAsText: String on which the meaning is added
     * @return: the meaning accordingly to the valAsText
     */
    fun addMeaning(valAsText : String) : String
}