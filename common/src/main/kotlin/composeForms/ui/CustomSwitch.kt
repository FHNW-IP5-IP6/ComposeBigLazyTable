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

package ui.util


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collect
import composeForms.ui.theme.ColorsUtil
import composeForms.ui.theme.FormColors
import kotlin.math.roundToInt
/**
 * This file contains a custom switch, where the appearance can be changed.
 *
 * @author Louisa Reinger, Steve Vogel
 */

// SIZE VALUES to increase/decrease the overall size of the switch
private val standard = 20   //do not change this
private val size = 20       //effective size

private fun getSize(value: Int): Dp {
    return (value*size/standard).dp
}

//modifiable CustomSwitch parameters
private  var TRACKWIDTH                 = getSize(34)
private  var MAXTRACKWIDTH              = true
private  var THUMBDIAMETER              = getSize(20)
private  var CIRCLETHUMB                = true

//internal parameters
private val TrackStrokeWidth            = getSize(14)
private val  ThumbRippleRadius          = getSize(24)
private val  DefaultSwitchPadding       = getSize(2)
private var  SWITCHWIDTH                = TRACKWIDTH
private val  SwitchHeight               = THUMBDIAMETER
private var  THUMBPATHLENGTH            = TRACKWIDTH - THUMBDIAMETER
private var  THUMBWIDTH                 = SWITCHWIDTH/2
private val  AnimationSpec              = TweenSpec<Float>(durationMillis = 100)
private val  ThumbDefaultElevation      = 1.dp
private val  ThumbPressedElevation      = 6.dp


/**
 * The [Switch] from compose material implemented as a custom switch. The switch is a toggleable element that can have
 * two states: left (off) and right (on).
 *
 * Most of the code is copied and adapted to the use case for Compose Forms.
 * The default sizes and shapes were not changeable and did not adapt to different screen sizes.
 * This implementation can change the width and the height of the whole switch and the shape of the thumb.
 *
 * @author Louisa Reinger, Steve Vogel
 *
 * @param checked: indicator for the state - left or right side
 * @param modifier: Modifier
 * @param onCheckedChange: function that is invoked after a changed state
 * @param enabled: if the switch can change the state or if it is read only
 * @param booleanSwitch: if the switch is for a boolean- or a decision-attribute. (different colors)
 * @param trackWidth: the width of the switch. (will be overridden if [maxTrackWidth] is true)
 * @param maxTrackWidth: if true the maximum width will be filled
 * @param thumbHeight: the height of the toggle element
 * @param circleThumb: if the thumb is a circle (width = height) or an oval (width = track width / 2)
 */
@ExperimentalMaterialApi
@Composable
fun CustomSwitch(
    checked             : Boolean,
    onCheckedChange     : ((Boolean) -> Unit)?,
    modifier            : Modifier                  = Modifier,
    enabled             : Boolean                   = true,

    booleanSwitch       : Boolean,
    trackWidth          : Dp                        = 34.dp,
    maxTrackWidth       : Boolean                   = false,
    thumbHeight         : Dp                        = 20.dp,
    circleThumb         : Boolean                   = true
) {
    BoxWithConstraints {
        TRACKWIDTH      = trackWidth
        MAXTRACKWIDTH   = maxTrackWidth
        CIRCLETHUMB     = circleThumb
        THUMBDIAMETER   = thumbHeight

        SWITCHWIDTH     = if(MAXTRACKWIDTH) maxWidth else TRACKWIDTH
        THUMBWIDTH      = if(CIRCLETHUMB) THUMBDIAMETER else SWITCHWIDTH/2
        THUMBPATHLENGTH = SWITCHWIDTH - THUMBWIDTH

        val interactionSource   : MutableInteractionSource  = remember { MutableInteractionSource() }

        val minBound = 0f
        val maxBound = with(LocalDensity.current) { THUMBPATHLENGTH.toPx() }
        val swipeableState = rememberSwipeableStateFor(checked, onCheckedChange ?: {}, AnimationSpec)
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val toggleableModifier =
            if (onCheckedChange != null) {
                Modifier.toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    enabled = enabled,
                    role = Role.Switch,
                    interactionSource = interactionSource,
                    indication = null
                )
            } else {
                Modifier
            }

        val switchColors = if(booleanSwitch){
            SwitchDefaults.colors(
                /*Thumb*/
                checkedThumbColor = ColorsUtil.get(FormColors.SWITCH_THUMB),
                uncheckedThumbColor = ColorsUtil.get(FormColors.BODY_BACKGROUND),
                /*Track*/
                disabledUncheckedTrackColor =  ColorsUtil.get(FormColors.UNCHECKEDTRACKCOLOR))
        }else{
            SwitchDefaults.colors(
                /*Thumb*/
                checkedThumbColor = ColorsUtil.get(FormColors.SWITCH_THUMB),
                uncheckedThumbColor = ColorsUtil.get(FormColors.SWITCH_THUMB),
                disabledCheckedThumbColor = ColorsUtil.get(FormColors.BODY_BACKGROUND),
                disabledUncheckedThumbColor = ColorsUtil.get(FormColors.BODY_BACKGROUND),
                /*Track*/
                uncheckedTrackColor = ColorsUtil.get(FormColors.SWITCH_THUMB).copy(alpha = 0.54f),
                disabledCheckedTrackColor = ColorsUtil.get(FormColors.UNCHECKEDTRACKCOLOR),
                disabledUncheckedTrackColor = ColorsUtil.get(FormColors.UNCHECKEDTRACKCOLOR),
            )
        }



        Box(
            modifier
                .then(toggleableModifier)
                .swipeable(
                    state = swipeableState,
                    anchors = mapOf(minBound to false, maxBound to true),
                    thresholds = { _, _ -> FractionalThreshold(0.5f) },
                    orientation = Orientation.Horizontal,
                    enabled = enabled && onCheckedChange != null,
                    reverseDirection = isRtl,
                    interactionSource = interactionSource,
                    resistance = null
                )
                .wrapContentSize(Alignment.Center)
                .padding(DefaultSwitchPadding)
                .fillMaxWidth()
                .requiredSize(SWITCHWIDTH, SwitchHeight)
        ) {
            customSwitchImpl(
                checked = checked,
                enabled = enabled,
                colors = switchColors,
                thumbValue = swipeableState.offset,
                interactionSource = interactionSource,
                screenWidth = SWITCHWIDTH
            )
        }
    }
}

