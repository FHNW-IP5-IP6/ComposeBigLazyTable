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

package model.attributes

import androidx.compose.runtime.mutableStateOf
import composeForms.convertibles.*
import kotlinx.coroutines.*
import model.ILabel
import model.IModel
import model.formatter.IFormatter
import model.meanings.SemanticMeaning
import model.validators.RequiredValidator
import model.validators.SyntaxValidator
import model.validators.ValidationResult
import model.validators.ValidatorType
import model.validators.semanticValidators.SemanticValidator
import composeForms.util.Utilities
import java.util.*

/**
 * An [Attribute] is a reflection of an object of a certain type [T] with all values and information needed for interaction.
 * The [Attribute] is an abstract class. It has implemented all functions that an attribute needs.
 * This Attribute class inherits its functionality to all other type-specific attributes.
 * The type-specific attributes implement the [typeT] property and have useful default constructor parameters that are
 * matching the type.
 *
 * @param model: the model that the attribute belongs to
 * @param label: [L] (ILable enum entry) where a string for each language can be defined.
 * @param value: initial value
 * @param required: if a value is required
 * @param readOnly: if the value is read only or writeable
 * @param observedAttributes: List of functions that are executed if the values of the observed attributes change.
 * @param validators: List of [SemanticValidator]s that are used for the validation of the user input ([valueAsText]).
 * @param convertibles: List of [CustomConvertible]s that are used to convert a not type-matching [valueAsText] (String)
 * into a type-matching String (that can be converted into the type [T] of the attribute).
 * @param meaning: [SemanticMeaning] used to add a meaning to the value
 * @param formatter: [IFormatter] that formats the value into a different user view.
 *
 * @author Louisa Reinger, Steve Vogel
 */
