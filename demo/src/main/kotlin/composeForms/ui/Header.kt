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

package composeForms.ui

import androidx.compose.desktop.AppManager
import androidx.compose.desktop.Window
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import composeForms.model.IModel
import composeForms.server.QRCodeService
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.DropdownColors
import composeForms.ui.theme.FormColors
import demo.bigLazyTable.model.AppState

/**
 * Header provides the element for interacting with the composeForms.model. Providing buttons to interact for example the save button.
 *
 * @param model: the composeForms.model for the form
 * @param changeShowError: function that changes the show error flag
 *
 * @author Louisa Reinger, Steve Vogel
 */
@Composable
fun Header(model : IModel<*>, changeShowError: (Boolean) -> Unit){
    val ctrlString = if(System.getProperty("os.name") == "Mac OS X")  "CMD" else "CTRL"

    with(model){
        TopAppBar(
            backgroundColor = get(FormColors.BACKGROUND_COLOR_HEADER),
            elevation = 100.dp
        ){

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically){

                Row(modifier = Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically){

                    //logo
                    Image(painter = painterResource("ic_logo_composeforms.svg"),
                        contentDescription = "Logo", modifier = Modifier.requiredSize(width = 166.dp, height = 42.dp))

                    //title
                    Text(getTitle(), color = get(FormColors.FONT_ON_BACKGOUND),
                        fontSize = 22.sp, modifier = Modifier.padding(start = 20.dp, top = 2.dp))
                }

                val isValid = if(isWizardMode()) isValidForWizardGroup() else allAttributesAreValid()
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically) {
                    //auto save
                    AutoSaveSwitch(model)
                    //language
                    LanguageDropDownButton(model)

                    //restart
                    HeaderButtonWithIcon(buttonIcon  = Icons.Filled.RestartAlt,
                                         enabled     = changesExist(),
                                         tooltipText  = getTooltipReset(),
                                         tooltipShortcutText = "($ctrlString+R)",
                                         onClick     = {reset()})

                    if(isWizardMode()){
                        WizardModeButtons(model, ctrlString, changeShowError)
                    }else{
                        //save
                        HeaderButtonWithIcon(buttonIcon  = Icons.Filled.Save,
                                             enabled     = changesExist() || !isValid,
                                             tooltipText = getTooltipSave(),
                                             tooltipShortcutText = "($ctrlString+S)",
                                             onClick     = {save()},
                                             errorIcon   = Icons.Filled.Error,
                                             onErrorClick = changeShowError,
                                             showError   = !isValid
                        )
                    }

                    SmartphoneButton(model)
                }
            }
        }
    }
}

//**********************************************************************************************************************
//Internal functions


//*************************
//Buttons

/**
 * [HeaderButtonWithIcon] is a composable function that creates a button with the given icon and additionally if provided
 * an error icon.
 *
 * @param buttonIcon: ImageVector with the base icon for the button
 * @param tooltipText: string that will be shown in the tooltip
 * @param enabled: flag if the button is enabled
 * @param onClick: function that is invoked on click if no error is present
 * @param errorIcon: ImageVector with the error icon
 * @param showError: flag that indicates if an error is present
 * @param onErrorClick: function that is invoke if the showError is true
 */
@Composable
private fun HeaderButtonWithIcon(buttonIcon: ImageVector, tooltipText: String = "", tooltipShortcutText: String = "",
                                 enabled: Boolean, onClick : () -> Unit,
                                 errorIcon: ImageVector? = null, showError: Boolean = false, onErrorClick: (Boolean) -> Unit = {}){
    BoxWithTooltip(
        tooltip = {
            Surface(modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
                Column(modifier = Modifier.wrapContentWidth().background(get(FormColors.BACKGROUND_COLOR_LIGHT)),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = tooltipText,
                        modifier = Modifier.padding(4.dp),
                        color = get(FormColors.NORMALTEXT)
                    )
                    if(tooltipShortcutText != ""){
                        Text(
                            text = tooltipShortcutText,
                            modifier = Modifier.padding(4.dp),
                            color = get(FormColors.LABEL)
                        )
                    }
                }
            }
        }, delay = 600
    ) {
        IconButton(
            modifier = Modifier,
            enabled = enabled,
            onClick = { if(!showError)onClick() else onErrorClick(true)}) {
            Icon(
                imageVector = buttonIcon,
                contentDescription = tooltipText,
                tint = if (enabled) get(FormColors.FONT_ON_BACKGOUND) else get(FormColors.DISABLED_ON_BACKGROUND)
            )
            if (showError && errorIcon != null) {
                Row(modifier = Modifier.offset(10.dp, (-8).dp)){
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Invalid Values",
                        tint = get(FormColors.ERRORCONTRAST)
                    )
                }
                //Icon
                Row(modifier = Modifier.offset(10.dp, (-8).dp)){
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = errorIcon,
                        contentDescription = "Invalid Values",
                        tint = get(FormColors.ERROR)
                    )
                }
            }
        }
    }
}

