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

package composeForms.model.attributes

import composeForms.model.ILabel
import composeForms.model.IModel
import composeForms.model.meanings.Default
import composeForms.model.meanings.SemanticMeaning
import org.jetbrains.exposed.sql.Column

/**
 * The [DecisionAttribute] is an attribute implementation to represent a decision between two values (Strings).
 * It is a sub class of DualAttribute and therefore it has two additional labels for those two values
 * It is a sub class of the DualAttribute. It has two additional parameters for the two decision values.
 *
 * @param model: the composeForms.model that the attribute belongs to
 * @param label: [L] (ILable enum entry) where a string for each language can be defined.
 * @param decisionText1: [L] for the first text
 * @param decisionText2: [L] for the second text
 * @param value: initial value
 * @param readOnly: if the value is read only or writeable
 * @param observedAttributes: List of functions that are executed if the values of the observed attributes change.
 * @param meaning: [SemanticMeaning] used to add a meaning to the value
 *
 * @author Louisa Reinger, Steve Vogel
 */
open class DecisionAttribute<L>(
    //required parameters
    model                               : IModel<L>,
    label                               : L,
    private val decisionText1           : L,
    private val decisionText2           : L,

    //optional parameters
    value                               : String                                = decisionText1.getLanguageStringFromLabel(decisionText1, model.getCurrentLanguage()),
    readOnly                            : Boolean                               = false,
    observedAttributes                  : List<(a: Attribute<*,*,*>) -> Unit>   = emptyList(),
    meaning                             : SemanticMeaning<Any>                  = Default(),

    canBeFiltered                       : Boolean                               = true,
    databaseField                       : Column<*>?                            = null

) : DualAttribute<DecisionAttribute<L>, Any, L>(
    model = model,
    value = value,
    label = label,
    decision1Text = decisionText1,
    decision2Text = decisionText2,
    decision1SaveValue = decisionText1.getLanguageStringFromLabel(decisionText1, model.getCurrentLanguage()),
    decision2SaveValue = decisionText2.getLanguageStringFromLabel(decisionText2, model.getCurrentLanguage()),
    readOnly = readOnly,
    observedAttributes = observedAttributes,
    meaning = meaning,
    canBeFiltered = canBeFiltered,
    databaseField = databaseField
) where L: Enum<*>, L : ILabel {


    override val typeT: String
        get() = "0"
}