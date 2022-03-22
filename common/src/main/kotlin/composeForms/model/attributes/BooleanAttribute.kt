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

import androidx.compose.ui.unit.Dp
import bigLazyTable.view.theme.MinTableCellWidth
import composeForms.model.ILabel
import composeForms.model.IModel
import composeForms.model.meanings.Default
import composeForms.model.meanings.SemanticMeaning
import org.jetbrains.exposed.sql.Column

/**
 * The [BooleanAttribute] is the attribute implementation of type Boolean.
 * It is a sub class of the DualAttribute. It has two additional parameters for the representation of the true and
 * the false value.
 *
 * @param model: the composeForms.model that the attribute belongs to
 * @param label: [L] (ILable enum entry) where a string for each language can be defined.
 * @param value: initial value
 * @param readOnly: if the value is read only or writeable
 * @param observedAttributes: List of functions that are executed if the values of the observed attributes change.
 * @param meaning: [SemanticMeaning] used to add a meaning to the value
 * @param falseText: [L] (ILable enum entry): User representation for the for the value false
 * @param trueText: [L] (ILable enum entry): User representation for the for the value true
 *
 * @author Louisa Reinger, Steve Vogel
 */
class BooleanAttribute<L>(
    //required parameters
    model                   : IModel<L>,
    label                   : L,

    //optional parameters
    value                   : Boolean                               = false,
    readOnly                : Boolean                               = false,
    observedAttributes      : List<(a: Attribute<*, *, *>) -> Unit> = emptyList(),
    meaning                 : SemanticMeaning<Boolean>              = Default(),

    falseText               : L                                     = Decision.False as L,
    trueText                : L                                     = Decision.True as L,

    canBeFiltered           : Boolean                               = true,
    databaseField           : Column<Boolean>?                      = null,
    tableColumnWidth        : Dp                                    = MinTableCellWidth

) : DualAttribute<BooleanAttribute<L>, Boolean, L>(
    model = model,
    label = label,
    decision1Text = falseText,
    decision2Text = trueText,
    decision1SaveValue = false,
    decision2SaveValue = true,
    value = value,
    readOnly = readOnly,
    observedAttributes = observedAttributes,
    meaning = meaning,
    canBeFiltered = canBeFiltered,
    databaseField = databaseField,
    tableColumnWidth = if (tableColumnWidth < MinTableCellWidth) MinTableCellWidth else tableColumnWidth
) where L : ILabel, L: Enum<*> {

    override val typeT: Boolean
        get() = false

    /**
     * Implementing default label and override getLanguageStringFromLabel to return always one value.
     */
    enum class Decision : ILabel {
        True,
        False;

        override fun getLanguageStringFromLabel(label: Enum<*>, language: String): String {
            return label.name
        }
    }
}