package demo.bigLazyTable.data.database

import bigLazyTable.paging.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greaterEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.SqlExpressionBuilder.lessEq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import java.util.*

object FilterUtil {

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
        var sql: Op<Boolean> = FilterUtil.filterEquals(filter = filters.first())
        for (i in 1 until filters.size) {
            val sql2: Op<Boolean> = FilterUtil.filterEquals(filter = filters[i])
            sql = sql and sql2
        }
        val end = System.currentTimeMillis()
        println("Just before return: selectWithAllFilters needed ${end - start} ms")
        return this.select { sql }
    }

    private fun Table.selectWithFilter(filter: Filter): Query {
        val sql: Op<Boolean> = filterEquals(filter = filter)
        return select { sql }
    }

    fun filterEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter    -> filter.dbField eq filter.filter
        is IntFilter     -> filter.dbField eq filter.filter
        is DoubleFilter  -> filter.dbField eq filter.filter
        is FloatFilter   -> filter.dbField eq filter.filter
        is BooleanFilter -> filter.dbField eq filter.filter // TODO: check -> in db == true/false doesnt work yet
        is StringFilter  -> caseSensitiveLike(filter.caseSensitive, filter.dbField, filter.filter)
    }

    /**
     * Is implemented with the thinking that the database will use case sensitive like by default or with a pragma
     */
    fun caseSensitiveLike(caseSensitive: Boolean, dbField: Column<String>, filter: String): Op<Boolean> =
        if (caseSensitive) dbField like filter
        else dbField.lowerCase() like filter.lowercase(Locale.getDefault())

    fun filterNotEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter    -> filter.dbField neq filter.filter
        is IntFilter     -> filter.dbField neq filter.filter
        is DoubleFilter  -> filter.dbField neq filter.filter
        is FloatFilter   -> filter.dbField neq filter.filter
        is BooleanFilter -> filter.dbField neq filter.filter // TODO: check -> in db == true/false doesnt work yet
        is StringFilter  -> caseSensitiveLike(filter.caseSensitive, filter.dbField, filter.filter)
    }

    fun filterGreater(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField greater filter.filter
        is IntFilter    -> filter.dbField greater filter.filter
        is DoubleFilter -> filter.dbField greater filter.filter
        is FloatFilter  -> filter.dbField greater filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    fun filterGreaterEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField greaterEq filter.filter
        is IntFilter    -> filter.dbField greaterEq filter.filter
        is DoubleFilter -> filter.dbField greaterEq filter.filter
        is FloatFilter  -> filter.dbField greaterEq filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    fun filterSmaller(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField less filter.filter
        is IntFilter    -> filter.dbField less filter.filter
        is DoubleFilter -> filter.dbField less filter.filter
        is FloatFilter  -> filter.dbField less filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    fun filterSmallerEquals(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filter.dbField lessEq filter.filter
        is IntFilter    -> filter.dbField lessEq filter.filter
        is DoubleFilter -> filter.dbField lessEq filter.filter
        is FloatFilter  -> filter.dbField lessEq filter.filter
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    // TODO: is there something like between?
    fun filterBetweenBothIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        is IntFilter    -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        is DoubleFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        is FloatFilter  -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    fun filterBetweenBothNotIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreater(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        is IntFilter    -> filterGreater(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        is DoubleFilter -> filterGreater(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        is FloatFilter  -> filterGreater(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    fun filterBetweenFromIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        is IntFilter    -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        is DoubleFilter -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        is FloatFilter  -> filterGreaterEquals(filter.between!!.fromFilter) and filterSmaller(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

    fun filterBetweenToIncluded(filter: Filter): Op<Boolean> = when (filter) {
        is LongFilter   -> filterGreater(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        is IntFilter    -> filterGreater(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        is DoubleFilter -> filterGreater(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        is FloatFilter  -> filterGreater(filter.between!!.fromFilter) and filterSmallerEquals(filter.between!!.toFilter)
        else -> throw IllegalArgumentException("Only number filters can be called with this function, but received $filter")
    }

}