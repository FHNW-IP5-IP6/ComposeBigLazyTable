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

sealed class Filter<T> {
    abstract val filter: T
    abstract val dbField: Column<T>
}

// TODO: Can this be used to make it more Generic?
data class GenericFilter<T>(
    val filters: List<T>,
    val dbField: Column<T>,
    val operation: NumberFilterType
)

data class BooleanFilter(
    override val filter: Boolean,
    override val dbField: Column<Boolean>,
) : Filter<Boolean>()

data class StringFilter(
    override val filter: String,
    override val dbField: Column<String>,
    var caseSensitive: Boolean
) : Filter<String>()

sealed class NumberFilter<T> : Filter<T>() {
    abstract val filterType: NumberFilterType
}

data class LongFilter(
    override val filter: Long,
    override val dbField: Column<Long>,
    override val filterType: NumberFilterType,
    val between: Between<Long>? = null
) : NumberFilter<Long>()

data class DoubleFilter(
    override val filter: Double,
    override val dbField: Column<Double>,
    override val filterType: NumberFilterType,
    val between: Between<Double>? = null
) : NumberFilter<Double>()

data class IntFilter(
    override val filter: Int,
    override val dbField: Column<Int>,
    override val filterType: NumberFilterType,
    val between: Between<Int>? = null
) : NumberFilter<Int>()

data class ShortFilter(
    override val filter: Short,
    override val dbField: Column<Short>,
    override val filterType: NumberFilterType,
    val between: Between<Short>? = null
) : NumberFilter<Short>()

data class FloatFilter(
    override val filter: Float,
    override val dbField: Column<Float>,
    override val filterType: NumberFilterType,
    val between: Between<Float>? = null
) : NumberFilter<Float>()