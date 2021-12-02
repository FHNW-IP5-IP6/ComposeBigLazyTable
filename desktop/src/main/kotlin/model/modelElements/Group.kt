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

import model.ILabel
import model.IModel
import model.attributes.Attribute

/**
 * [Group] is a to add attributes together for domain specific grouping.
 *
 * @param model: [IModel] that this group is added to
 * @param title: [L] for title of the group
 * @param fields: Fields that are in this group
 *
 * @author Louisa Reinger, Steve Vogel
 */
open class Group<L>(val model : IModel<*>, private val title : L, vararg fields : Field) where L: ILabel, L: Enum<*> {

    private val groupFields = fields.map { it }.toMutableList()
    private val groupAttributes = fields.map { it.getAttribute() }.toMutableList()

    init {
        model.addGroup(this)
        fields.forEach{
            if(model != it.getAttribute().getModel()){
                throw IllegalArgumentException("Model of the attribute does not match the model of the group.")
            }
            it.getAttribute().setCurrentLanguage(model.getCurrentLanguage())
        }
    }

    fun getTitle(): String{
        return title.getLanguageStringFromLabel(title, model.getCurrentLanguage())
    }

    /**
     * Adds an attribute to the group and sets its language to the current language of the [model]
     * @param attribute: Attribute that will be added to the group
     */
    fun addAttribute(attribute : Attribute<*, *, *>){
        groupAttributes.add(attribute)
        attribute.setCurrentLanguage(model.getCurrentLanguage())
    }

    /**
     * Removes the attribute from the group
     * @param attribute: Attribute that will be removed from the group
     */
    fun removeAttribute(attribute: Attribute<*, *, *>){
        groupAttributes.remove(attribute)
    }

    //**************
    //Getter

    fun getAttributes(): List<Attribute<*, *, *>>{
        return groupAttributes
    }

    fun getFields(): List<Field>{
        return groupFields
    }
}