abstract class Attribute <A,T,L> (
    //required parameters
    private val model                       : IModel<L>,
    label                                   : L,

    //optional parameters
    private var value                       : T?,
    required                                : Boolean,
    readOnly                                : Boolean,
    private var observedAttributes          : List<(Attribute<*, *, *>) -> Unit>,
    var validators                          : List<SemanticValidator<T, L>>,
    var convertibles                        : List<CustomConvertible>,
    var meaning                             : SemanticMeaning<T>,
    var formatter                           : IFormatter<T>?

) where A : Attribute<A, T, L>, T : Any?, L : ILabel, L: Enum<*> {

    //******************************************************************************************************************
    //Properties

    //User Input
    private val valueAsText                 = mutableStateOf(value?.toString() ?: "")

    //Save
    private var savedValue                  = value
    private val changed                     = mutableStateOf(false)

    //Undo, Redo & Reset
    private var isUndoable                  = mutableStateOf(false)
    private var isRedoable                  = mutableStateOf(false)
    private var undoStack: Stack<String>    = Stack()
    private var redoStack: Stack<String>    = Stack()
    private val undoScope: CoroutineScope   = CoroutineScope(Dispatchers.Default)
    private var job: Job                    = Job()
    private val typingDelay                 = 350L

    //On-Change listener
    private var onChangeListenersOfThis     = mutableListOf<(T?) -> Unit>()

    //Validation
    private val valid                       = mutableStateOf(true)
    private val rightTrackValid             = mutableStateOf(true)
    private val rightTrackValue             = mutableStateOf(value)
    private val listOfValidationResults     = mutableStateOf<List<ValidationResult<L>>>(emptyList())
    private val reqValidator                = RequiredValidator<T, L>(required, validationMessage = model.getValidationMessageOfNonSemanticValidator(ValidatorType.REQUIREDVALIDATOR))
    private val syntaxValidator             = SyntaxValidator<T, L>(validationMessage = model.getValidationMessageOfNonSemanticValidator(ValidatorType.SYNTAXVALIDATOR))

    //Convertibles
    private val convertible                 = mutableStateOf(false)
    private val listOfConvertibleResults    = mutableStateOf<List<ConvertibleResult>>(emptyList())

    //Remaining attribute properties
    private val id                          = getNextId()
    private val label                       = label
    private val labelAsText                 = mutableStateOf("")
    private val required                    = mutableStateOf(required)
    private val readOnly                    = mutableStateOf(readOnly)

    /**
     *  Helper Property to find out the type of an attribute
     */
    abstract val typeT : T



    /**
     * Initial setup of the attribute.
     * The attribute sets up the undo functionality and performs the initial validation.
     * It adds itself to the validators to be notified of validator updates.
     */
    init{
        undoStack.push(valueAsText.value)
        setUndobale()
        validators.forEach{it.addAttribute(this)}
        reqValidator.addAttribute(this)
        syntaxValidator.addAttribute(this)
        checkAndSetValue(getValueAsText())
        setThisAsListenerOnOtherAttributes()
    }


    //******************************************************************************************************************
    //User Input

    // TODO:
    fun setValue() {
        TODO()
    }

    /**
     * If the attribute is not readonly, valueAsText is set to the new input value.
     * This method also handles the undo and redo lists, checks if there are any changes compared to the savedValue
     * and validates the new input value.
     * The valueAsText is also published if the parameter doPublish is set.
     *
     * @param valueAsText : new input value (String) to be set for valueAsText.
     * @param doPublish : if true then the new valueAsText will be published
     * @param addToUndoStack : if true the new valueAsText is added to the undo stack
     * @param clearRedo : if true redo will be reset
     */
    fun setValueAsText(valueAsText : String, doPublish: Boolean = true, addToUndoStack: Boolean = true, clearRedo: Boolean = true){
        if(!isReadOnly()){
            if(addToUndoStack && this.valueAsText.value != valueAsText){
                addStringToUndoStack(valueAsText)
            }
            if(clearRedo){
                redoStack.clear()
                setRedoable()
            }
            this.valueAsText.value = valueAsText
            setChanged(valueAsText)
            checkAndSetValue(valueAsText)
            if(doPublish) {
                model.publishText(this)
            }
            if(model.isAutoSave()){
                model.updateChanges()
            }
        }
    }

    //****************************
    //Getter:

    fun getValueAsText(): String {
        return valueAsText.value
    }

    //******************************************************************************************************************
    //Save

    /**
     * This method sets the savedValue to the current value and makes the attribute "not changed" again if the attribute
     * is valid. If the attribute is invalid nothing happens.
     *
     * @return if save was executed or not
     */
    fun save() : Boolean{
        if(isValid()){
            setSavedValue(getValue())
            setChanged(false)
            return true
        }else{
            return false
        }
    }

    //****************************
    //Internal functions:

    private fun setSavedValue(value: T?) {
        this.savedValue = value
    }

    /**
     * [setChanged] compares the [newVal] with the saved value.
     * If they are not equal the [changed] value will be set to true (otherwise to false).
     * Afterwards the model will be updated about the [changed] value.
     *
     * Special case [SelectionAttribute]: A different order of the elements will not be handled as a change.
     *
     * @param newVal : String
     */
    private fun setChanged(newVal: String) {
        if(this is SelectionAttribute){
            var set : Set<L>
            if(newVal.equals("[]")){
                set = emptySet()
            }else{
                set = convertStringToType(newVal)
            }
            this.changed.value = !(set.equals(getSavedValue()))
            if(!isChanged()){ // set value & savedValue to the new order, otherwise the user will be irritated if the order changes when reset is clicked
                checkAndSetValue(newVal)
                save()
            }
        }else{
            this.changed.value = !newVal.equals(getSavedValue().toString()) && !(newVal.equals("") && getSavedValue() == null)
        }
        model.updateChanges()
    }

    /**
     * This method sets the changed value and updates the model about the new changes.
     *
     * @param isChanged : Boolean which will be set for changed
     */
    private fun setChanged(isChanged : Boolean) {
        this.changed.value = isChanged
        model.updateChanges()
    }

    //****************************
    //Getter:

    fun getSavedValue() : T?{
        return savedValue
    }

    fun isChanged() : Boolean{
        return changed.value
    }

    //******************************************************************************************************************
    //Undo, Redo & Reset

    /**
     * Undoes the user input (valueAsText) if it is undoable (valueAsText was changed).
     * [setValueAsText] is used to set the reverted value (this also triggers the publishing).
     * The reverted value is also added to the [redoStack].
     */
    fun undo(){
        if(isUndoable()){
            cancelUndoJob(true)
            redoStack.push(undoStack.pop())
            setValueAsText(undoStack.peek(), addToUndoStack = false, clearRedo = false)
            setUndobale()
            setRedoable()
        }
    }

    /**
     * Undoes the last undo (if one exists and the last attribute modification by the user was executing undo or redo).
     */
    fun redo(){
        if(isRedoable()){
            val value = redoStack.pop()
            setValueAsText(value, clearRedo = false)
            cancelUndoJob(true)
            setRedoable()
        }
    }

    /**
     * This method resets the valueAsText to the last saved value.
     * This will force an update of the undo functionality.
     */
    fun reset(){
        cancelUndoJob(true)
        setValueAsText(getSavedValue()?.toString() ?: "")
        cancelUndoJob(true)
    }

    //****************************
    //Internal functions:

    /**
     * Updates the property [isUndoable]. If there are existing any changes that can be reverted the value will be set to true.
     * The result ist published to update the smartphone app.
     */
    private fun setUndobale(){
        isUndoable.value = undoStack.size > 1
        model.publishValidation(this)
    }

    /**
     * Updates the property [isRedoable]. isRedoable will be true if there is a redo possibility.
     * The result ist published to update the smartphone app.
     */
    private fun setRedoable(){
        isRedoable.value = !redoStack.isEmpty()
        model.publishValidation(this)
    }


    /**
     * Launches the undo functionality that will store the [valueAsText] in the [undoStack] if the job is finished.
     * ValueAsText is added after a [typingDelay] to be able to undo the valueAsText block-by-block.
     *
     * @param valueAsText: String that will be added to the [undoStack]
     */
    private fun addStringToUndoStack(valueAsText: String){
        cancelUndoJob()
        job = undoScope.launch{
            try {
                delay(typingDelay)
                undoStack.push(valueAsText)
                setUndobale()
            }catch(e: Exception){
                if(e.message == "reset"){
                    undoStack.push(valueAsText)
                    setUndobale()
                }
            }
        }
    }

    /**
     * Cancels the [job] which updates the [undoStack]. If [waitForCancel] flag is true the function will wait for the job
     * to be canceled before returning.
     *
     * @param waitForCancel: Boolean flag to decide if the cancel will be sync or async. True will force to write the last
     * job to the undo list.
     */
    private fun cancelUndoJob(waitForCancel: Boolean = false){
        if(job.isActive){
            if(waitForCancel) {
                runBlocking {
                    job.cancel("reset")
                    job.join()
                }
            }else{
                job.cancel()
            }
        }
    }

    //****************************
    //Getter:

    fun isUndoable(): Boolean{
        return isUndoable.value
    }

    fun isRedoable(): Boolean{
        return isRedoable.value
    }


    //******************************************************************************************************************
    //On-Change listener

    /**
     * This method returns a [func]tion to add a new onChangeListener to the list of all onChangeListeners observing the
     * value of this attribute.
     * By returning the the [func]tion the [func]tion will initially be executed to inform the listeners about the
     * initial value.
     *
     * @param func that gets invoked on value change with the parameters:
     * - Other attribute which should listen for value changes of this attribute and execute changes if necessary.
     * - Value of this attribute to be observed by the other attribute.
     *
     * @return function with the parameters:
     * - The attribute of the passed function (which should listen for value changes and execute changes if necessary).
     */
    infix fun addOnChangeListener(func: (attr: Attribute<*, *, *>, value: T?) -> Unit): (Attribute<*, *, *>) -> Unit {
        return { attr ->
            onChangeListenersOfThis.add{ func(attr, it) }
            func(attr, value)
        }
    }

    /**
     * This method adds a new onChangeListener to the list of all onChangeListeners observing the value of this attribute.
     * The [func]tion will be added to the listeners and initially be executed to inform the listeners about the initial value.
     *
     * @param func that gets invoked on value change with the parameter:
     * - Value of this attribute to be observed
     */
    fun addOnChangeListener(func: (T?) -> Unit){
        onChangeListenersOfThis.add{ func(it)}
        func(value)
    }

    /**
     * This method adds the current attribute as a listener to the attributes that should be observed.
     */
    fun setThisAsListenerOnOtherAttributes(){
        observedAttributes.forEach{ f ->
            f(this)
        }
    }

    //******************************************************************************************************************
    //Validation

    /**
     * This method adds a new semantic validator to the attribute.
     *
     * @param validator: [SemanticValidator] that will be triggered on validation.
     */
    fun addValidator(validator : SemanticValidator<T, L>){
        val tempList = validators.toMutableList()
        tempList.add(validator)
        validators = tempList
        validator.addAttribute(this)
    }

    /**
     * Revalidates the current user input (valueAsText).
     */
    fun revalidate(){
        checkAndSetValue(this.valueAsText.value)
    }

    //******************************************
    //check and set value

    /**
     * [checkAndSetValue] checks if the user input [newval] has a fitting Convertible and if it is valid according to
     * all set Validators. If it is valid, the value will be set to the into-type-converted newVal.
     * This method updates [rightTrackValid], [rightTrackValue], [valid] and [value].
     *
     * @param newVal : String to be checked and set
     * @param convertBecauseUnfocused : Boolean, if [newval] should be checked for Convertibles (if the convertible
     * parameter convertImmediately = true, the convertibles will be checked anyways)
     */
    protected fun checkAndSetValue(newVal: String?, convertBecauseUnfocused : Boolean = false){
        if(newVal == null || newVal.equals("") || newVal.equals("[]")){
            checkAndSetNullValue()
        }
        else {
            checkAndSetNonNullValue(newVal, convertBecauseUnfocused)
        }
    }

    /**
     * This method checks a non null value (newVal). This function lets the [newVal] be converted and validated.
     * [value] is set to the into-type-converted newVal, if this is valid.
     * This method updates [rightTrackValid], [rightTrackValue], [valid] and [value].
     *
     * @param newVal: String to be checked and set
     * @param convertBecauseUnfocused: flag that indicates if the check and set is called because the attribute is not
     * the current focused element anymore.
     */
    private fun checkAndSetNonNullValue(newVal: String, convertBecauseUnfocused : Boolean) {
        val convertedValueAsText : String
        var isConverted = false
        checkAllConvertibles(newVal)
        if(convertible.value){
            convertedValueAsText = getConvertedValueAsText()[0]
            isConverted = true
        }else{
            convertedValueAsText = newVal
        }
        checkSyntaxValidator(convertedValueAsText, isConverted)
        if (isValid()) {
            val typeValue = convertStringToType(convertedValueAsText)
            checkAllSemanticValidators(typeValue, convertedValueAsText)
            if (isValid()) {
                setValue(typeValue)
                if(!getConvertUserView().isNullOrEmpty() && getConvertUserView()[0] && (getConvertImmediately()[0] || convertBecauseUnfocused)){
                    setValueAsText(getValue().toString())
                }
            }
            if (isRightTrackValid()) {
                setRightTrackValue(typeValue)
            }
        }
    }

    /**
     * This method checks if the valueAsText is required.
     * If not the value will be set to null.
     */
    private fun checkAndSetNullValue() {
        var nullValue: T? = null
        var nullValueAsText = ""
        if (this is SelectionAttribute) {
            nullValue = emptySet<String>() as T
            nullValueAsText = "[]"
        }
        checkRequiredValidator(nullValue, nullValueAsText)
        if (isValid()) {
            setValue(nullValue)
        }
        if (isRightTrackValid()) {
            setRightTrackValue(nullValue)
        }
    }

    //****************************
    //Internal functions:

    /**
     * If the new value is different to the current value, the new value is set and the onChangeListeners are informed
     * about the value change. If auto save is activated the attribute is also saved.
     *
     * @param value: T?
     */
    private fun setValue(value: T?) {
        if(value != this.value){
            this.value = value
            onChangeListenersOfThis.forEach{
                it(value)
            }
        }
        if(model.isAutoSave()){
            save()
        }
    }

    private fun setRightTrackValue(newVal : T?){
        this.rightTrackValue.value = newVal
    }

    /**
     * This method sets the valid value and informs the [model] about that by calling [IModel.setValid].
     *
     * @param isValid : Boolean
     */
    private fun setValid(isValid : Boolean){
        this.valid.value = isValid
        this.model.setValid()
    }

    /**
     * This method sets the [Attribute.listOfValidationResults].
     * It also checks if all results are valid and on right track and sets the corresponding properties.
     * The validation will also be published.
     *
     * @param listOfValidationResults: List<ValidationResult<L>>
     */
    private fun setListOfValidationResults(listOfValidationResults: List<ValidationResult<L>>){
        this.listOfValidationResults.value = listOfValidationResults
        setValid(this.getListOfValidationResults().all { it.result })
        rightTrackValid.value = this.getListOfValidationResults().all{ it.rightTrackResult }
        model.publishValidation(this)
    }

    /**
     * This method checks if the [newValAsText] is valid regarding the syntax validator of this attribute.
     * The result is recorded in the validationResults.
     *
     * @param newValAsText : String
     * @param isConverted : Boolean
     * @throws IllegalArgumentException if the value was converted, but the converted value is not valid
     */
    private fun checkSyntaxValidator(newValAsText : String, isConverted : Boolean){
        val syntaxValidationResult = syntaxValidator.validateUserInput(typeT, newValAsText)
        if(!syntaxValidationResult.result && isConverted){
            model.setException(IllegalArgumentException("Converted into wrong syntax."))
            throw model.getException()!!
        }else {
            setListOfValidationResults(listOf(syntaxValidationResult))
        }
    }

    /**
     * This method checks if newValAsText (string) and/or the type-converted newValAsString (value)
     * (which of the two parameters is checked varies depending on the validator) is valid regarding the required
     * validator of this attribute. The result is recorded in the validationResults.
     *
     * @param newVal : T? (newValAsText converted into T)
     * @param newValAsText : String?
     */
    private fun checkRequiredValidator(newVal : T?, newValAsText : String?){
        setListOfValidationResults(listOf(reqValidator.validateUserInput(newVal, newValAsText)))
    }

    /**
     * This method checks if [newValAsText] (string) and/or the type-converted newValAsString ([value])
     * (which of the two parameters is checked varies depending on the validator) is valid regarding all semantic validators
     * of this attribute. The result is recorded in the validationResults.
     *
     * @param newVal : T (newValAsText converted into T)
     * @param newValAsText : String
     */
    private fun checkAllSemanticValidators(newVal: T, newValAsText : String) {
        setListOfValidationResults(validators.map { it.validateUserInput(newVal, newValAsText) })
    }

    //****************************
    //Getter:

    fun getValue() : T?{
        return value
    }

    fun getRightTrackValue() : T? {
        return rightTrackValue.value
    }

    fun isValid() : Boolean{
        return valid.value
    }

    fun isRightTrackValid() : Boolean {
        return rightTrackValid.value
    }

    fun getListOfValidationResults() : List<ValidationResult<L>>{
        return listOfValidationResults.value
    }

    /**
     * This method returns the list of validationMessages (String) of all invalid validation results.
     *
     * @return List<String> with the invalid result messages.
     */
    fun getErrorMessages(): List<String>{
        return listOfValidationResults.value.filter{!it.result}.map{
            it.validationMessage?.getLanguageStringFromLabel(it.validationMessage, model.getCurrentLanguage())?: it.defaultMessage
        }
    }

    //******************************************************************************************************************
    //Convertibles

    /**
     * This method calls setAndCheckValue with the parameters convertBecauseUnfocussed = true and newVal = current valueAsText
     */
    fun checkAndSetConvertibleBecauseUnfocusedAttribute(){
        checkAndSetValue(newVal = getValueAsText(), convertBecauseUnfocused = true)
    }

    //****************************
    //Internal functions:

    /**
     * This method checks, if the value is convertible regarding all convertibles of this attribute.
     * The result is recorded in the convertibleResult.
     *
     * @param newValAsText : String
     */
    private fun checkAllConvertibles(newValAsText: String){
        setListOfConvertibleResults(convertibles.map { it.convertUserInput(newValAsText)})
    }

    /**
     * This method sets the listOfConvertibleResults.
     * If any result is convertible (true) [convertible] is set to true else to false.
     *
     * @param listOfconvertibleResults
     */
    private fun setListOfConvertibleResults(listOfConvertibleResults: List<ConvertibleResult>){
        this.listOfConvertibleResults.value = listOfConvertibleResults
        this.convertible.value = (this.listOfConvertibleResults.value.any { it.isConvertible })
    }

    //****************************
    //Getter:

    /**
     * This method returns the convertedValueAsText of all convertible convertible-results
     *
     * @return List<String>
     */
    fun getConvertedValueAsText(): List<String>{
        return listOfConvertibleResults.value.filter{it.isConvertible}.map{it.convertedValueAsText}
    }

    /**
     * This method returns the convertUserView values (true/false) for all convertible results
     *
     * @return List<Boolean>
     */
    fun getConvertUserView() : List<Boolean>{
        return listOfConvertibleResults.value.filter{it.isConvertible}.map{it.convertUserView}
    }

    /**
     * This method returns the convertImmediately values (true/false) for all convertible results
     *
     * @return List<Boolean>
     */
    fun getConvertImmediately() : List<Boolean>{
        return listOfConvertibleResults.value.filter{it.isConvertible}.map {it.convertImmediately}
    }


    //******************************************************************************************************************
    //Remaining attribute properties

    /**
     *  This method sets the required property.
     *  Also the [RequiredValidator] will be overridden with the new isRequired value
     *
     *  @param isRequired: Boolean that should be set for the required property.
     */
    fun setRequired(isRequired: Boolean){
        reqValidator.overrideRequiredValidator(isRequired)
        this.required.value = reqValidator.isRequired()
    }

    fun setReadOnly(isReadOnly : Boolean){
        this.readOnly.value = isReadOnly
    }

    /**
     * This method changes the text of the labelAsText property to the text in the current [language] of the label [L].
     *
     * @param language : String
     */
    fun setCurrentLanguage(language : String){
        labelAsText.value = label.getLanguageStringFromLabel(this.label, language)
    }


    //****************************
    //Getter:

    fun getId(): Int{
        return id
    }

    /**
     * This method is there to get overridden by the specific attributes.
     */
    open fun getPossibleSelections(): List<T>{
        return emptyList()
    }

    fun getLabel() : String{
        return labelAsText.value
    }

    fun getModel() : IModel<L> {
        return model
    }

    fun isRequired(): Boolean {
        return required.value
    }

    fun isReadOnly() : Boolean{
        return readOnly.value
    }

    /**
     * This method returns the formatted value if valueAsText is valid or else the [valueAsText] will be returned.
     *
     * @return String: formatted value
     */
    fun getFormattedValue(): String{
        return if(isValid()){
            formatter?.format(value)?: valueAsText.value
        }else{
            valueAsText.value
        }
    }


    //******************************************************************************************************************
    //Helper function:

    /**
     * This method converts a string into the type of the attribute.
     *
     * @param newValAsText : String
     * @return typeT : T
     */
    open fun convertStringToType(newValAsText: String) : T {
        return Utilities<T>().toDataType(newValAsText, typeT)
    }

    //******************************************************************************************************************
    //Companion object:

    /**
     * Companion object for id
     */
    companion object{
        private var id : Int = 0

        /**
         * Increments id after return
         * @return Next unique Id
         */
        fun getNextId(): Int{
            return id++
        }

        /**
         * Resets the id to 0
         */
        fun resetId(){
            id = 0
        }
    }

}