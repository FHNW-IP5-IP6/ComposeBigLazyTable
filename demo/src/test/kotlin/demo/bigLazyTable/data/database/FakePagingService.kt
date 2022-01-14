package demo.bigLazyTable.data.database

import bigLazyTable.paging.IPagingService
import demo.bigLazyTable.model.Playlist

object FakePagingService: IPagingService<Playlist> {

    private val fakePage = mutableListOf<Playlist>()

    init {
        for (i in 0 until 10) {
            fakePage.add(Playlist(id = i.toLong(), name = "name $i"))
        }
    }

    override suspend fun getPage(startIndex: Int, pageSize: Int, filter: String): List<Playlist> {
        return fakePage
    }

    override fun getFilteredCount(filter: String): Int {
        return fakePage.filter { it.name == filter }.size
    }

    override fun getTotalCount(): Int {
        return fakePage.size
    }

    override fun get(id: Long): Playlist {
        return fakePage[id.toInt()]
    }

    override fun indexOf(id: Long, filter: String): Int {
        TODO("Not yet implemented -> first implement the real method!")
    }
}