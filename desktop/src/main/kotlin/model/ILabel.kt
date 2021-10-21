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

package model

import java.lang.reflect.Method
import java.util.*

/**
 * [ILabel] is an Interface for retrieving language from a class. Default implementation uses reflection for finding
 * all languages. The function are found if the start with "get" and are not functions from [Object] and not from [Enum]
 *
 * @author Louisa Reinger, Steve Vogel
 */
interface ILabel {

    /**
     * This method returns the String of a [label] (name of the enum constant) in the desired [language].
     * If the language is not defined an IllegalArgumentException is thrown
     *
     * @param label : Enum<*>
     * @param language : String
     * @return label : String
     * @throws IllegalArgumentException
     */
    fun getLanguageStringFromLabel(label: Enum<*>, language: String): String {

        val methodMap = getMethods().map { it.name.removePrefix("get").lowercase(Locale.getDefault()) to it }.toMap()

        if (methodMap.containsKey(language.lowercase(Locale.getDefault()))) {
            return methodMap[language.lowercase(Locale.getDefault())]!!.invoke(label) as String
        } else {
            throw IllegalArgumentException("Language not found")
        }
    }

    /**
     * This method can read out all public parameters (languages) of an enum.
     * It returns all used languages as a list.
     *
     * @return languages : List<String>
     */
    fun getLanguagesDynamic(): List<String> {
        val methodsFiltered = getMethods()
        return methodsFiltered.map { it.name.removePrefix("get").lowercase(Locale.getDefault()) }
    }

    /**
     * This method returns all getter functions that are created in the class
     *
     * @return getter : List<Method>
     */
    private fun getMethods(): List<Method> {

        val methodsUnfiltered: Array<Method> = this::class.java.methods

        val ownMethodNames = ILabel::class.java.methods.map { it.name }

        return methodsUnfiltered.filter {
            it.name.startsWith("get")
                    && !it.declaringClass.name.contains("Object")
                    && !it.declaringClass.name.contains("Enum")
                    && !it.name.equals(ownMethodNames[0])
                    && !it.name.equals(ownMethodNames[1])
        }
    }
}