package bigLazyTable.paging

import org.jetbrains.exposed.sql.Column


//class TestFilter<T> {
//    var dbField: Column<T>? = null
//
//    var args: List<T> = TODO()
//    var operation: NumberFilterType
//
//    fun updateValues(vararg valueAsString: String) {
//        //convertieren nach T
//    }
//}

// TODO: Operation?
enum class NumberFilterType {
    EQUALS,
    NOT_EQUALS,
    GREATER,
    GREATER_EQUALS,
    LESS,
    LESS_EQUALS,
    BETWEEN_BOTH_INCLUDED,
    BETWEEN_BOTH_NOT_INCLUDED,
    BETWEEN_FROM_INCLUDED,
    BETWEEN_TO_INCLUDED
}

sealed class Filter

// TODO: Can this be used to make it more Generic?
data class GenericFilter<T>(
    val filters: List<T>,
    val dbField: Column<T>,
    val operation: NumberFilterType
)

data class BooleanFilter(
    val filter: Boolean,
    val dbField: Column<Boolean>,
) : Filter()

data class StringFilter(
    val filter: String,
    val dbField: Column<String>,
    var caseSensitive: Boolean
) : Filter()

sealed class NumberFilter : Filter() {
    abstract val filterType: NumberFilterType
}

data class LongFilter(
    val filter: Long,
    val dbField: Column<Long>,
    override val filterType: NumberFilterType,
    val between: Between<Long>? = null
) : NumberFilter()

data class DoubleFilter(
    val filter: Double,
    val dbField: Column<Double>,
    override val filterType: NumberFilterType,
    val between: Between<Double>? = null
) : NumberFilter()

data class IntFilter(
    val filter: Int,
    val dbField: Column<Int>,
    override val filterType: NumberFilterType,
    val between: Between<Int>? = null
) : NumberFilter()

data class ShortFilter(
    val filter: Short,
    val dbField: Column<Short>,
    override val filterType: NumberFilterType,
    val between: Between<Short>? = null
) : NumberFilter()

data class FloatFilter(
    val filter: Float,
    val dbField: Column<Float>,
    override val filterType: NumberFilterType,
    val between: Between<Float>? = null
) : NumberFilter()