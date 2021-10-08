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

package util

/**
 * [Utilities] contains util functions for retrieving information about the type [T] of a value
 * or for converting a value from one type into another type.
 *
 * @author Louisa Reinger, Steve Vogel
 */

class Utilities<T> {

    /**
     * This method converts a sting to the desired type typeT.
     *
     * @param value: String
     * @param typeT : T
     * @return value as T
     */
    fun toDataType(value : String, typeT : T) : T{
        return when (typeT){
            is Int      -> value.toInt() as T
            is Short    -> value.toShort() as T
            is Long     -> value.toLong() as T
            is Double   -> value.toDouble() as T
            is Float    -> value.toFloat() as T
            is Set<*>   -> stringToSetConverter(value) as T
            is Boolean  -> {if(value.equals("false")) false as T else if (value.equals("true")) true as T
                            else throw IllegalArgumentException("No Boolean")} //Otherwise all strings will be converted to Boolean.
            else -> value as T
        }
    }

    /**
     * This method converts a String into a Set<String>.
     *
     * @param valAsText : String
     * @return set : Set<String>
     * @throws StringIndexOutOfBoundsException: thrown if the [valAsText] is shorter than 2 and not [].
     */
    fun stringToSetConverter(valAsText : String) : Set<String>{
        var set : Set<String>
        if(valAsText == "[]" || valAsText == ""){
            set = emptySet()
        }else{
            set = valAsText.substring(1,valAsText.length-1).split(", ").toSet() //convert string to set
        }
        return set
    }

    /**
     * Get the min value of number type.
     *
     * @param typeT : T
     */
    fun getMinValueOfNumberTypeT(typeT : T): T{
        return when (typeT) {
            is Int -> Int.MIN_VALUE as T
            is Short -> Short.MIN_VALUE as T
            is Long -> Long.MIN_VALUE as T
            is Double -> -Double.MAX_VALUE as T
            is Float -> -Float.MAX_VALUE as T
            else -> 0 as T
        }
    }

    /**
     * Get max value of a number type.
     *
     * @param typeT : T
     */
    fun getMaxValueOfNumber(typeT: T): T{
        return when (typeT) {
            is Int -> Int.MAX_VALUE as T
            is Short -> Short.MAX_VALUE as T
            is Long -> Long.MAX_VALUE as T
            is Double -> Double.MAX_VALUE as T
            is Float -> Float.MAX_VALUE as T
            else -> 0 as T
        }
    }
}