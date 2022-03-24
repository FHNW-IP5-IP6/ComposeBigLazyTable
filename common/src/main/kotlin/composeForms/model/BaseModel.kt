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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import bigLazyTable.controller.AppState
import composeForms.communication.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import composeForms.model.attributes.*
import composeForms.model.modelElements.Group
import composeForms.model.modelElements.HeaderGroup
import composeForms.model.validators.ValidatorType
import composeForms.server.EmbeddedMqtt
import java.net.NetworkInterface
import java.util.*


/**
 * [BaseModel] is a implementation of [IModel] with all functions implemented.
 * It also provides a mqtt composeForms.server to allow a smartphone connection.
 * Inherit from this class and add attributes to it.
 *
 * @param title: Title as [L] for the form
 * @param smartphoneOption: starts the mqtt composeForms.server if the boolean is true
 * @param wizardMode : sets the option isWizardMode
 * @param tooltipAutoSave: [L] for auto save tooltip
 * @param tooltipReset: [L] for reset tooltip
 * @param tooltipSave: [L] for save tooltip
 * @param tooltipConnectSmartphone: [L] for connection to smartphone tooltip
 * @param tooltipUndo: [L] for undo tooltip
 * @param tooltipRedo: [L] for redo tooltip
 * @param tooltipPrevious: [L] for previous tooltip
 * @param tooltipSaveAndNext: [L] for save and next tooltip
 * @param tooltipSaveAndExit: [L] for save and exit tooltip
 *
 * @author Louisa Reinger, Steve Vogel
 */
