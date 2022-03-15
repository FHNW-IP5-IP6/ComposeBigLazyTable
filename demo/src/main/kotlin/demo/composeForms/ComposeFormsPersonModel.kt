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

package demo.composeForms

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageBitmapConfig
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import composeForms.convertibles.CustomConvertible
import composeForms.convertibles.ReplacementPair
import composeForms.model.BaseModel
import composeForms.model.attributes.*
import composeForms.model.formatter.DateFormatter
import composeForms.model.meanings.Currency
import composeForms.model.meanings.CustomMeaning
import composeForms.model.modelElements.Field
import composeForms.model.modelElements.FieldSize
import composeForms.model.modelElements.Group
import composeForms.model.modelElements.HeaderGroup
import composeForms.model.validators.ValidatorType
import composeForms.model.validators.semanticValidators.FloatingPointValidator
import composeForms.model.validators.semanticValidators.NumberValidator
import composeForms.model.validators.semanticValidators.RegexValidator
import composeForms.model.validators.semanticValidators.SelectionValidator
import java.net.URL
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.*
import javax.net.ssl.HttpsURLConnection


/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
class ComposeFormsPersonModel : BaseModel<ComposeFormsPersonLabels>(title = ComposeFormsPersonLabels.TITLE, smartphoneOption = true, wizardMode = false) {

    //*****************************************************************************************************************
    // Create attributes: (required)


