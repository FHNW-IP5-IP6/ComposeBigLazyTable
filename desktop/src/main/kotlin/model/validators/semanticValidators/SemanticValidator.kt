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

package model.validators.semanticValidators

import model.ILabel
import model.validators.Validator

/**
 * [SemanticValidator] is a [Validator] with additional functionality. Those are for change of the validator which are
 * allowed to happen during runtime but not allowed to change.
 * SemanticValidator is meant to validate for some domain specific / type specific values.
 *
 * @param validationMessage: [L] custom message
 *
 * @author Louisa Reinger, Steve Vogel
 */
abstract class SemanticValidator<T, L>(validationMessage : L?) : Validator<T, L>(validationMessage = validationMessage)
        where L: ILabel, L: Enum<*> {

    /**
     * The values to be set are first checked to see if they make sense
     */
    fun init(){
        checkAndSetDevValues()
    }

    /**
     * This method checks whether the values to set make sense or not.
     * If not, an IllegalArgumentException is thrown.
     * If yes, setValues is called.
     * Finally the temporary cache values are deleted again.
     *
     * @throws IllegalArgumentException
     */
    abstract fun checkAndSetDevValues()

    /**
     * This method writes the temporary set caches into the values.
     */
    protected abstract fun setValues()

    /**
     * This method sets all cache values to null.
     */
    protected abstract fun deleteCaches()
}