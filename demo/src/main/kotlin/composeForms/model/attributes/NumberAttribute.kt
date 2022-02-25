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

import composeForms.convertibles.CustomConvertible
import composeForms.model.ILabel
import composeForms.model.IModel
import composeForms.model.formatter.IFormatter
import composeForms.model.meanings.SemanticMeaning
import composeForms.model.validators.semanticValidators.SemanticValidator
import org.jetbrains.exposed.sql.Column

/**
 * The [NumberAttribute] is an abstract attribute for all types of numbers.
 *
 * @param model: the composeForms.model that the attribute belongs to
 * @param label: [L] (ILable enum entry) where a string for each language can be defined.
 * @param value: initial value
 * @param required: if a value is required
 * @param readOnly: if the value is read only or writeable
 * @param observedAttributes: List of functions that are executed if the values of the observed attributes change.
 * @param validators: List of [SemanticValidator]s that are used for the validation of the user input ([valueAsText]).
 * @param convertibles: List of [CustomConvertible]s that are used to convert a not type-matching [valueAsText] (String)
 * into a type-matching String (that can be converted into the type [T] of the attribute).
 * @param meaning: [SemanticMeaning] used to add a meaning to the value
 * @param formatter: [IFormatter] that formats the value into a different view
 *
 * @author Louisa Reinger, Steve Vogel
 */
abstract class NumberAttribute <N,T,L> (
    //required parameters
    model                   : IModel<L>,
    label                   : L,

    //optional parameters
    value                   : T?,
    required                : Boolean,
    readOnly                : Boolean,
    observedAttributes      : List<(Attribute<*, *, *>) -> Unit>,
    validators              : List<SemanticValidator<T,L>>,
    convertibles            : List<CustomConvertible>,
    meaning                 : SemanticMeaning<T>,
    formatter               : IFormatter<T>?,

    canBeFiltered           : Boolean,
    databaseField           : Column<*>?

) : Attribute<N, T, L>(
    model = model,
    value = value,
    label = label,
    required = required,
    readOnly = readOnly,
    observedAttributes = observedAttributes,
    validators = validators,
    convertibles = convertibles,
    meaning = meaning,
    formatter = formatter,
    canBeFiltered = canBeFiltered,
    databaseField = databaseField
) where N : NumberAttribute<N, T, L>, T : Number, T : Comparable<T>, L: Enum<*>, L : ILabel