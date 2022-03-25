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

package composeForms.model.modelElements

import androidx.compose.runtime.Composable
import composeForms.model.ILabel
import composeForms.model.IModel

/**
 * [HeaderGroup] is a subclass of [Group]. Header is a semantic meaning for a group.
 * The HeaderGroup is used to group all important attributes together.
 * It has additional parameters to place individual UI elements.
 *
 * @param model: [IModel] that this group is added to
 * @param title: [L] for the title of the group
 * @param fields: Fields that are in this group
 * @param topSideHeader: Composable function that is on top
 * @param leftSideHeader: Composable function that is on the left
 * @param rightSideHeader: Composable function that is on the right
 * @param bottomSideHeader: Composable function that is on the bottom
 *
 * @author Louisa Reinger, Steve Vogel
 */
class HeaderGroup<L>(model : IModel<*>,
                     title : L,
                     vararg fields : Field,
                     val topSideHeader: @Composable () -> Unit = {},
                     val leftSideHeader: @Composable () -> Unit = {},
                     val rightSideHeader: @Composable () -> Unit = {},
                     val bottomSideHeader: @Composable () -> Unit = {}): Group<L>(model, title, *fields)

        where L : ILabel, L: Enum<*>