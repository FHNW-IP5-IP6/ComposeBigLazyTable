package bigLazyTable.data.paging

import bigLazyTable.data.paging.FilterUtil.retrieveSql
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.notLike
import java.util.*

fun Table.selectWithAllFilters(filters: List<Filter>): Query {
    println("Inside selectWithAllFilters")
    val start = System.currentTimeMillis()
    if (filters.isEmpty()) return Query(this, null)

    var sql = retrieveSql(filter = filters.first())
    for (i in 1 until filters.size) {
        val sql2 = retrieveSql(filter = filters[i])
        sql = sql and sql2
    }
    val end = System.currentTimeMillis()
    println("Just before return: selectWithAllFilters needed ${end - start} ms")
    return this.select { sql }
}

object FilterUtil {

    fun retrieveSql(filter: Filter): Op<Boolean> = when (filter) {
        is BooleanFilter/*, is StringFilter*/ -> filterEquals(filter)
        is MultipleOperationsFilter -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterOperation)
    }

    private fun chooseCorrectFilterTypeMethod(filter: MultipleOperationsFilter, filterType: FilterOperation): Op<Boolean> {
        return when (filterType) {
            FilterOperation.EQUALS -> filterEquals(filter)
            FilterOperation.LESS    -> filterLess(filter)
            FilterOperation.GREATER -> filterGreater(filter)
            FilterOperation.NOT_EQUALS     -> filterNotEquals(filter)
            FilterOperation.LESS_EQUALS    -> filterLessEquals(filter)
            FilterOperation.GREATER_EQUALS -> filterGreaterEquals(filter)
            FilterOperation.BETWEEN_TO_INCLUDED        -> filterBetweenToIncluded(filter)
            FilterOperation.BETWEEN_FROM_INCLUDED      -> filterBetweenFromIncluded(filter)
            FilterOperation.BETWEEN_BOTH_INCLUDED      -> filterBetweenBothIncluded(filter)
            FilterOperation.BETWEEN_BOTH_NOT_INCLUDED  -> filterBetweenBothNotIncluded(filter)
        }
    }

    private fun filterEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filter.dbField eq filter.filter
        is IntFilter -> filter.dbField eq filter.filter
        is DoubleFilter -> filter.dbField eq filter.filter
        is FloatFilter -> filter.dbField eq filter.filter
        is ShortFilter -> filter.dbField eq filter.filter
        is BooleanFilter -> filter.dbField as Column<String> like filter.filter.toString()
        is StringFilter -> caseSensitiveLike(filter.caseSensitive, filter.dbField, filter.filter)
    }

    /**
     * Is implemented with the thinking that the database will
     * use case sensitive like by default or with a pragma
     *
     * [dbField] must be lowerCase() in the else case
     */
    private fun caseSensitiveLike(
        caseSensitive: Boolean,
        dbField: Column<String>,
        filter: String
    ): Op<Boolean> =
        if (caseSensitive) dbField like filter
        else dbField.lowerCase() like filter.lowercase(Locale.getDefault())

    private fun filterNotEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filter.dbField neq filter.filter
        is IntFilter -> filter.dbField neq filter.filter
        is DoubleFilter -> filter.dbField neq filter.filter
        is FloatFilter -> filter.dbField neq filter.filter
        is ShortFilter -> filter.dbField neq filter.filter
        is StringFilter -> caseSensitiveNotLike(filter.caseSensitive, filter.dbField, filter.filter)
        else -> throw IllegalArgumentException("Only number & string filters can be called with this function, but received $filter")
    }

    /**
     * For future uses a NOT LIKE function
     */
    private fun caseSensitiveNotLike(
        caseSensitive: Boolean,
        dbField: Column<String>,
        filter: String
    ): Op<Boolean> =
        if (caseSensitive) dbField notLike filter
        else dbField.lowerCase() notLike filter.lowercase(Locale.getDefault())

    private fun filterGreater(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filter.dbField greater filter.filter
        is IntFilter -> filter.dbField greater filter.filter
        is DoubleFilter -> filter.dbField greater filter.filter
        is FloatFilter -> filter.dbField greater filter.filter
        is ShortFilter -> filter.dbField greater filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterGreaterEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filter.dbField greaterEq filter.filter
        is IntFilter -> filter.dbField greaterEq filter.filter
        is DoubleFilter -> filter.dbField greaterEq filter.filter
        is FloatFilter -> filter.dbField greaterEq filter.filter
        is ShortFilter -> filter.dbField greaterEq filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterLess(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filter.dbField less filter.filter
        is IntFilter -> filter.dbField less filter.filter
        is DoubleFilter -> filter.dbField less filter.filter
        is FloatFilter -> filter.dbField less filter.filter
        is ShortFilter -> filter.dbField less filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterLessEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filter.dbField lessEq filter.filter
        is IntFilter -> filter.dbField lessEq filter.filter
        is DoubleFilter -> filter.dbField lessEq filter.filter
        is FloatFilter -> filter.dbField lessEq filter.filter
        is ShortFilter -> filter.dbField lessEq filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenBothIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is IntFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is DoubleFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is FloatFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is ShortFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenBothNotIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is IntFilter -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is DoubleFilter -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is FloatFilter -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is ShortFilter -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenFromIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is IntFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is DoubleFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is FloatFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is ShortFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenToIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is IntFilter -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is DoubleFilter -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is FloatFilter -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is ShortFilter -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

}