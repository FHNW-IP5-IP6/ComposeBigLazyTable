package demo.bigLazyTable.data.database

import bigLazyTable.paging.*
import demo.bigLazyTable.data.database.FilterUtil.chooseCorrectFilterTypeMethod
import demo.bigLazyTable.data.database.FilterUtil.filterEquals
//import demo.bigLazyTable.data.database.FilterUtil.selectWithFilter
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import java.util.*

fun Table.selectWithAllFilters(filters: List<Filter>): Query {
    println("Inside selectWithAllFilters")
    val start = System.currentTimeMillis()
    if (filters.isEmpty()) return Query(this, null)

    // TODO: Could this be removed due to the below for loop?
    if (filters.size == 1) {
        val filter = filters.first()
        return selectWithFilter(filter)
    }

    // TODO: Better way than this ugly code?
    var sql = when (val filter = filters.first()) {
        is BooleanFilter -> filterEquals(filter)
        is DoubleFilter  -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is FloatFilter   -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is IntFilter     -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is LongFilter    -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is ShortFilter   -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is StringFilter  -> filterEquals(filter)
    }
    for (i in 1 until filters.size) {
        val sql2: Op<Boolean> = when (val filter = filters[i]) {
            is BooleanFilter -> filterEquals(filter)
            is DoubleFilter  -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
            is FloatFilter   -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
            is IntFilter     -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
            is LongFilter    -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
            is ShortFilter   -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
            is StringFilter  -> filterEquals(filter)
        }
        sql = sql and sql2
    }
    val end = System.currentTimeMillis()
    println("Just before return: selectWithAllFilters needed ${end - start} ms")
    return this.select { sql }
}

fun Table.selectWithFilter(filter: Filter): Query {
    val sql: Op<Boolean> = when (filter) {
        is BooleanFilter -> filterEquals(filter)
        is DoubleFilter  -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is FloatFilter   -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is IntFilter     -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is LongFilter    -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is ShortFilter   -> chooseCorrectFilterTypeMethod(filter = filter, filterType = filter.filterType)
        is StringFilter  -> filterEquals(filter)
    }
    return select { sql }
}

object FilterUtil {

    fun chooseCorrectFilterTypeMethod(filter: Filter, filterType: NumberFilterType): Op<Boolean> {
        return when (filterType) {
            NumberFilterType.EQUALS  -> filterEquals(filter)
            NumberFilterType.LESS    -> filterLess(filter)
            NumberFilterType.GREATER -> filterGreater(filter)
            NumberFilterType.NOT_EQUALS     -> filterNotEquals(filter)
            NumberFilterType.LESS_EQUALS    -> filterLessEquals(filter)
            NumberFilterType.GREATER_EQUALS -> filterGreaterEquals(filter)
            NumberFilterType.BETWEEN_TO_INCLUDED        -> filterBetweenToIncluded(filter)
            NumberFilterType.BETWEEN_FROM_INCLUDED      -> filterBetweenFromIncluded(filter)
            NumberFilterType.BETWEEN_BOTH_INCLUDED      -> filterBetweenBothIncluded(filter)
            NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED  -> filterBetweenBothNotIncluded(filter)
        }
    }

    fun filterEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter    -> filter.dbField eq filter.filter
        is IntFilter     -> filter.dbField eq filter.filter
        is DoubleFilter  -> filter.dbField eq filter.filter
        is FloatFilter   -> filter.dbField eq filter.filter
        is ShortFilter   -> filter.dbField eq filter.filter
        is BooleanFilter -> filter.dbField as Column<String> like filter.filter.toString()
        is StringFilter  -> caseSensitiveLike(filter.caseSensitive, filter.dbField, filter.filter)
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
        is LongFilter    -> filter.dbField neq filter.filter
        is IntFilter     -> filter.dbField neq filter.filter
        is DoubleFilter  -> filter.dbField neq filter.filter
        is FloatFilter   -> filter.dbField neq filter.filter
        is ShortFilter   -> filter.dbField neq filter.filter
        is StringFilter  -> caseSensitiveLike(filter.caseSensitive, filter.dbField, filter.filter)
        else -> throw IllegalArgumentException("Only number & string filters can be called with this function, but received $filter")
    }

    private fun filterGreater(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField greater filter.filter
        is IntFilter    -> filter.dbField greater filter.filter
        is DoubleFilter -> filter.dbField greater filter.filter
        is FloatFilter  -> filter.dbField greater filter.filter
        is ShortFilter  -> filter.dbField greater filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterGreaterEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField greaterEq filter.filter
        is IntFilter    -> filter.dbField greaterEq filter.filter
        is DoubleFilter -> filter.dbField greaterEq filter.filter
        is FloatFilter  -> filter.dbField greaterEq filter.filter
        is ShortFilter  -> filter.dbField greaterEq filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterLess(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField less filter.filter
        is IntFilter    -> filter.dbField less filter.filter
        is DoubleFilter -> filter.dbField less filter.filter
        is FloatFilter  -> filter.dbField less filter.filter
        is ShortFilter  -> filter.dbField less filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterLessEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField lessEq filter.filter
        is IntFilter    -> filter.dbField lessEq filter.filter
        is DoubleFilter -> filter.dbField lessEq filter.filter
        is FloatFilter  -> filter.dbField lessEq filter.filter
        is ShortFilter  -> filter.dbField lessEq filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    // TODO: is there something like between?
    private fun filterBetweenBothIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is IntFilter    -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is DoubleFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is FloatFilter  -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is ShortFilter  -> filterGreaterEquals(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenBothNotIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is IntFilter    -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is DoubleFilter -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is FloatFilter  -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is ShortFilter  -> filterGreater(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenFromIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is IntFilter    -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is DoubleFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is FloatFilter  -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        is ShortFilter  -> filterGreaterEquals(filter.between!!.fromFilter) and filterLess(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    private fun filterBetweenToIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is IntFilter    -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is DoubleFilter -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is FloatFilter  -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        is ShortFilter  -> filterGreater(filter.between!!.fromFilter) and filterLessEquals(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

}