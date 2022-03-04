package bigLazyTable.paging

import org.jetbrains.exposed.sql.Column

sealed class Filter

data class LongFilter(
    val filter: Long,
    val dbField: Column<Long>,
    val caseSensitive: Boolean,
    val between: Between<Long>? = null
) : Filter()

data class DoubleFilter(
    val filter: Double,
    val dbField: Column<Double>,
    val caseSensitive: Boolean,
    val between: Between<Double>? = null
) : Filter()

data class IntFilter(
    val filter: Int,
    val dbField: Column<Int>,
    val caseSensitive: Boolean,
    val between: Between<Int>? = null
) : Filter()

data class FloatFilter(
    val filter: Float,
    val dbField: Column<Float>,
    val caseSensitive: Boolean,
    val between: Between<Float>? = null
) : Filter()

data class BooleanFilter(
    val filter: Boolean,
    val dbField: Column<Boolean>,
    val caseSensitive: Boolean
) : Filter()

data class StringFilter(
    val filter: String,
    val dbField: Column<String>,
    var caseSensitive: Boolean
) : Filter()