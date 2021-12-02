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

package composeForms.convertibles

/**
 * ConvertibleResult is the result of a [CustomConvertible]. It has the following information:
 * @param isConvertible : is the valueAsText convertible?,
 * @param convertedValueAsText : converted string,
 * @param convertUserView : should valueAsText get converted or not? (value gets converted in any case),
 * @param convertImmediately : should be converted immediately or only when the user leaves the attribute?
 *
 * @author Louisa Reinger, Steve Vogel
 */
class ConvertibleResult(val isConvertible : Boolean, val convertedValueAsText : String, val convertUserView : Boolean, val convertImmediately : Boolean)