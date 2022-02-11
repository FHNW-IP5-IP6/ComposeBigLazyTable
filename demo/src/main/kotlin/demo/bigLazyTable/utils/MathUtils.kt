package demo.bigLazyTable.utils

import kotlin.math.ceil

// Question: Object or just as a function in a file?
//
// Answer:
// You can either put your methods in the top-level declaration or inside an object. It's all about whether you
// want to pollute namespaces or not. You can have a function named X in an object and you can still have a function
// name X in a different object file since you can refer to specific method with the help of their object name. But you
// won't be able to do this if you declare methods top-level, each method would need to have a unique signature
// name(not counting overloaded methods).
//
// Additionally, object can have supertypes, meaning you can inherit other classes or implement interfaces. Neither
// introduces more technical capability over another. If you define variables in an object or as top-level, both will
// be initialized lazily.
//
// In short, it depends if you want to pollute namespaces or not.
// https://stackoverflow.com/questions/57318508/when-should-i-use-object-type-vs-straight-kotlin-file
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
    ): Int {
        println("Inside roundDivisionToNextBiggerInt")
        return ceil(number.toDouble() / dividedBy).toInt()
    }

}