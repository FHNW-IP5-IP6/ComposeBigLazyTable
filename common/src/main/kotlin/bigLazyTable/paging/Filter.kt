package bigLazyTable.paging

import org.jetbrains.exposed.sql.Column

//abstract class Filter<T>(
//    open val filter: T,
//    open val dbField: Column<T>,
//    open var caseSensitive: Boolean,
//)

sealed class Filter

data class LongFilter(
    val filter: Long,
    val dbField: Column<Long>,
    val caseSensitive: Boolean
) : Filter()//<Long>(filter, dbField, caseSensitive)

data class DoubleFilter(
    val filter: Double,
    val dbField: Column<Double>,
    val caseSensitive: Boolean
) : Filter()//<Double>(filter, dbField, caseSensitive)

data class IntFilter(
    val filter: Int,
    val dbField: Column<Int>,
    val caseSensitive: Boolean
) : Filter()//<Int>(filter, dbField, caseSensitive)


data class FloatFilter(
    val filter: Float,
    val dbField: Column<Float>,
    val caseSensitive: Boolean
) : Filter()//<Float>(filter, dbField, caseSensitive)

data class BooleanFilter(
    val filter: Boolean,
    val dbField: Column<Boolean>,
    val caseSensitive: Boolean
) : Filter()//<Boolean>(filter, dbField, caseSensitive)

data class StringFilter(
    /*override*/ val filter: String,
    /*override*/ val dbField: Column<String>,
    /*override*/ var caseSensitive: Boolean
) : Filter()//<String>(filter, dbField, caseSensitive)