abstract class BaseModel<L>(private val title: L,
                            private val smartphoneOption: Boolean       = false,
                            private val wizardMode : Boolean            = false,

                            private val tooltipAutoSave: L?             = null,
                            private val tooltipReset: L?                = null,
                            private val tooltipSave: L?                 = null,
                            private val tooltipConnectSmartphone: L?    = null,
                            private val tooltipUndo: L?                 = null,
                            private val tooltipRedo: L?                 = null,
                            private val tooltipPrevious: L?             = null,
                            private val tooltipSaveAndNext: L?          = null,
                            private val tooltipSaveAndExit: L?          = null,
                            private val tooltipMessage: L?              = null

                            ) : composeForms.model.IModel<L> where L: composeForms.model.ILabel, L: Enum<*> {

    //******************************************************************************************************************
    //Properties

    private var currentLanguage                              = mutableStateOf("")

    //Group Information
    private var currentGroupIndex                            = mutableStateOf(0)
    private var allGroups                                    = mutableStateListOf<Group<*>>()
    private val currentFocusedGroup                          = mutableStateOf<Group<*>?>(null)
    private var currentWizardGroup : MutableState<Group<*>?> = if(allGroups.isEmpty()) mutableStateOf(null)
                                                               else mutableStateOf(allGroups.filter{ it !is HeaderGroup }[currentGroupIndex.value])
    //(Auto) Save
    private var autoSave                                     = mutableStateOf(false)
    private var allChangedAttributes                         = mutableStateOf(allGroups.flatMap { it.getAttributes() }.filter { it.isChanged() })
    private var changesExist                                 = mutableStateOf(false)

    //Focus Handling
    private val currentFocusedAttribute                      = mutableStateOf<Attribute<*, *, *>?>(null)
    private var allFocusedAttributesOfCurrentView            = mutableSetOf<Attribute<*, *, *>>()
    private var focusBlocked : Boolean                       = false     //Used for blocking focus changes

    //Validation
    private val allAttributesAreValid                        = mutableStateOf(true)
    private val validForWizardGroup                          = mutableStateOf(true)
    private var allChangedAttributesAreValid                 = mutableStateOf(allChangedAttributes.value.all{it.isValid()})
    private var allFocusedAttributesOfCurrentViewAreValid    = mutableStateOf(allFocusedAttributesOfCurrentView.all{ it.isValid() })

    //Error Handling
    private val exception                                    = mutableStateOf<Exception?>(null)

    //Communication with smartphone app
    val mqttBroker                                           = "localhost"
    val mainTopic                                            = "/fhnwforms/"
    open val mqttConnector                                   = MqttConnector(mqttBroker, mainTopic)
    private val modelScope                                   = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    var startedUp                                            = false

    // Compose BigLazyTable
    var appState: AppState<BaseModel<*>>? = null
    abstract val displayedAttributesInTable: List<Attribute<*,*,*>>?
    abstract val id: Attribute<*,*,*>

    init{
        if (getPossibleLanguages().isNotEmpty()) {
            currentLanguage.value = getPossibleLanguages()[0]
        }
        startUpServer()
    }



    override fun getTitle(): String {
        println("getTitle is called") // 2 times
        return title.getLanguageStringFromLabel(title, getCurrentLanguage())
    }

    //******************************************************************************************************************
    //Group Information

    /**
     * This method adds a new group to the composeForms.model and reinitializes and validates the currentWizardGroup if it is set to null.
     *
     * @param group : Group to add
     */
    override fun addGroup(group: Group<*>) {
        allGroups.add(group)
        if(currentWizardGroup.value == null && getWizardGroups().isNotEmpty()) {
            currentWizardGroup.value = getWizardGroups()[currentGroupIndex.value]
            setValid()
        }
    }

    //****************************
    //Getter:

    override fun getAllGroups(): List<Group<*>> {
        return allGroups
    }

    override fun getCurrentGroupIndex(): Int {
        return currentGroupIndex.value
    }

    override fun getCurrentFocusedGroup(): Group<*>? {
        return currentFocusedGroup.value
    }

    override fun isLastWizardGroup() : Boolean{
        return currentGroupIndex.value == getWizardGroups().size - 1
    }

    override fun getCurrentWizardGroup(): Group<*>? {
        return currentWizardGroup.value
    }

    /**
     * Filters the list of all groups for wizards groups
     *
     * @return List<Group>: filtered List
     */
    private fun getWizardGroups(): List<Group<*>>{
        return allGroups.filter{ it !is HeaderGroup }
    }


    //******************************************************************************************************************
    //Reset

    /**
     * This method resets all attributes (or the attributes of the current Wizard-Group if wizard mode is activated)
     * if there is at leased one change.
     *
     * @return if the attributes had changes and were resetted or not : Boolean
     */
    override fun reset(): Boolean {
        return if(changesExist()){
            if(isWizardMode()){
                currentWizardGroup.value?.getAttributes()?.forEach{ it.reset() }
            }else{
                allGroups.forEach{it.getAttributes().forEach{ it.reset() }}
            }
            true
        }else{
            false
        }
    }

    //******************************************************************************************************************
    //Language

    /**
     * This method sets the currentLanguage. Updates language of all attributes
     *
     * @param lang : String
     */
    override fun setCurrentLanguage(lang: String){
        // TODO: Why is this function called twice when changing the language
        //  println("setCurrentLanguage is called with lang $lang")
        currentLanguage.value = lang
        allGroups.forEach { it.getAttributes().forEach{ attribute -> attribute.setCurrentLanguage(lang) }}
        getCurrentFocusedAttribute()?.let { publishAll(it) }
    }

    //****************************
    //Getter:

    override fun isCurrentLanguage(lang : String) : Boolean{
        return currentLanguage.value == lang
    }

    override fun getCurrentLanguage(): String {
        // TODO: Why is this function called all the time
        //  How to change this behaviour?
        //  val language by lazy { currentLanguage.value } has no effect - will still be printed million of times
        //  println("getCurrentLanguage is called & returns ${currentLanguage.value}")
        return currentLanguage.value
    }

    final override fun getPossibleLanguages(): List<String> {
        return title.getLanguagesDynamic()
    }

    //******************************************************************************************************************
    //Save

    /**
     * This method saves all attributes, if all attributes are valid.
     * If the wizard mode is turned on, saveAndNextWizardGroup() is called instead.
     *
     * @return if the attributes where saved or not : Boolean
     */
    override fun save(): Boolean {
        return if(isWizardMode()) {
            saveAndNextWizardGroup()
        }else {
            if (allAttributesAreValid()) {
                allGroups.forEach { it.getAttributes().forEach { it.save() } }
                customSave()
                true
            } else {
                false
            }
        }
    }


    /**
     * This function can be overridden to custom save the attributes.
     * E.g. for saving to a database.
     */
    open fun customSave(){
        //DEFAULT DO NOTHING
    }


    /**
     * This method changes the auto save state from on to off or vice versa.
     * It also updates the list of all changed unsaved attributes and saves all attributes in the list (if valid).
     */
    override fun changeAutoSave() {
        autoSave.value = !autoSave.value
        updateChanges()
        allChangedAttributes.value.forEach { it.save() }
    }

    //****************************
    //Internal functions:

    /**
     * This method updates the list of all changed unsaved attributes.
     * In addition, allChangedAttributesAreValid and if changesExist are updated.
     */
    override fun updateChanges(){
        // Compose BigLazyTable
        if (appState?.changedTableModels?.contains(this) == false) {
            appState?.changedTableModels?.add(this)
        }

        // Forms
        allChangedAttributes.value = allGroups.flatMap { it.getAttributes() }.filter { it.isChanged() }
        allChangedAttributesAreValid.value = getListOfChangedAttributes().all{it.isValid()}
        changesExist.value = allGroups.flatMap{it.getAttributes()}.any(Attribute<*,*,*>::isChanged)
    }

    //****************************
    //Getter:

    override fun isAutoSave(): Boolean {
        return autoSave.value
    }

    override fun changesExist() : Boolean{
        return changesExist.value
    }

    private fun getListOfChangedAttributes(): List<Attribute<*, *, *>> {
        return allChangedAttributes.value
    }

    //******************************************************************************************************************
    //Wizard-Mode

    /**
     * This method changes the current wizard group to the previous one if the current is not the first one.
     * setChangedForAllOrWizardGroup() is executed to update if there are changes on the current attributes.
     * setValid will be called if the group changes.
     * The [currentFocusedAttribute] property is set to the first attribute of the new current group.
     *
     * @return if the attributes where saved or not : Boolean
     */
    override fun previousWizardGroup() : Boolean {
        return if(currentGroupIndex.value> 0){
            currentGroupIndex.value -= 1
            currentWizardGroup.value = getWizardGroups()[currentGroupIndex.value]

            currentFocusedGroup.value = currentWizardGroup.value
            currentFocusedAttribute.value = currentWizardGroup.value?.getAttributes()?.first()

            setValid()
            true
        }else{
            false
        }
    }

    //****************************
    //Internal functions:

    /**
     * This method saves all attributes of the current Wizard-Group, if all attributes in the current wizard-group are valid.
     * If all could be saved the currentWizardGroup changes to the next one.
     * The new currentWizardGroup is validated.
     * The [currentFocusedAttribute] property is set to the first attribute of the new current group
     *
     * @return if the attributes where saved or not : Boolean
     */
    private fun saveAndNextWizardGroup(): Boolean {
        return if(isWizardMode() && isValidForWizardGroup()){
            currentWizardGroup.value?.getAttributes()?.forEach{ it.save() }
            customSave()
            if(currentGroupIndex.value < getWizardGroups().size-1){

                currentGroupIndex.value += 1
                currentWizardGroup.value = getWizardGroups()[currentGroupIndex.value]

                currentFocusedGroup.value = currentWizardGroup.value
                currentFocusedAttribute.value = currentWizardGroup.value?.getAttributes()?.first()

                allFocusedAttributesOfCurrentView = mutableSetOf()  // Reset the list of focused attributes
                setValid()                                          // Validate newly set wizard group
            }
            true
        }else{
            false
        }
    }

    //****************************
    //Getter:

    override fun isWizardMode(): Boolean {
        return wizardMode
    }


    //******************************************************************************************************************
    //Validation

    /**
     * This method checks if if all current attributes are valid.
     * If yes, [validForAll] (or [validForWizardGroup] if wizard mode is activated) is set true.
     * If not, [validForAll] (or [validForWizardGroup]) is set false.
     * It also sets if [allFocusedAttributesOfCurrentViewAreValid] and if [allChangedAttributesAreValid].
     */
    override fun setValid(){
        if(isWizardMode() && currentWizardGroup.value?.getAttributes() != null){
            validForWizardGroup.value = currentWizardGroup.value?.getAttributes()!!.all(Attribute<*,*,*>::isValid)
        }else{
            allAttributesAreValid.value = allGroups.flatMap{it.getAttributes()}.all(Attribute<*,*,*>::isValid)
        }
        allFocusedAttributesOfCurrentViewAreValid.value = getListOfFocusedAttributesOfCurrentView().all{it.isValid()}
        allChangedAttributesAreValid.value = getListOfChangedAttributes().all{it.isValid()}
    }

    //*********************************
    //Getter

    override fun allAttributesAreValid() : Boolean{
        return allAttributesAreValid.value
    }

    override fun allChangedAttributesAreValid(): Boolean {
        return allChangedAttributesAreValid.value
    }

    override fun allFocusedAttributesOfCurrentViewAreValid(): Boolean {
        return allFocusedAttributesOfCurrentViewAreValid.value
    }

    override fun isValidForWizardGroup() : Boolean{
        return validForWizardGroup.value
    }

    /**
     * This method returns a label for the validation message of a non-semantic validator.
     * The attribute will use this function to retrieve the labels for the messages.
     * By default, null is returned and the default internal english message is used.
     *
     * @param validator : ValidatorType
     * @return L?
     */
    override fun getValidationMessageOfNonSemanticValidator(validator: ValidatorType): L?{
        return null
    }


    //******************************************************************************************************************
    //Tooltips

    override fun getTooltipAutoSave(): String {
        return tooltipAutoSave?.getLanguageStringFromLabel(tooltipAutoSave, getCurrentLanguage()) ?: "Auto Save"
    }
    override fun getTooltipReset(): String{
        return tooltipReset?.getLanguageStringFromLabel(tooltipReset, getCurrentLanguage()) ?: "Reset"
    }
    override fun getTooltipSave(): String{
        return tooltipSave?.getLanguageStringFromLabel(tooltipSave, getCurrentLanguage()) ?: "Save"
    }
    override fun getTooltipConnectSmartphone(): String{
        return tooltipConnectSmartphone?.getLanguageStringFromLabel(tooltipConnectSmartphone, getCurrentLanguage()) ?: "Show QR-Code to connect smartphone"
    }
    override fun getTooltipUndo(): String{
        return tooltipUndo?.getLanguageStringFromLabel(tooltipUndo, getCurrentLanguage()) ?: "Undo"
    }
    override fun getTooltipRedo(): String{
        return tooltipRedo?.getLanguageStringFromLabel(tooltipRedo, getCurrentLanguage()) ?: "Redo"
    }
    override fun getTooltipPrevious(): String{
        return tooltipPrevious?.getLanguageStringFromLabel(tooltipPrevious, getCurrentLanguage()) ?: "Go to previous group"
    }
    override fun getTooltipSaveAndNext(): String{
        return tooltipSaveAndNext?.getLanguageStringFromLabel(tooltipSaveAndNext, getCurrentLanguage()) ?: "Save and go to next group"
    }
    override fun getTooltipSaveAndExit(): String{
        return tooltipSaveAndExit?.getLanguageStringFromLabel(tooltipSaveAndExit, getCurrentLanguage()) ?: "Save and exit group"
    }
    override fun getTooltipMessage(): String{
        return tooltipMessage?.getLanguageStringFromLabel(tooltipMessage, getCurrentLanguage()) ?: "Show error message"
    }

    //******************************************************************************************************************
    //Focus Handling

    /**
     * This method sets the current selected attribute and group if the focus change is not blocked by [focusBlocked]
     *
     * @param attr: Attribute
     * @param group: Group
     * @throws IllegalArgumentException for not existing attributes
     */
    override fun setCurrentFocusedAttribute(attr: Attribute<*, *, *>?, group: Group<*>?) {
        if(attr != null && group != null) {
            checkCurrentFocusIllegalState(attr, group)
        }

        if(!focusBlocked && group !is HeaderGroup) {
            if (attr != currentFocusedAttribute.value) {
                currentFocusedAttribute.value = attr
                if (attr != null) {
                    allFocusedAttributesOfCurrentView.add(attr)
                    allFocusedAttributesOfCurrentViewAreValid.value = getListOfFocusedAttributesOfCurrentView().all{it.isValid()}
                    publishAll(attr)
                }
            }
            if (group != currentFocusedGroup.value && (isWizardMode() && group != null || !isWizardMode())) {
                currentFocusedGroup.value = group
            }
        }
    }

    /**
     * This method focuses the next attribute if there is an already focused attribute.
     * If no attribute is selected the first one will be chosen.
     */
    override fun focusNext() {
        if(currentFocusedAttribute.value == null  && currentFocusedGroup.value == null){
            setCurrentFocusedAttribute(allGroups.filter { it !is HeaderGroup }.first().getAttributes().first(), allGroups.filter { it !is HeaderGroup }.first())
        }else{
            if(isWizardMode()){
                focusNextWizardMode()
            }else{
                focusNextNaturalMode()
            }
        }
    }

    /**
     * This method focuses the previous attribute if there is an already focused field.
     * If no attribute is selected the last one will be chosen.
     */
    override fun focusPrevious() {
        if(currentFocusedAttribute.value == null && currentFocusedGroup.value == null){
            if(isWizardMode()){
                setCurrentFocusedAttribute(getWizardGroups().first().getAttributes().last(), getWizardGroups().first())
            }else{
                setCurrentFocusedAttribute(allGroups.filter { it !is HeaderGroup }.last().getAttributes().last(), allGroups.filter { it !is HeaderGroup }.last())
            }

        }else{
            if(isWizardMode()){
                focusPreviousWizardMode()
            }else{
                focusPreviousNaturalMode()
            }
        }
    }

    override fun setFocusBlocked(block: Boolean){
        focusBlocked = block
    }

    //*********************************
    //Internal functions

    /**
     * Checking if [attr] is from this composeForms.model and in the [group]
     *
     * @param attr: Attribute
     * @param group : Group
     */
    private fun checkCurrentFocusIllegalState(attr: Attribute<*,*,*>, group: Group<*>){
        if (!allGroups.flatMap { it.getAttributes() }.contains(attr)) {
            exception.value = IllegalArgumentException("Model does not contain this Attribute")
            throw exception.value!!
        }
        if (!group.getAttributes().contains(attr)) {
            exception.value = IllegalArgumentException("Group does not contain this Attribute")
            throw exception.value!!
        }
    }

    /**
     * This method is for the wizard mode.
     * It sets the focus on the next attribute in the current group.
     */
    private fun focusNextWizardMode(){
        if(getCurrentFocusedGroup() in getWizardGroups()) {
            val attrIndex = currentWizardGroup.value?.getAttributes()?.indexOf(currentFocusedAttribute.value)
            if (attrIndex != null) {
                val size: Int = currentWizardGroup.value?.getAttributes()!!.size
                setCurrentFocusedAttribute(
                    currentWizardGroup.value?.getAttributes()?.get((attrIndex + 1) % size),
                    currentWizardGroup.value
                )
            }
        }else{
            val attrIndex = currentFocusedGroup.value?.getAttributes()?.indexOf(currentFocusedAttribute.value)
            if (attrIndex != null) {
                val size: Int = currentFocusedGroup.value?.getAttributes()!!.size
                if(attrIndex < size -1) {
                    setCurrentFocusedAttribute(
                        currentFocusedGroup.value?.getAttributes()?.get((attrIndex + 1)),
                        currentFocusedGroup.value
                    )
                }else{
                    setCurrentFocusedAttribute(
                        currentWizardGroup.value?.getAttributes()?.first(),
                        currentWizardGroup.value
                    )
                }
            }
        }
    }

    /**
     * This method is for the natural mode.
     * It sets the focus on the next attribute of all attributes
     */
    private fun focusNextNaturalMode(){
        val attrIndex = currentFocusedGroup.value?.getAttributes()?.indexOf(currentFocusedAttribute.value)
        if(attrIndex != null) {
            if (attrIndex + 1 == currentFocusedGroup.value?.getAttributes()?.size) {
                val groups = allGroups.filter{ it !is HeaderGroup}
                val groupIndex = groups.indexOf(currentFocusedGroup.value)
                val groupAfter = groups[(groupIndex +1) % groups.size]
                val attrsOfGroup = groupAfter.getAttributes()
                if(attrsOfGroup.isEmpty()){
                    setCurrentFocusedAttribute(null, groupAfter)
                }else{
                    setCurrentFocusedAttribute(attrsOfGroup.first(), groupAfter)
                }

            }else{
                setCurrentFocusedAttribute(currentFocusedGroup.value?.getAttributes()?.get(attrIndex +1), currentFocusedGroup.value)
            }
        }
    }


    /**
     * This method is for the wizard mode.
     * It sets the focus on the previous attribute in the current group.
     */
    private fun focusPreviousWizardMode(){
        if(getCurrentFocusedGroup() in getWizardGroups()) {
            val attrIndex = currentWizardGroup.value?.getAttributes()?.indexOf(currentFocusedAttribute.value)
            if (attrIndex != null && attrIndex >= 0) {
                val size: Int = currentWizardGroup.value?.getAttributes()!!.size
                setCurrentFocusedAttribute(
                    currentWizardGroup.value?.getAttributes()?.get((attrIndex - 1 + size) % size),
                    currentWizardGroup.value
                )
            }else{
                setCurrentFocusedAttribute(
                    currentWizardGroup.value?.getAttributes()?.last(),
                    currentWizardGroup.value
                )
            }
        }else{
            val attrIndex = currentFocusedGroup.value?.getAttributes()?.indexOf(currentFocusedAttribute.value)
            if (attrIndex != null) {
                if(attrIndex > 0) {
                    setCurrentFocusedAttribute(
                        currentFocusedGroup.value?.getAttributes()?.get((attrIndex - 1)),
                        currentFocusedGroup.value
                    )
                }else{
                    setCurrentFocusedAttribute(
                        currentWizardGroup.value?.getAttributes()?.last(),
                        currentWizardGroup.value
                    )
                }
            }
        }
    }

    /**
     * This method is for the natural mode.
     * It sets the focus on the previous attribute of all attributes
     */
    private fun focusPreviousNaturalMode(){
        val attrIndex = currentFocusedGroup.value?.getAttributes()?.indexOf(currentFocusedAttribute.value)
        if(attrIndex != null) {
            if (attrIndex == 0 || attrIndex == -1) {
                val groups = allGroups.filter{ it !is HeaderGroup}
                val groupIndex = groups.indexOf(currentFocusedGroup.value)
                val groupBefore = groups[(groupIndex - 1 + groups.size) % groups.size]
                val attrsOfGroup = groupBefore.getAttributes()
                if(attrsOfGroup.isEmpty()){
                    setCurrentFocusedAttribute(null, groupBefore)
                }else{
                    setCurrentFocusedAttribute(attrsOfGroup.last(), groupBefore)
                }

            }else{
                setCurrentFocusedAttribute(currentFocusedGroup.value?.getAttributes()?.get(attrIndex - 1 ) , currentFocusedGroup.value)
            }
        }
    }

    //*********************************
    //Getter:

    override fun getCurrentFocusedAttribute(): Attribute<*, *, *>? {
        return currentFocusedAttribute.value
    }

    private fun getListOfFocusedAttributesOfCurrentView(): Set<Attribute<*, *, *>> {
        return allFocusedAttributesOfCurrentView
    }

    //******************************************************************************************************************
    //Communication with smartphone app

    //*********************************
    //Publish a specific topic

    /**
     * Publish the valueAsText of an attribute as a DTOText to the subtopic "text" if the attribute is the current selected.
     *
     * @param attr: Attribute used for getting the text
     */
    override fun publishText(attr: Attribute<*, *, *>){
        if(attr == getCurrentFocusedAttribute()) {
            val dtoText = DTOText(attr.getId(), attr.getValueAsText())
            val string = Json.encodeToString(dtoText)

            mqttConnector.publish(
                message = string,
                subtopic = "text",
                onPublished = { println("Sent Text:" + string) })
        }
    }

    /**
     * Publish the attribute as DTOAttribute to the subtopic "attribute".
     *
     * @param attr: Attribute that has to be published
     */
    override fun publishAttribute(attr: Attribute<*, *, *>) {
        val posSelection = attr.getPossibleSelections().map{ it.toString() }.toMutableList()
        if(attr is SelectionAttribute<*>){
            posSelection.clear()
            attr.getPossibleSelections().first().forEach {
                posSelection.add(it.toString())
            }
            attr.getPossibleSelections().first().forEach {
                posSelection.add((it).getLanguageStringFromLabel(it as Enum<*>, getCurrentLanguage()))
            }
        }
        val dtoAttr = DTOAttribute(attr.getId(), attr.getLabel(), getAttributeType(attr), posSelection,
            attr.convertibles, attr.isReadOnly(), attr.isRequired(), attr.meaning.addMeaning(attr.getValueAsText()))
        val string = Json.encodeToString(dtoAttr)

        mqttConnector.publish(
            message = string,
            subtopic = "attribute",
            onPublished = { println("Sent Attr Changed:" + string) })
    }

    /**
     * Publishing the validation result of an attribute as DTOValidation to the subtopic "validation" if the attribute
     * is the current selected.
     *
     * @param attr: Attribute which validation changed
     */
    override fun publishValidation(attr: Attribute<*, *, *>) {
        if(attr == getCurrentFocusedAttribute()) {
            val dtoValidation = DTOValidation(
                attr.isRightTrackValid(), attr.isValid(),
                attr.getErrorMessages(), attr.isUndoable(),
                attr.isRedoable()
            )
            val string = Json.encodeToString(dtoValidation)

            mqttConnector.publish(
                message = string,
                subtopic = "validation",
                onPublished = { println("sent validation for ${attr.getLabel()} with test ${attr.getValueAsText()}: " + string) })
        }
    }

    //*********************************
    //Internal functions

    /**
     * This method starts an embeded MQTT composeForms.server in a separate scope.
     */
    private fun startUpServer(){
        if(smartphoneOption)
            modelScope.launch {
                if(!startedUp) {
                    startedUp = true
                    EmbeddedMqtt.start()
                    connectAndSubscribe()
                }
            }
    }

    /**
     * Publishing all information
     *
     * @param attr: Attribute whose changes are sent.
     */
    private fun publishAll(attr: Attribute<*, *, *>){
        publishAttribute(attr)
        publishText(attr)
        publishValidation(attr)
    }


    /**
     * Call [MqttConnector.connect] to connect to the composeForms.server and subscribe the subtopics text and command.
     */
    internal fun connectAndSubscribe(){
        mqttConnector.connect(
            onConnectionFailed = { println("Could not connect to service")},
            onConnected = { subscribeToTextAndCommand() })
    }


    /**
     * Subscribe to all channels that the composeForms.model is listening to and set the corresponding function calls
     */
    private fun subscribeToTextAndCommand(){
        mqttConnector.subscribe(
            subtopic        = "text",
            onNewMessage    = { onReceivedText(it) })

        mqttConnector.subscribe(
            subtopic        = "command",
            onNewMessage    = { onReceivedCommand(it) })
    }

    /**
     * Function that sets the valueAsText for the attribute with the id given in dtoTextAsString.
     * Will not publish text change
     *
     * @param dtoTextAsString: DTOText as JSON String
     */
    internal fun onReceivedText(dtoTextAsString: String) {
        val dtoText = Json.decodeFromString<DTOText>(dtoTextAsString)
        getAttributeById(dtoText.id)?.setValueAsText(dtoText.text, false)
    }

    /**
     * This method handles a commands from a string
     *
     * @param dtoCommandAsString: DTOCommand as JSON String
     */
    internal fun onReceivedCommand(dtoCommandAsString: String) {
        val dtoCommand = Json.decodeFromString<DTOCommand>(dtoCommandAsString)

        when(dtoCommand.command){
            Command.NEXT -> focusNext()
            Command.PREVIOUS -> focusPrevious()
            Command.REQUEST -> {
                val attr: Attribute<*, *, *>? = getCurrentFocusedAttribute()
                if(attr != null) {
                    publishAll(attr)
                }
            }
            Command.UNDO -> currentFocusedAttribute.value?.undo()
            Command.REDO -> currentFocusedAttribute.value?.redo()
        }
    }

    //*********************************
    //Getter:

    override fun isSmartphoneOption(): Boolean {
        return smartphoneOption
    }

    /**
     * This method returns the attribute with the searched id.
     *
     * @param id: Int?
     */
    override fun getAttributeById(id: Int?): Attribute<*, *, *>?{
        return getAllGroups().flatMap{it.getAttributes()}.find{it.getId() == id}
    }

    /**
     * This method finds out the IP address of the used desktop device.
     * The first found running network interface with an IPv4 will be taken.
     *
     * @return IPv4 address
     * @throws RuntimeException if no address is found
     */
    override fun getIPAdress(): String {
        // Filter only addresses that are running and open
        val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces()).filter{
            !(it.isLoopback || !it.isUp || it.isVirtual || it.displayName.lowercase(Locale.getDefault()).contains("virtual"))
        }
        for(interFace: NetworkInterface in interfaces){
            val addresses = interFace.inetAddresses()
            for(addr in addresses){
                val sAddr = addr.hostAddress
                val isIPv4 = sAddr.indexOf(':')<0

                if(isIPv4) {
                    return sAddr
                }
            }
        }
        throw RuntimeException("IP not found")
    }

    //******************************************************************************************************************
    //Error Handling

    //*********************************
    //Getter:

    override fun hasException(): Boolean {
        return exception.value != null
    }

    override fun getException(): Exception? {
        return exception.value
    }

    override fun setException(e: Exception) {
        exception.value = e
    }

    //******************************************************************************************************************
    //Helper Function

    internal fun getAttributeType(attr: Attribute<*, *, *>): AttributeType {
        return when (attr){
            is DoubleAttribute      -> AttributeType.DOUBLE
            is FloatAttribute       -> AttributeType.FLOAT
            is IntegerAttribute     -> AttributeType.INTEGER
            is LongAttribute        -> AttributeType.LONG
            is SelectionAttribute   -> AttributeType.SELECTION
            is ShortAttribute       -> AttributeType.SHORT
            is StringAttribute      -> AttributeType.STRING
            is DecisionAttribute    -> AttributeType.DECISION
            is BooleanAttribute     -> AttributeType.BOOLEAN
            else                    -> AttributeType.OTHER
        }
    }
}