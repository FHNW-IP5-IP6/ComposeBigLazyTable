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

package composeForms.model

import composeForms.model.attributes.Attribute
import composeForms.model.modelElements.Group
import composeForms.model.validators.ValidatorType

/**
 * [IModel] is the interface needed for Compose Forms. The composeForms.model is for the state of the application and controls
 * the attributes and common properties.
 *
 * @author Louisa Reinger, Steve Vogel
 */
interface IModel<L> where L : ILabel, L: Enum<*> {

    fun getTitle() : String

    //Group Information
    fun addGroup(group: Group<*>)
    fun getAllGroups() : List<Group<*>>
    fun getCurrentGroupIndex(): Int
    fun isLastWizardGroup() : Boolean
    fun getCurrentWizardGroup() : Group<*>?
    fun getCurrentFocusedGroup(): Group<*>?

    //Reset
    fun reset() : Boolean

    //Language
    fun setCurrentLanguage(lang : String)
    fun isCurrentLanguage(lang: String): Boolean
    fun getCurrentLanguage(): String
    fun getPossibleLanguages(): List<String>

    //Save
    fun save() : Boolean
    fun changeAutoSave()
    fun updateChanges()
    fun isAutoSave(): Boolean
    fun changesExist(): Boolean

    //Wizard-Mode
    fun previousWizardGroup() : Boolean
    fun isWizardMode() : Boolean

    //Validation
    fun setValid()
    fun allAttributesAreValid() : Boolean
    fun allChangedAttributesAreValid(): Boolean
    fun allFocusedAttributesOfCurrentViewAreValid(): Boolean
    fun getValidationMessageOfNonSemanticValidator(validator: ValidatorType): L?
    fun isValidForWizardGroup() : Boolean

    //Tooltips
    fun getTooltipAutoSave(): String
    fun getTooltipReset(): String
    fun getTooltipPrevious(): String
    fun getTooltipSaveAndNext(): String
    fun getTooltipSave(): String
    fun getTooltipConnectSmartphone(): String
    fun getTooltipUndo(): String
    fun getTooltipRedo(): String
    fun getTooltipSaveAndExit(): String
    fun getTooltipMessage(): String

    //Focus Handling
    fun setCurrentFocusedAttribute(attr: Attribute<*, *, *>?, group: Group<*>?)
    fun getCurrentFocusedAttribute(): Attribute<*, *, *>?
    fun focusNext()
    fun focusPrevious()
    fun setFocusBlocked(block: Boolean)

    //Communication with smartphone app
    fun publishText(attr: Attribute<*, *, *>)
    fun publishAttribute(attr: Attribute<*, *, *>)
    fun publishValidation(attr: Attribute<*, *, *>)
    fun getIPAdress(): String
    fun getAttributeById(id: Int?): Attribute<*, *, *>?
    fun isSmartphoneOption() : Boolean

    //Error Handling
    fun hasException(): Boolean
    fun getException(): Exception?
    fun setException(e: Exception)
}