package demo.mountainForm.model

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
import demo.mountainForm.service.MountainDTO
import demo.mountainForm.service.MountainService
import model.BaseModel
import model.attributes.DoubleAttribute
import model.attributes.LongAttribute
import model.attributes.SelectionAttribute
import model.attributes.StringAttribute
import model.meanings.CustomMeaning
import model.modelElements.Field
import model.modelElements.FieldSize
import model.modelElements.Group
import model.modelElements.HeaderGroup
import model.validators.semanticValidators.NumberValidator
import model.validators.semanticValidators.SelectionValidator
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

class MountainPM(var service : MountainService) : BaseModel<Labels>(title = Labels.TITLE, smartphoneOption = true) {

    private val BASE_URL = "https://dieterholz.github.io/StaticResources/mountainpictures/"
    private var currentMountain: MountainDTO

    init {
        val id = Random().nextInt(231).toLong()
        currentMountain = service.get(id)
    }

    //******************************************************************************************************************
    //use of Compose Forms Library

    private val id              = LongAttribute(  value = currentMountain.getId(),model = this, label = Labels.ID, readOnly = true)
    private val name            = StringAttribute(value = currentMountain.getName(), model = this, label = Labels.NAME, required = true)
    private val height          = DoubleAttribute(value = currentMountain.getHeight(), model = this, label = Labels.HEIGHT,
                                                meaning = CustomMeaning("m"), validators = listOf(NumberValidator(0.0, 6000.0)))

    private val type            = StringAttribute(value = currentMountain.getType(), model = this, label = Labels.TYPE)
    private val region          = StringAttribute(value = currentMountain.getRegion(), model = this, label = Labels.REGION)

    private val cantonsSwitzerland = listOf("AG", "AR", "AI", "BL", "BS", "BE", "FR", "GE", "GL", "GR", "JU", "LU", "NE", "NW", "OW", "SG", "SH", "SZ", "SO", "TG", "TI", "UR", "VD", "VS", "ZG", "ZH" )
    private val cantons         = SelectionAttribute(
                                                  value = setOf(getCantonByLabel(currentMountain.getCantons())),
                                     possibleSelections = cantonsSwitzerland.map{ getCantonByLabel(it)},
                                                  model = this, label = Labels.CANTONS,
                                             validators = listOf(SelectionValidator(maxNumberOfSelections = 1)))

    private val range           = StringAttribute(value = currentMountain.getRange(),
                                                  model = this, label = Labels.RANGE,
                                     observedAttributes = listOf(cantons addOnChangeListener{rangeAttr, cantonsVal ->
                                                            rangeAttr.setReadOnly(cantonsVal == setOf("AG"))
                                     })) //if the canton is AG, the rangeAttribute can no longer be edited

    private val isolation       = DoubleAttribute(value = currentMountain.getIsolation(), model = this, label = Labels.ISOLATON)
    private val isolationPoint  = StringAttribute(value = currentMountain.getIsolationPoint(), model = this, label = Labels.ISOLATIONPOINT,
                                     observedAttributes = listOf(isolation addOnChangeListener{isoPointAttr, isoVal ->
                                                            isoPointAttr.setRequired(isoVal != null)
                                     })) //IsolationPoint becomes mandatory as soon as a value is entered for isolation.

    private val prominence      = DoubleAttribute(value = currentMountain.getProminence(), model = this, label = Labels.PROMINENCE)
    private val prominencePoint = StringAttribute(value = currentMountain.getProminencePoint(), model = this, label = Labels.PROMINENCEPOINT)
    private val imageUrl        = StringAttribute(value = BASE_URL + currentMountain.getId().toString() + "-1.jpg",
                                                  model = this, label = Labels.IMAGEURL)

