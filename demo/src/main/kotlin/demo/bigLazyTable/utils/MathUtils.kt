package demo.bigLazyTable.utils

import kotlin.math.ceil

// TODO: Object or just as a function in a file?
object MathUtils {

    /**
     * Just a Wrapper function around the Kotlin [ceil] function with two Int Parameters
     * @param number the number which should be divided
     * @param dividedBy the number which divides [number]
     * @return the next bigger int - Example: 10 / 3 = 4, where number=10 & dividedBy=3
     */
    fun roundDivisionToNextBiggerInt(
        number: Int,
        dividedBy: Int
    ): Int = ceil(number.toDouble() / dividedBy).toInt()

}

/**
 * Just a Wrapper function around the Kotlin [ceil] function with two Int Parameters
 * @param number the number which should be divided
 * @param dividedBy the number which divides [number]
 * @return the next bigger int - Example: 10 / 3 = 4, where number=10 & dividedBy=3
 */
fun roundDivisionToNextBiggerInt(
    number: Int,
    dividedBy: Int
): Int = ceil(number.toDouble() / dividedBy).toInt()