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

import androidx.compose.desktop.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import composeForms.model.IModel
import composeForms.ui.theme.ColorsUtil.Companion.get
import composeForms.ui.theme.FormColors

/**
 * External Window for showing thrown exceptions the composeForms.model : [IModel] has been informed of.
 *
 * @author Louisa Reinger, Steve Vogel
 */
@Composable
fun ExceptionWindow(model: IModel<*>){
    with(model) {
        val showingException = remember { mutableStateOf(false) }
        if (hasException()) {
            val showDetails = remember { mutableStateOf(false) }
            if (!showingException.value) {
                Window(size = IntSize(1000, 300), title = getTitle()) {
                    Column {
                        //Title
                        Box(
                            modifier = Modifier.fillMaxWidth().height(38.dp)
                                .border(0.dp, Color.Transparent, RoundedCornerShape(10.dp))
                                .background(get(FormColors.BACKGROUND_COLOR_HEADER)),
                            contentAlignment = Alignment.Center
                        ){
                            Text("Error occurred", color = get(FormColors.BODY_BACKGROUND))
                        }

                        //Content
                        val buttonText = if(showDetails.value) "Hide Details" else "Show Details"
                        Column(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Please contact your administrator. Following error occurred:", fontWeight = FontWeight.Bold)
                            Text(model.getException()?.message ?: "No message was set")
                            Button(
                                colors = ButtonDefaults.buttonColors(backgroundColor = get(FormColors.BACKGROUND_COLOR_HEADER)),
                                onClick = { showDetails.value = !showDetails.value } ) {
                                Text(buttonText, color = get(FormColors.FONT_ON_BACKGOUND))
                            }
                            DetailList(model, showDetails.value)
                        }
                    }
                }
            }
            showingException.value = true
        }
    }
}

//**********************************************************************************************************************
//Internal functions

/**
 * Show detail list for exceptions the composeForms.model has been informed of.
 * If [showDetails] is true details are shown else not.
 */
@Composable
private fun DetailList(model: IModel<*>, showDetails: Boolean){
    if(showDetails) {
        LazyColumn {
            item{
                Text(model.getException()?.stackTraceToString() ?: "")
            }
        }
    }
}