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

package composeForms.model.validators

import composeForms.model.ILabel

/**
 * [ValidationResult] is for the result of a validation with a hard [result], a [rightTrackResult], the
 * [validationMessage] and the [defaultMessage] for the case that there is no validationMessage.
 *
 * @param result: Boolean flag that indicates if the value is valid
 * @param rightTrackResult: Boolean flag that indicate if the value is on a way that can be valid
 * @param validationMessage: [L] that contains the message for the case that the result is invalid
 * @param defaultMessage: String that contains a default message for the validator that generated the result
 *
 * @author Louisa Reinger, Steve Vogel
 */
class ValidationResult<L>(val result: Boolean, val rightTrackResult: Boolean, val validationMessage: L?, val defaultMessage: String) where L: ILabel, L: Enum<*>