    private val imageCaption    = StringAttribute(value = currentMountain.getImageCaption(), model = this, label = Labels.IMAGECAPTION,
                                     observedAttributes = listOf(imageUrl addOnChangeListener{imgCAttr, imgUrlVal ->
                                                             imgCAttr.setRequired(imgUrlVal != null && !imgUrlVal.isBlank()
                                                                     && (imgUrlVal.startsWith("http:") ||
                                                                     imgUrlVal.startsWith("https:")))}
                                     )) //imageCaption becomes mandatory as soon as a value is entered in the imageURL.

    
    val headerGroup = HeaderGroup(model = this, title = Labels.HEADERGROUP,
        Field(name),
        Field(height),
        Field(isolationPoint),
        rightSideHeader = {MountainPicture()}
        )

    val group1 = Group(model = this, title = Labels.GROUP,
        Field(name           ),
        Field(height         , FieldSize.SMALL),
        Field(isolation      , FieldSize.SMALL),
        Field(prominence     , FieldSize.SMALL),
        Field(type           , FieldSize.SMALL),
        Field(isolationPoint ),
        Field(prominencePoint),
        Field(region         ),
        Field(cantons        ),
        Field(range          ),
        Field(imageUrl       , FieldSize.BIG),
        Field(imageCaption   , FieldSize.BIG)
    )

    override fun customSave() {
        service.save(toDTO())
    }

    //******************************************************************************************************************
    // for saving to csv

    fun toDTO(): MountainDTO {
        return MountainDTO(listOf(
            id              .getSavedValue().toString(),
            name            .getSavedValue().toString(),
            height          .getSavedValue().toString(),
            type            .getSavedValue().toString(),
            region          .getSavedValue().toString(),

            if(cantons.getSavedValue().toString() == "[]") ""
            else cantons     .getSavedValue().toString().substring(1,range.getSavedValue().toString().length-1), //Convert "[...]" to ...

            range           .getSavedValue().toString(),
            isolation       .getSavedValue().toString(),
            isolationPoint  .getSavedValue().toString(),
            prominence      .getSavedValue().toString(),
            prominencePoint .getSavedValue().toString(),
            imageCaption    .getSavedValue().toString()
        ))
    }

    //******************************************************************************************************************
    //create some individual ui-elements for the headergroup (optional)

    var mountainPicture : MutableState<ImageBitmap> = mutableStateOf(ImageBitmap( 256, 256, ImageBitmapConfig.Alpha8, colorSpace = ColorSpaces.LinearExtendedSrgb))
    val imageCaptionText = mutableStateOf(imageCaption.getValue())

    init {
        imageUrl.addOnChangeListener{ imgUrlValue -> loadImgFromUrl()}
        imageCaption.addOnChangeListener{ emplTVal -> imageCaptionText.value = imageCaption.getValue()}
    }

    fun loadImgFromUrl(){
        if(imageUrl.getValue() != null) {
            with(URL(imageUrl.getValue()).openConnection() as HttpsURLConnection) {
                try {
                    println("Downloading")
                    connect()
                    val allBytes = inputStream.readBytes()
                    mountainPicture.value = org.jetbrains.skija.Image.makeFromEncoded(allBytes).asImageBitmap()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @Composable
    fun MountainPicture(){
            Column(modifier = Modifier.width(300.dp), horizontalAlignment = Alignment.CenterHorizontally){
                Card(modifier = Modifier.height(150.dp).fillMaxWidth().padding(top = 12.dp, end = 12.dp, bottom = 12.dp), shape = RoundedCornerShape(4.dp)){
                    Image(modifier = Modifier.fillMaxSize(), bitmap = mountainPicture.value, contentDescription = "", contentScale = ContentScale.Crop,)
                }
                Text(if(imageCaptionText.value == null) "" else imageCaptionText.value!!)
            }
    }

    //******************************************************************************************************************
    //helper function

    private fun getCantonByLabel(string: String): Labels{

        return if(Labels.values().map{it.name}.contains("CANTON_$string")) {
            Labels.valueOf("CANTON_$string")
        }else{
            Labels.CANTON_EMPTY
        }
    }
}