    val dateConvertibles = listOf(
        CustomConvertible(listOf(ReplacementPair("(\\d{2})(/)(\\d{2})(/)(\\d{4})", "$1.$3.$5"))),
        CustomConvertible(listOf(ReplacementPair("now", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))), convertImmediately = true)
    )

    val dateValidator = RegexValidator<String, ComposeFormsPersonLabels>(
        regexPattern = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$",
        rightTrackRegexPattern = "^\\d{1,2}\\.\\d{1,2}\\.\\d{0,4}\$|^\\d{1,2}\\.\\d{1,2}\\.\$|^\\d{1,2}\\.\\d{1,2}\$|^\\d{1,2}\\.\$|^\\d{0,2}\$",
        validationMessage = ComposeFormsPersonLabels.DATEVALIDATOR)

    //*********************************
    //Personal information

    val firstName       = StringAttribute(model = this, label = ComposeFormsPersonLabels.FIRSTNAME, required = true)

    val lastName        = StringAttribute(model = this, label = ComposeFormsPersonLabels.LASTNAME, required = true)

    val birthDate       = StringAttribute(this, ComposeFormsPersonLabels.BIRTHDATE,
                            validators = listOf(dateValidator),
                            convertibles = dateConvertibles,
                            formatter = DateFormatter()
    )

    val height          = DoubleAttribute(this, ComposeFormsPersonLabels.HEIGHT,
                            meaning = CustomMeaning("m"),
                            validators = listOf(FloatingPointValidator(2, ComposeFormsPersonLabels.FLOATINGPOINTVALIDATIONMSG),
                                NumberValidator(lowerBound = 0.0, upperBound = 3.0)
                            ),
                            convertibles = listOf(
                                CustomConvertible(
                                replaceRegex = listOf(ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")),
                                convertUserView = false,
                                convertImmediately = true
                            )
                            ))

    val married         = BooleanAttribute(this, ComposeFormsPersonLabels.MARRIED , trueText = ComposeFormsPersonLabels.SELECTIONYES,
                            falseText = ComposeFormsPersonLabels.SELECTIONNO,
                            readOnly = false)


    val gender          = SelectionAttribute(this, ComposeFormsPersonLabels.GENDER,
                            possibleSelections = listOf(ComposeFormsPersonLabels.SELECTIONMAN, ComposeFormsPersonLabels.SELECTIONWOMAN, ComposeFormsPersonLabels.SELECTIONOTHER),
                            readOnly = false,
                            validators = listOf(
                                SelectionValidator(0,1, ComposeFormsPersonLabels.SELECTIONVALIDATIONMSG)
                            ))

    val imageUrl        = StringAttribute(model = this, label = ComposeFormsPersonLabels.IMAGE_URL)

    val lastOccupation  = StringAttribute(this, ComposeFormsPersonLabels.LASTOCCUPATION)

    val taxNumber       = IntegerAttribute(model = this, label = ComposeFormsPersonLabels.TAXNUMBER,
                            observedAttributes = listOf(
                                lastOccupation addOnChangeListener { taxAttr, occValue -> taxAttr.setRequired(occValue != null)}
                            ))

    //*********************************
    //Adress infos

    val postCode        = IntegerAttribute(this, ComposeFormsPersonLabels.POSTCODE,
                            validators = listOf(RegexValidator(
                                regexPattern = ".{3,5}", rightTrackRegexPattern = ".{0,5}",
                                validationMessage = ComposeFormsPersonLabels.REGEXVALIDATIONMSG)
                            ))

    val place           = StringAttribute(this, ComposeFormsPersonLabels.PLACE)
    val street          = StringAttribute(this, ComposeFormsPersonLabels.STREET)
    val houseNumber     = ShortAttribute(this, ComposeFormsPersonLabels.HOUSENUMBER)

    //*********************************
    //Business details

    val state           = DecisionAttribute(this, ComposeFormsPersonLabels.STATE,
                            decisionText1 = ComposeFormsPersonLabels.SELECTIONNORMAL,
                            decisionText2 = ComposeFormsPersonLabels.SELECTIONKADER,
                            readOnly = false)

    val startOfContract = StringAttribute(this, ComposeFormsPersonLabels.CONTRACTSTART,
                            validators = listOf(dateValidator),
                            convertibles = dateConvertibles,
                            formatter = DateFormatter()
                            )

    val wage            = DoubleAttribute(this, ComposeFormsPersonLabels.WAGE,
                            meaning = Currency(java.util.Currency.getInstance("EUR")), required = true,
                            validators = listOf(NumberValidator(lowerBound = 0.0), FloatingPointValidator(decimalPlaces = 1)))

    val department      = StringAttribute(model = this, label = ComposeFormsPersonLabels.DEPARTMENT)

    val employmentTitle = StringAttribute(this, ComposeFormsPersonLabels.EMPOYMENTTITLE)

    val contractWasSigned = BooleanAttribute(this, ComposeFormsPersonLabels.CONTRACTWASSIGNED,
                            trueText = ComposeFormsPersonLabels.SELECTIONYES, falseText = ComposeFormsPersonLabels.SELECTIONNO)


    //*********************************
    //additional attributes (for header group)

    val id              = IntegerAttribute(model = this, label = ComposeFormsPersonLabels.ID,
                            value = Random().nextInt(20), readOnly = true)

    val age             = LongAttribute(this, ComposeFormsPersonLabels.AGE,
        observedAttributes = listOf(
            birthDate addOnChangeListener { ageAttr, birthDateVal -> ageAttr.setValueAsText(updateBirthDate(birthDateVal)) }
        ),
        tableColumnWidth = 150.dp)


    //*****************************************************************************************************************
    //position attributes: (required)

    val headerGroup = HeaderGroup(model = this, title = ComposeFormsPersonLabels.HEADERGROUP,
        Field(firstName, FieldSize.SMALL),
        Field(lastName, FieldSize.SMALL),
        Field(id, FieldSize.SMALL),
        Field(age, FieldSize.SMALL),
        Field(wage, FieldSize.SMALL),
        rightSideHeader = {PersonPicture()}
    )

    val group1 = Group(model = this, title = ComposeFormsPersonLabels.GROUPTITLEPERSINFO,
        Field(firstName),
        Field(lastName),
        Field(birthDate,          FieldSize.SMALL),
        Field(height,       FieldSize.SMALL),
        Field(married,      FieldSize.SMALL),
        Field(gender,       FieldSize.SMALL),
        Field(lastOccupation),
        Field(taxNumber),
        Field(imageUrl,     FieldSize.BIG),
    )

    val group2 = Group(model = this, title = ComposeFormsPersonLabels.GROUPTITLEADDRESS,
        Field(postCode),
        Field(place),
        Field(street),
        Field(houseNumber)
    )

    val group3 = Group(model = this, title = ComposeFormsPersonLabels.GROUPTITLEBUSINESSINFO,
        Field(state),
        Field(startOfContract),
        Field(wage),
        Field(department),
        Field(employmentTitle),
        Field(contractWasSigned)
    )


    //*****************************************************************************************************************
    //optional settings

    //choose initial language
    init{
        this.setCurrentLanguage("english")
    }

    //override validation-Messages of non-semantic-validators
    override fun getValidationMessageOfNonSemanticValidator(validator: ValidatorType): ComposeFormsPersonLabels? {
        return when(validator){
            ValidatorType.REQUIREDVALIDATOR -> ComposeFormsPersonLabels.REQUIREDVALIDATIONMSG
            ValidatorType.SYNTAXVALIDATOR -> ComposeFormsPersonLabels.SYNTAXVALIDATIONMSG
            ValidatorType.DUALVALIDATOR -> ComposeFormsPersonLabels.DUALVALIDATIONMSG
            else -> null
        }
    }

    //*****************************************************************************************************************
    //create some individual composeForms.ui-elements for the headergroup (optional)

    //mutable States to force recompose
    var personPicture : MutableState<ImageBitmap> = mutableStateOf(ImageBitmap( 256, 256, ImageBitmapConfig.Alpha8, colorSpace = ColorSpaces.LinearExtendedSrgb))
    val employmentTitleText = mutableStateOf(employmentTitle.getValue())

    //add Change-Listener
    init {
        imageUrl.addOnChangeListener{ _ -> loadImgFromUrl()}
        employmentTitle.addOnChangeListener{ _ -> employmentTitleText.value = employmentTitle.getValue()}
    }

    //create a composable function
    @Composable
    fun PersonPicture(){
        Column(modifier = Modifier.width(300.dp), horizontalAlignment = Alignment.CenterHorizontally){
            Card(modifier = Modifier.height(150.dp).fillMaxWidth().padding(top = 12.dp, end = 12.dp, bottom = 12.dp), shape = RoundedCornerShape(4.dp)){
                Image(
                    modifier = Modifier.fillMaxSize(),
                    bitmap = personPicture.value,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
            Text(if(employmentTitleText.value == null) "" else employmentTitleText.value!!)
        }
    }


    //some helper functions
    fun updateBirthDate(date : String?): String{

        if(birthDate.isValid() && date != null){
            try {
                val today = LocalDate.now()
                val localDate = date.split(".")
                val addition = if (localDate[2].length == 2){
                    if(localDate[2].toInt() <= today.year-2000){
                        "20"
                    }else{
                        "19"
                    }
                }else{
                    ""
                }
                val birthDay = LocalDate.parse("${localDate[0]}.${localDate[1]}.$addition${localDate[2]}", DateTimeFormatter.ofPattern("d.M.yyyy"))

                val age = Period.between(
                    birthDay,
                    today
                ).years
                return age.toString()
            }catch(e: Exception){
                e.printStackTrace()
            }
        }
        return ""
    }

    fun loadImgFromUrl(){
        if(imageUrl.getValue() != null) {
            try {
                with(URL(imageUrl.getValue()).openConnection() as HttpsURLConnection) {
                    println("Downloading")
                    connect()
                    val allBytes = inputStream.readBytes()
                    personPicture.value = org.jetbrains.skia.Image.makeFromEncoded(allBytes).asImageBitmap()
                }
            } catch (e: Exception) {
                personPicture.value = ImageBitmap( 256, 256, ImageBitmapConfig.Alpha8, colorSpace = ColorSpaces.LinearExtendedSrgb)
            }
        }
    }


}