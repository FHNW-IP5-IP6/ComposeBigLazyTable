package demo.bigLazyTable.data.database

import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.utils.PageUtils
import org.jetbrains.exposed.sql.Column

class FakePagingService(val numberOfPlaylists: Int, val pageSize: Int) : IPagingService<Playlist> {

    private val allData = mutableMapOf<Int, List<Playlist>>()
    private val numberOfPages = PageUtils.getTotalPages(
        totalCount = numberOfPlaylists,
        pageSize = pageSize
    )

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

    // TODO: Fix for new getPage Method signature
    override fun getPage(
        startIndex: Int,
        pageSize: Int,
//        filter: Filter,
        filter: String,
        dbField: Column<*>?,
        caseSensitive: Boolean,
        sorted: String
    ): List<Playlist> {
        val pageNrOfStartIndex = startIndex / pageSize
        return allData[pageNrOfStartIndex]?.filter { it.name == filter } ?: emptyList()
    }

    override fun getFilteredCount(filter: String, caseSensitive: Boolean): Int {
        var count = 0
        allData.values.forEach { playlists ->
            count += playlists.filter { playlist -> playlist.name == filter }.size
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
}