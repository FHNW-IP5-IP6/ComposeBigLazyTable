package demo.bigLazyTable.utils

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class PageUtilsTest {

    @Test
    fun `roundDivisionToNextBiggerInt of 1 divided by 1`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(1, PageUtils.getTotalPages(totalCount = 1, pageSize = 1))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of 10 divided by 3`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(4, PageUtils.getTotalPages(totalCount = 10, pageSize = 3))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of 1_000_000 divided by 40`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(25_000, PageUtils.getTotalPages(totalCount = 1_000_000, pageSize = 40))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of 516454 divided by 44`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(11_738, PageUtils.getTotalPages(totalCount = 516454, pageSize = 44))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of any number divided by 0 returns Int MAX_VALUE`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(Int.MAX_VALUE, PageUtils.getTotalPages(totalCount = 516454, pageSize = 0))
    }

    @Test
    fun `roundDivisionToNextBiggerInt of any number divided by -1 returns that number as negative value`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(-516454, PageUtils.getTotalPages(totalCount = 516454, pageSize = -1))
    }

}