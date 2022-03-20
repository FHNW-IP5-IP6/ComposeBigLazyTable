package demo.bigLazyTable.data.database

import bigLazyTable.paging.Filter
import bigLazyTable.paging.IPagingService
import bigLazyTable.paging.Sort
import bigLazyTable.paging.StringFilter
import demo.bigLazyTable.model.Playlist
import kotlin.math.ceil
import kotlin.reflect.full.memberProperties

class FakePagingService(val numberOfPlaylists: Int, val pageSize: Int) : IPagingService<Playlist> {

    private val allData = mutableMapOf<Int, List<Playlist>>()
    private val numberOfPages = ceil(numberOfPlaylists.toDouble() / pageSize).toInt()

    init {
        for (pageNr in 0 until numberOfPages) {
            val playlistsOfPage = mutableListOf<Playlist>()
            for (pageItemNr in 1 until pageSize + 1) {
                val playlistId = (pageNr * pageSize) + pageItemNr.toLong()
                playlistsOfPage.add(Playlist(id = playlistId, name = "name $playlistId"))
            }
            allData[pageNr] = playlistsOfPage
        }
    }

    override fun getPage(
        startIndex: Int,
        pageSize: Int,
        filters: List<Filter>,
        sort: Sort?
    ): List<Playlist> {
        val adjustedFilters = mutableListOf<Filter>()
        filters.forEach {
            it as StringFilter
            val filterString = it.filter
            if (!it.caseSensitive) {
                filterString.lowercase()
            }
            adjustedFilters.add(StringFilter(
                filter = filterString,
                dbField =  it.dbField,
                caseSensitive = it.caseSensitive
            ))
        }

        val allDataFiltered = mutableListOf<Playlist>()
        allData.values.forEach { playlists ->
            var filteredData = playlists
            adjustedFilters.forEach { filter ->
                filter as StringFilter
                filteredData = filteredData.filter { it.getField<String>(filter.dbField?.name ?: "") == filter.filter }
            }
            allDataFiltered.addAll(filteredData)
        }
        allDataFiltered.sortByDescending { it.getField<String>(sort?.dbField?.name ?: "") }
        if (sort?.sortOrder?.name == "ASC") { allDataFiltered.reverse() }

        return allDataFiltered.subList(startIndex, startIndex+pageSize)
    }

    override fun getFilteredCount(filters: List<Filter>): Int {
        var count = 0

        val adjustedFilters = mutableListOf<Filter>()
        filters.forEach {
            it as StringFilter
            val filterString = it.filter
            if (!it.caseSensitive) {
                filterString.lowercase()
            }
            adjustedFilters.add(StringFilter(
                filter = filterString,
                dbField =  it.dbField,
                caseSensitive = it.caseSensitive
            ))
        }

        allData.values.forEach { playlists ->
            var filteredData = playlists
            adjustedFilters.forEach { filter ->
                filter as StringFilter
                filteredData = filteredData.filter { it.getField<String>(filter.dbField?.name ?: "") == filter.filter }
            }
            count += filteredData.size
        }

        return count
    }

    override fun getTotalCount() = numberOfPlaylists

    override fun get(id: Long): Playlist {
        lateinit var playlist: Playlist
        allData.values.forEach { playlists ->
            playlist = playlists.find { it.id == id }!!
        }
        return playlist
    }

    override fun indexOf(id: Long, filter: String): Int {
        TODO("Not yet implemented -> first implement the real method!")
    }

    // TODO: What does this function do?
    @Throws(IllegalAccessException::class, ClassCastException::class)
    inline fun <reified T> Any.getField(fieldName: String): T? {
        this::class.memberProperties.forEach { kCallable ->
            if (fieldName == kCallable.name) {
                return kCallable.getter.call(this) as T?
            }
        }
        return null
    }

}