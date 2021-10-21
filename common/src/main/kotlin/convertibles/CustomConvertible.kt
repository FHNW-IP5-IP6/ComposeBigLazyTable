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

package convertibles

import kotlinx.serialization.Serializable
import java.util.regex.Pattern

/**
 * A [CustomConvertible] has the function [convertUserInput] that checks if the [valueAsText] could be converted (Is
 * there a ReplacementPair that is fitting).
 * If yes, the first convertible result will be returned and all other replacement pairs ignored.
 *
 * @param replaceRegex : a list of [ReplacementPair] holding a string representation of regex pattern for converting.
 * @param convertUserView : Boolean. Should the value be converted for the user view?
 * @param convertImmediately : Boolean. Should the result be converted directly after the result is created? (Or only
 * when the user leaves the attribute?)
 *
 * @author Louisa Reinger, Steve Vogel
 */
@Serializable
class CustomConvertible(var replaceRegex : List<ReplacementPair>, var convertUserView : Boolean = true, var convertImmediately : Boolean = false){

    /**
     * Converts the [valueAsText]. Therefore it replaces the string with the regex pattern given in the constructor.
     * The first match of a replacement pair will be used for the result.
     *
     * @param valueAsText: value that will be converted
     * @return ConvertibleResult
     */
    fun convertUserInput(valueAsText : String): ConvertibleResult {
        var convertiblePattern : Pattern
        replaceRegex.forEach{
            convertiblePattern = Pattern.compile(it.convertibleRegex)
            if(convertiblePattern.matcher(valueAsText).matches()){
                val convertIntoPattern = convertiblePattern.matcher(valueAsText).replaceAll(it.convertIntoRegex)
                return ConvertibleResult(true, convertIntoPattern, convertUserView, convertImmediately)
            }
        }
        return ConvertibleResult(false, "", convertUserView, convertImmediately)
    }

}