/**
 * Creating a dropdown with the provided languages.
 *
 * @param model: composeForms.model used for the form
 */
@Composable
private fun LanguageDropDownButton(model: IModel<*>){
    with(model){
        Column {
            val langDropDownIsOpen = remember { mutableStateOf(false) }

            val selectedIndex = remember { mutableStateOf(0) }

            OutlinedButton(
                modifier = Modifier.width(110.dp),
                onClick = { langDropDownIsOpen.value = !langDropDownIsOpen.value },
                shape = RoundedCornerShape(12),
                colors = ButtonDefaults.buttonColors(backgroundColor = get(DropdownColors.BUTTON_BACKGROUND)),
                border = BorderStroke(1.dp, get(FormColors.FONT_ON_BACKGOUND)),
            ) {
                Text(getCurrentLanguage(), color = get(FormColors.FONT_ON_BACKGOUND))
            }
            CustomDropdownMenu(
                expanded = langDropDownIsOpen.value,
                onDismissRequest = { langDropDownIsOpen.value = false },
                modifier = Modifier.wrapContentSize(),
                content = {
                    getPossibleLanguages().forEachIndexed { index, string ->
                        DropdownElement(model, string, index, selectedIndex)
                    }
                }
            )
        }
    }
}

/**
 * Creating HeaderButtonWithIcon for QR Code
 */
@Composable
private fun SmartphoneButton(model: IModel<*>){
    if(model.isSmartphoneOption()){
        HeaderButtonWithIcon(buttonIcon = Icons.Filled.QrCode,
            tooltipText = model.getTooltipConnectSmartphone(),
            enabled = true,
            onClick = { openQrCodeWindow(model, 500)})
    }
}

/**
 * Creating Buttons for Wizard Mode
 *
 * @param model: [IModel] that knows about the state of the mode
 * @param ctrlString: String for shortcut. holding information about the control button on the system
 * @param changeShowError: Function for changig the error show flag
 */
@Composable
private fun WizardModeButtons(model: IModel<*>, ctrlString: String, changeShowError: (Boolean) -> Unit){
    with(model) {
        //back
        HeaderButtonWithIcon(buttonIcon = Icons.Filled.ArrowBack,
            enabled = getCurrentGroupIndex() != 0,
            tooltipText = getTooltipPrevious(),
            tooltipShortcutText = "($ctrlString+B)",
            onClick = { previousWizardGroup() })
        //next / exit
        HeaderButtonWithIcon(
            buttonIcon = if (isLastWizardGroup()) Icons.Filled.ExitToApp else Icons.Filled.ArrowForward,
            enabled = true,
            tooltipText = if (isLastWizardGroup()) getTooltipSaveAndExit() else getTooltipSaveAndNext(),
            tooltipShortcutText = "($ctrlString+${if (isLastWizardGroup()) "E" else "N"})",
            onClick = {
                changeShowError(false)
                val isLastWizardGroup = isLastWizardGroup()
                save()
                if (isLastWizardGroup) {
                    AppManager.focusedWindow?.close()
                }
            },
            errorIcon = Icons.Filled.Error,
            onErrorClick = changeShowError,
            showError = !isValidForWizardGroup()
        )
    }
}


//*************************
//Switch

/**
 * [AutoSaveSwitch] is a Composable-Function that creates a switch to enable/disable autosave
 *
 * @param model: composeForms.model that contains the autosave information
 */
