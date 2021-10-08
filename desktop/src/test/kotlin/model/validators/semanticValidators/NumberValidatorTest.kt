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

package model.validators.semanticValidators

import model.Labels
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * @author Louisa Reinger
 * @author Steve Vogel
 */
internal class NumberValidatorTest{


    @Test
    fun testDefaultValues(){
        //given
        val intValidator = NumberValidator<Int, Labels>(0)
        val doubleValidator = NumberValidator<Double, Labels>(0.0)


        //then
        assertEquals(Int.MAX_VALUE, intValidator.getUpperBound())
        assertEquals(Double.MAX_VALUE, doubleValidator.getUpperBound())


        //given
        val intValidator1 = NumberValidator<Int, Labels>(upperBound = 0)
        val doubleValidator1 = NumberValidator<Double, Labels>(upperBound = 0.0)
        assertEquals(Int.MIN_VALUE, intValidator1.getLowerBound())
        assertEquals(-Double.MAX_VALUE, doubleValidator1.getLowerBound())
    }

    @Test
    fun testBounds(){
        //given
        val intValidator = NumberValidator<Int, Labels>(3, 10)

        //then
        assertFalse(intValidator.validateUserInput(2, "2").rightTrackResult)
        assertFalse(intValidator.validateUserInput(2, "2").result)
        assertTrue(intValidator.validateUserInput(3, "3").rightTrackResult)
        assertTrue(intValidator.validateUserInput(3, "3").result)

        assertTrue(intValidator.validateUserInput(9, "9").rightTrackResult)
        assertTrue(intValidator.validateUserInput(9, "9").result)
        assertTrue(intValidator.validateUserInput(10, "10").rightTrackResult)
        assertTrue(intValidator.validateUserInput(10, "10").result)
        assertFalse(intValidator.validateUserInput(11, "11").rightTrackResult)
        assertFalse(intValidator.validateUserInput(11, "11").result)
    }


    @Test
    fun testSteps(){

        val intValidator = NumberValidator<Int, Labels>(stepStart = 0, stepSize = 2, onlyStepValuesAreValid = true)

        assertTrue(intValidator.validateUserInput(-4, "-4").result)
        assertTrue(intValidator.validateUserInput(-2, "-2").result)
        assertTrue(intValidator.validateUserInput(0, "0").result)
        assertTrue(intValidator.validateUserInput(2, "2").result)
        assertTrue(intValidator.validateUserInput(4, "4").result)

        assertFalse(intValidator.validateUserInput(3, "3").result)
        assertFalse(intValidator.validateUserInput(-3, "-3").result)
    }

    @Test
    fun testRightTrack(){
        //given
        val posValidator = NumberValidator<Int, Labels>(10, 200)

        //then
        assertFalse(posValidator.validateUserInput(-10, "-10").rightTrackResult)
        assertFalse(posValidator.validateUserInput(0, "0").rightTrackResult)
        assertTrue(posValidator.validateUserInput(9, "9").rightTrackResult)
        assertTrue(posValidator.validateUserInput(10, "10").rightTrackResult)
        assertTrue(posValidator.validateUserInput(200, "200").rightTrackResult)
        assertFalse(posValidator.validateUserInput(201, "201").rightTrackResult)


        //given
        val negValidator = NumberValidator<Int, Labels>(-1000, -550)

        //then
        assertFalse(negValidator.validateUserInput(10, "10").rightTrackResult)
        assertFalse(negValidator.validateUserInput(0, "0").rightTrackResult)
        assertTrue(negValidator.validateUserInput(-9, "-9").rightTrackResult)
        assertTrue(negValidator.validateUserInput(-55, "55").rightTrackResult)
        assertTrue(negValidator.validateUserInput(-56, "-56").rightTrackResult)
        assertTrue(negValidator.validateUserInput(-765, "-765").rightTrackResult)
        assertTrue(negValidator.validateUserInput(-1000, "-1000").rightTrackResult)
        assertFalse(negValidator.validateUserInput(-1001, "-1001").rightTrackResult)
        assertFalse(negValidator.validateUserInput(-250, "-250").rightTrackResult)


        //given
        val posNegValidator = NumberValidator<Int, Labels>(-15, 200)

        //then
        assertFalse(posNegValidator.validateUserInput(-16, "-16").rightTrackResult)
        assertTrue(posNegValidator.validateUserInput(-15, "-15").rightTrackResult)
        assertTrue(posNegValidator.validateUserInput(0, "0").rightTrackResult)
        assertTrue(posNegValidator.validateUserInput(30, "30").rightTrackResult)
        assertTrue(posNegValidator.validateUserInput(200, "200").rightTrackResult)
        assertFalse(posNegValidator.validateUserInput(201, "201").rightTrackResult)

    }
}