//**********************************************************************************************************************
//Internal functions

/**
 * Copied [SwipeableState]  implementation from compose material for the [CustomSwitch] because SwipeableState is internal.
 *
 * @author Louisa Reinger, Steve Vogel
 */
@Composable
@ExperimentalMaterialApi
private fun <T : Any> rememberSwipeableStateFor(
    value: T,
    onValueChange: (T) -> Unit,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
): SwipeableState<T> {
    val swipeableState = rememberSwipeableState(
        initialValue = value,
        animationSpec = animationSpec
    )
    val forceAnimationCheck = remember { mutableStateOf(false) }
    LaunchedEffect(value, forceAnimationCheck.value) {
        if (value != swipeableState.currentValue) {
            swipeableState.animateTo(value)
        }
    }
    DisposableEffect(swipeableState.currentValue) {
        if (value != swipeableState.currentValue) {
            onValueChange(swipeableState.currentValue)
            forceAnimationCheck.value = !forceAnimationCheck.value
        }
        onDispose { }
    }
    return swipeableState
}

/**
* Private function for the Switch. Copied from [Switch]
*/
@Composable
private fun BoxScope.customSwitchImpl(
    checked             : Boolean,
    enabled             : Boolean,
    colors              : SwitchColors,
    thumbValue          : State<Float>,
    interactionSource   : InteractionSource,
    screenWidth         : Dp
) {
    val interactions = remember { mutableStateListOf<Interaction>() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }

    val hasInteraction = interactions.isNotEmpty()
    val elevation = if (hasInteraction) {
        ThumbPressedElevation
    } else {
        ThumbDefaultElevation
    }
    val trackColor by colors.trackColor(enabled, checked)
    Canvas(Modifier.align(Alignment.Center).fillMaxSize()) {
        drawTrack(trackColor, if(MAXTRACKWIDTH) screenWidth.toPx() else TRACKWIDTH.toPx(), TrackStrokeWidth.toPx())
    }
    val thumbColor by colors.thumbColor(enabled, checked)
    Surface(
        shape = CircleShape,
        color = thumbColor,
        elevation = elevation,
        modifier = Modifier
            .align(Alignment.CenterStart)
            .offset { IntOffset(thumbValue.value.roundToInt(), 0) }
            .indication(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = false, radius = ThumbRippleRadius)
            )
            .requiredSize(if(CIRCLETHUMB) THUMBDIAMETER else SWITCHWIDTH/2, THUMBDIAMETER),
        content = {}
    )
}

/**
 * Private function for the Switch. Copied from [Switch]
 */
private fun DrawScope.drawTrack(trackColor: Color, trackWidth: Float, strokeWidth: Float) {
    val strokeRadius = strokeWidth / 2
    drawLine(
        trackColor,
        Offset(strokeRadius, center.y),
        Offset(trackWidth - strokeRadius, center.y),
        strokeWidth,
        StrokeCap.Round
    )
}