@Composable
private fun AutoSaveSwitch(model: IModel<*>){
    with(model){
        val switchColors =  SwitchDefaults.colors( checkedThumbColor = get(FormColors.FONT_ON_BACKGOUND), uncheckedThumbColor = get(FormColors.FONT_ON_BACKGOUND))

        val offFontWeight = if (!isAutoSave()) FontWeight.Bold else FontWeight.Normal
        val offColor = if (!isAutoSave()) get(FormColors.FONT_ON_BACKGOUND) else get(FormColors.DISABLED_ON_BACKGROUND)
        val onFontWeight = if (isAutoSave()) FontWeight.Bold else FontWeight.Normal
        val onColor = if (isAutoSave()) get(FormColors.FONT_ON_BACKGOUND) else get(FormColors.DISABLED_ON_BACKGROUND)

        BoxWithTooltip(
            tooltip = {
                Surface(modifier = Modifier.shadow(4.dp), shape = RoundedCornerShape(4.dp)) {
                    Text(text = getTooltipAutoSave(), modifier = Modifier.background(get(FormColors.BACKGROUND_COLOR_LIGHT)).padding(4.dp)) }
            }, delay = 600
        ) {
            Row(
                modifier = Modifier.height(48.dp).padding(6.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Auto Save",
                    color = get(FormColors.FONT_ON_BACKGOUND),
                    modifier = Modifier.padding(6.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(76.dp)
                ) {
                    Text(
                        text = "off",
                        fontWeight = offFontWeight,
                        color = offColor
                    )
                    Switch(
                        checked = isAutoSave(),
                        onCheckedChange = { changeAutoSave() },
                        colors = switchColors
                    )
                    Text(
                        text = "on",
                        fontWeight = onFontWeight,
                        color = onColor
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.width(40.dp).padding(6.dp)
                ) {
                    AutoSaveStateIcon(model)
                }
            }
        }
    }
}

//*************************
//Drop Downs/Icons


/**
 * Create dropdown element with information on [model], with the current language as selection check and [selectedIndex]
 * marking the current position of selection
 */
@Composable
private fun DropdownElement(model: IModel<*>, language: String, index: Int, selectedIndex: MutableState<Int>){
    val elementIsSelected = model.isCurrentLanguage(language)
    val elementIsSelectedBackgroundColor =
        if (elementIsSelected) get(DropdownColors.BACKGROUND_ELEMENT_SEL) else get(DropdownColors.BACKGROUND_ELEMENT_NOT_SEL)
    val elementIsSelectedTextColor =
        if (elementIsSelected) get(DropdownColors.TEXT_ELEMENT_SEL) else get(DropdownColors.TEXT_ELEMENT_NOT_SEL)

    val borderStroke = if(index == selectedIndex.value){
        BorderStroke(width = 2.dp, color = get(FormColors.BACKGROUND_COLOR_GROUPS))
    }else{
        BorderStroke(0.dp, Color.Transparent)
    }

    DropdownMenuItem(
        modifier = Modifier.background(elementIsSelectedBackgroundColor)
            .width(110.dp)
            .border(border = borderStroke, shape = RoundedCornerShape(4.dp))
            .pointerMoveFilter(onEnter = { selectedIndex.value = index; true }),
        onClick = {
            AppState.defaultPlaylistModel.setCurrentLanguage(language)
            model.setCurrentLanguage(language)
        },
        content = {
            Text(
                text = language,
                modifier = Modifier.background(elementIsSelectedBackgroundColor),
                color = elementIsSelectedTextColor
            )
        }
    )
}

/**
 * Creating Icon for auto save state.
 * @param model: holding information for state
 */
@Composable
private fun AutoSaveStateIcon(model: IModel<*>){
    if (model.isAutoSave()) {
        if (model.allFocusedAttributesOfCurrentViewAreValid()) {
            Icon(Icons.Filled.Done, "Done-Icon", tint = get(FormColors.FONT_ON_BACKGOUND))
        } else {
            Icon(Icons.Filled.Error, "Error", tint = get(FormColors.FONT_ON_BACKGOUND))
        }
    }
}


//*************************
//Open new Window

/**
 * Open external window with QR-Code that contains the IP-Address
 *
 * @param model: composeForms.model for the form
 * @param size: size for the icon
 */
private fun openQrCodeWindow(model: IModel<*>, size : Int){
    with(model){
        Window(title = "QR Code", size = IntSize(size, size)) {
            val img = remember { mutableStateOf(ImageBitmap(size,size)) }
            val ip = getIPAdress()
            QRCodeService().getQRCode("https://stevevogel1.github.io/ComposeForms/$ip", size){ img.value = it}

            Row(modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically){
                Image(img.value, "QR Code")
            }
        }
    }
}


