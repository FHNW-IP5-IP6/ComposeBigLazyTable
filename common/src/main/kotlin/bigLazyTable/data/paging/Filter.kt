package bigLazyTable.data.paging

import org.jetbrains.exposed.sql.Column

/**
 * @author Marco Sprenger, Livio Näf
 */
enum class FilterOperation {
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

data class BooleanFilter(
    val filter: Boolean,
    val dbField: Column<Boolean>,
) : Filter()

sealed class MultipleOperationsFilter : Filter() {
    abstract val filterOperation: FilterOperation
}

data class StringFilter(
    val filter: String,
    val dbField: Column<String>,
    var caseSensitive: Boolean,
    override val filterOperation: FilterOperation = FilterOperation.EQUALS
) : MultipleOperationsFilter()

data class LongFilter(
    val filter: Long,
    val dbField: Column<Long>,
    override val filterOperation: FilterOperation,
    val between: Between<Long>? = null
) : MultipleOperationsFilter()

data class DoubleFilter(
    val filter: Double,
    val dbField: Column<Double>,
    override val filterOperation: FilterOperation,
    val between: Between<Double>? = null
) : MultipleOperationsFilter()

data class IntFilter(
    val filter: Int,
    val dbField: Column<Int>,
    override val filterOperation: FilterOperation,
    val between: Between<Int>? = null
) : MultipleOperationsFilter()

data class ShortFilter(
    val filter: Short,
    val dbField: Column<Short>,
    override val filterOperation: FilterOperation,
    val between: Between<Short>? = null
) : MultipleOperationsFilter()

data class FloatFilter(
    val filter: Float,
    val dbField: Column<Float>,
    override val filterOperation: FilterOperation,
    val between: Between<Float>? = null
) : MultipleOperationsFilter()

// TODO-Future: Can this be used to make it more Generic?
data class GenericFilter<T>(
    val filters: List<T>, // list of filters instead of additional Between object
    val dbField: Column<T>,
    val operation: FilterOperation
) {
    fun updateValues(vararg valueAsString: String) {
        // konvertieren nach T
    }
}