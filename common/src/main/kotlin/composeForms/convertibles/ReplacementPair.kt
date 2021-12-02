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

import kotlinx.serialization.Serializable

/**
 * A [ReplacementPair] has the following two parameters:
 * @param convertibleRegex : String. Regex pattern that is used to transform an string.
 * @param convertIntoRegex : String. Regex pattern that defines into what string a fitting string should transform.
 * 
 * Example:
 * ConvertIntoRegex could be "(\d+)(\,)(\d*)". This would extract all numbers before and all numbers after the comma.
 * ConvertIntoRegex could be "$1,$3". With this convertIntoRegex the split value will get put together with a point in between.
 *
 * @author Louisa Reinger, Steve Vogel
 */
@Serializable
class ReplacementPair(val convertibleRegex : String, val convertIntoRegex : String)