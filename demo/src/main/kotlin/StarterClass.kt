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

import androidx.compose.desktop.Window
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntSize
import demo.personForm.PersonModel
import ui.Form
import java.io.File
import javax.imageio.ImageIO

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
fun main() = Window(
    title = "Compose for Desktop",
    size = IntSize(1200, 800)
) {
    val model = remember { PersonModel() }
    Form().of(model)
}

