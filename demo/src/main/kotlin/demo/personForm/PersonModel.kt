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

package demo.personForm

import convertibles.CustomConvertible
import convertibles.ReplacementPair
import model.BaseModel
import model.attributes.*
import model.meanings.CustomMeaning
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.Group
import model.modelElements.HeaderGroup
import model.validators.semanticValidators.*

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
class PersonModel : BaseModel<PersonLabels>(title = PersonLabels.TITLE, smartphoneOption = true, wizardMode = true) {

    //********************************************
    //Attributes

    val id = IntegerAttribute(model = this, label = PersonLabels.ID, value = 1, readOnly = true)

    val firstName = StringAttribute(model = this, label = PersonLabels.FIRSTNAME,
        required = true,
        validators = listOf(StringValidator(minLength = 3, maxLength = 10)),
        formatter = { if(it != null && it.isNotEmpty()) "${it[0]}." else "" })

    val lastName = StringAttribute(model = this, label = PersonLabels.LASTNAME, required = true)

    val gender = SelectionAttribute(this, PersonLabels.GENDER,
        possibleSelections = listOf(PersonLabels.SELECTIONMAN, PersonLabels.SELECTIONWOMAN, PersonLabels.SELECTIONOTHER),
        validators = listOf(
            SelectionValidator(0,1, PersonLabels.GENDERVALIDATIONMSG)
        ))

    val married = BooleanAttribute(this, PersonLabels.MARRIED,
        trueText = PersonLabels.SELECTIONYES, falseText = PersonLabels.SELECTIONNO)

    val size = DoubleAttribute(this, PersonLabels.SIZE,
        meaning = CustomMeaning("m"),
        validators = listOf(
            FloatingPointValidator(2, PersonLabels.SIZEVALIDATIONMSG),
            NumberValidator(lowerBound = 0.0, upperBound = 3.0)
        ),
        convertibles = listOf(CustomConvertible(
            replaceRegex = listOf(ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")),
            convertUserView = false,
            convertImmediately = true
        )))

    val ageValidator = NumberValidator<Long, PersonLabels>(lowerBound = 0, upperBound = 130)

    val age = LongAttribute(this, PersonLabels.AGE,
        validators = listOf(ageValidator),
        observedAttributes = listOf(
            size addOnChangeListener { _, sizeValue ->
                ageValidator.overrideNumberValidator(lowerBound = if(sizeValue != null && sizeValue >= 1) 6 else 0)}
        ))

    val occupation = StringAttribute(this, PersonLabels.OCCUPATION)

    val taxNumber = IntegerAttribute(this, PersonLabels.TAXNUMBER,
        observedAttributes = listOf(
            occupation addOnChangeListener {taxAttr, occValue -> taxAttr.setRequired(occValue != null)}
        ))

    val membershipType = DecisionAttribute(this, PersonLabels.MEMBERSHIP_TYPE,
        decisionText1 = PersonLabels.SELECTIONACTIVEMEMBER,
        decisionText2 = PersonLabels.SELECTIONPASSIVMEMBER)

    val postCode = IntegerAttribute(this, PersonLabels.POSTCODE,
        validators = listOf(RegexValidator(
            regexPattern = ".{3,5}", rightTrackRegexPattern = ".{0,5}",
            validationMessage = PersonLabels.POSTCODEVALIDATIONMSG)
        ))

    val place       = StringAttribute(model = this, label = PersonLabels.PLACE)
    val street      = StringAttribute(model = this, label = PersonLabels.STREET)
    val houseNumber = ShortAttribute(model = this, label = PersonLabels.HOUSENUMBER)


    //********************************************
    //Groups

    val headerGroup = HeaderGroup(model = this, title = PersonLabels.HEADERGROUP,
        Field(id),
        Field(firstName, FieldSize.SMALL),
        Field(lastName, FieldSize.SMALL)
    )

    val group1 = Group(model = this, title = PersonLabels.PERSINFO,
        Field(id, FieldSize.SMALL),
        Field(firstName, FieldSize.SMALL),
        Field(lastName),
        Field(age, FieldSize.SMALL),
        Field(size, FieldSize.SMALL),
        Field(married),
        Field(gender, FieldSize.NORMAL),
        Field(occupation),
        Field(taxNumber),
        Field(membershipType)
    )

    val group2 = Group(model = this, title = PersonLabels.ADDRESS,
        Field(postCode),
        Field(place),
        Field(street),
        Field(houseNumber)
    )

    //********************************************

    init{
        this.setCurrentLanguage("english")
    }
}