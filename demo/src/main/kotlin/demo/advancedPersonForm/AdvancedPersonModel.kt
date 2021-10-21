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

package demo.advancedPersonForm

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
import convertibles.CustomConvertible
import convertibles.ReplacementPair
import model.BaseModel
import model.attributes.*
import model.formatter.DateFormatter
import model.meanings.Currency
import model.meanings.CustomMeaning
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.Group
import model.modelElements.HeaderGroup
import model.validators.ValidatorType
import model.validators.semanticValidators.FloatingPointValidator
import model.validators.semanticValidators.NumberValidator
import model.validators.semanticValidators.RegexValidator
import model.validators.semanticValidators.SelectionValidator
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
class AdvancedPersonModel : BaseModel<AdvancedPersonLabels>(title = AdvancedPersonLabels.TITLE, smartphoneOption = true, wizardMode = false) {

    //*****************************************************************************************************************
    // Create attributes: (required)


    val dateConvertibles = listOf(
        CustomConvertible(listOf(ReplacementPair("(\\d{2})(/)(\\d{2})(/)(\\d{4})", "$1.$3.$5"))),
        CustomConvertible(listOf(ReplacementPair("now", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")))), convertImmediately = true))

    val dateValidator = RegexValidator<String, AdvancedPersonLabels>(
        regexPattern = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})\$",
        rightTrackRegexPattern = "^\\d{1,2}\\.\\d{1,2}\\.\\d{0,4}\$|^\\d{1,2}\\.\\d{1,2}\\.\$|^\\d{1,2}\\.\\d{1,2}\$|^\\d{1,2}\\.\$|^\\d{0,2}\$",
        validationMessage = AdvancedPersonLabels.DATEVALIDATOR)

    //*********************************
    //Personal information

    val firstName       = StringAttribute(model = this, label = AdvancedPersonLabels.FIRSTNAME, required = true)

    val lastName        = StringAttribute(model = this, label = AdvancedPersonLabels.LASTNAME, required = true)

    val birthDate       = StringAttribute(this, AdvancedPersonLabels.BIRTHDATE,
                            validators = listOf(dateValidator),
                            convertibles = dateConvertibles,
                            formatter = DateFormatter()
    )

    val height          = DoubleAttribute(this, AdvancedPersonLabels.HEIGHT,
                            meaning = CustomMeaning("m"),
                            validators = listOf(FloatingPointValidator(2, AdvancedPersonLabels.FLOATINGPOINTVALIDATIONMSG),
                                NumberValidator(lowerBound = 0.0, upperBound = 3.0)
                            ),
                            convertibles = listOf(CustomConvertible(
                                replaceRegex = listOf(ReplacementPair("(\\d*)(,)(\\d*)", "$1.$3")),
                                convertUserView = false,
                                convertImmediately = true
                            )))

    val married         = BooleanAttribute(this, AdvancedPersonLabels.MARRIED , trueText = AdvancedPersonLabels.SELECTIONYES,
                            falseText = AdvancedPersonLabels.SELECTIONNO,
                            readOnly = false)


    val gender          = SelectionAttribute(this, AdvancedPersonLabels.GENDER,
                            possibleSelections = listOf(AdvancedPersonLabels.SELECTIONMAN, AdvancedPersonLabels.SELECTIONWOMAN, AdvancedPersonLabels.SELECTIONOTHER),
                            readOnly = false,
                            validators = listOf(
                                SelectionValidator(0,1, AdvancedPersonLabels.SELECTIONVALIDATIONMSG)
                            ))

    val imageUrl        = StringAttribute(model = this, label = AdvancedPersonLabels.IMAGE_URL)

    val lastOccupation  = StringAttribute(this, AdvancedPersonLabels.LASTOCCUPATION)

    val taxNumber       = IntegerAttribute(model = this, label = AdvancedPersonLabels.TAXNUMBER,
                            observedAttributes = listOf(
                                lastOccupation addOnChangeListener { taxAttr, occValue -> taxAttr.setRequired(occValue != null)}
                            ))

    //*********************************
    //Adress infos

    val postCode        = IntegerAttribute(this, AdvancedPersonLabels.POSTCODE,
                            validators = listOf(RegexValidator(
                                regexPattern = ".{3,5}", rightTrackRegexPattern = ".{0,5}",
                                validationMessage = AdvancedPersonLabels.REGEXVALIDATIONMSG)
                            ))

    val place           = StringAttribute(this, AdvancedPersonLabels.PLACE)
    val street          = StringAttribute(this, AdvancedPersonLabels.STREET)
    val houseNumber     = ShortAttribute(this, AdvancedPersonLabels.HOUSENUMBER)

    //*********************************
    //Business details

    val state           = DecisionAttribute(this, AdvancedPersonLabels.STATE,
                            decisionText1 = AdvancedPersonLabels.SELECTIONNORMAL,
                            decisionText2 = AdvancedPersonLabels.SELECTIONKADER,
                            readOnly = false)

    val startOfContract = StringAttribute(this, AdvancedPersonLabels.CONTRACTSTART,
                            validators = listOf(dateValidator),
                            convertibles = dateConvertibles,
                            formatter = DateFormatter()
                            )

    val wage            = DoubleAttribute(this, AdvancedPersonLabels.WAGE,
                            meaning = Currency(java.util.Currency.getInstance("EUR")), required = true,
                            validators = listOf(NumberValidator(lowerBound = 0.0), FloatingPointValidator(decimalPlaces = 1)))

    val department      = StringAttribute(model = this, label = AdvancedPersonLabels.DEPARTMENT)

    val employmentTitle = StringAttribute(this, AdvancedPersonLabels.EMPOYMENTTITLE)

    val contractWasSigned = BooleanAttribute(this, AdvancedPersonLabels.CONTRACTWASSIGNED,
                            trueText = AdvancedPersonLabels.SELECTIONYES, falseText = AdvancedPersonLabels.SELECTIONNO)


    //*********************************
    //additional attributes (for header group)

    val id              = IntegerAttribute(model = this, label = AdvancedPersonLabels.ID,
                            value = Random().nextInt(20), readOnly = true)

    val age             = LongAttribute(this, AdvancedPersonLabels.AGE,
                            observedAttributes = listOf(
                                birthDate addOnChangeListener { ageAttr, birthDateVal -> ageAttr.setValueAsText(updateBirthDate(birthDateVal)) }
                            ))


    //*****************************************************************************************************************
    //position attributes: (required)

    val headerGroup = HeaderGroup(model = this, title = AdvancedPersonLabels.HEADERGROUP,
        Field(firstName, FieldSize.SMALL),
        Field(lastName, FieldSize.SMALL),
        Field(id, FieldSize.SMALL),
        Field(age, FieldSize.SMALL),
        Field(wage, FieldSize.SMALL),
        rightSideHeader = {PersonPicture()}
    )

    val group1 = Group(model = this, title = AdvancedPersonLabels.GROUPTITLEPERSINFO,
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

    val group2 = Group(model = this, title = AdvancedPersonLabels.GROUPTITLEADDRESS,
        Field(postCode),
        Field(place),
        Field(street),
        Field(houseNumber)
    )

    val group3 = Group(model = this, title = AdvancedPersonLabels.GROUPTITLEBUSINESSINFO,
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
    override fun getValidationMessageOfNonSemanticValidator(validator: ValidatorType): AdvancedPersonLabels? {
        return when(validator){
            ValidatorType.REQUIREDVALIDATOR -> AdvancedPersonLabels.REQUIREDVALIDATIONMSG
            ValidatorType.SYNTAXVALIDATOR -> AdvancedPersonLabels.SYNTAXVALIDATIONMSG
            ValidatorType.DUALVALIDATOR -> AdvancedPersonLabels.DUALVALIDATIONMSG
            else -> null
        }
    }

    //*****************************************************************************************************************
    //create some individual ui-elements for the headergroup (optional)

    //mutable States to force recompose
    var personPicture : MutableState<ImageBitmap> = mutableStateOf(ImageBitmap( 256, 256, ImageBitmapConfig.Alpha8, colorSpace = ColorSpaces.LinearExtendedSrgb))
    val employmentTitleText = mutableStateOf(employmentTitle.getValue())

    //add Change-Listener
    init {
        imageUrl.addOnChangeListener{ imgUrlValue -> loadImgFromUrl()}
        employmentTitle.addOnChangeListener{ emplTVal -> employmentTitleText.value = employmentTitle.getValue()}
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
                    personPicture.value = org.jetbrains.skija.Image.makeFromEncoded(allBytes).asImageBitmap()
                }
            } catch (e: Exception) {
                personPicture.value = ImageBitmap( 256, 256, ImageBitmapConfig.Alpha8, colorSpace = ColorSpaces.LinearExtendedSrgb)
            }
        }
    }


}