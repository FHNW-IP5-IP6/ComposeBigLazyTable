package demo.bigLazyTable.utils

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MathUtilsTest {

    @Test
    fun `roundDivisionToNextBiggerInt of 1 divided by 1`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(1, MathUtils.roundDivisionToNextBiggerInt(number = 1, dividedBy = 1))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of 10 divided by 3`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(4, MathUtils.roundDivisionToNextBiggerInt(number = 10, dividedBy = 3))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of 1_000_000 divided by 40`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(25_000, MathUtils.roundDivisionToNextBiggerInt(number = 1_000_000, dividedBy = 40))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of 516454 divided by 44`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(11_738, MathUtils.roundDivisionToNextBiggerInt(number = 516454, dividedBy = 44))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of any number divided by 0 returns Int MAX_VALUE`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(Int.MAX_VALUE, MathUtils.roundDivisionToNextBiggerInt(number = 516454, dividedBy = 0))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of any number divided by -1 returns that number as negative value`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(-516454, MathUtils.roundDivisionToNextBiggerInt(number = 516454, dividedBy = -1))
    }

}