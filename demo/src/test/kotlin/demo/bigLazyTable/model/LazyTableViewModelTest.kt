package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.FakePagingService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.ceil
import kotlin.math.exp

internal class LazyTableViewModelTest {

    private lateinit var viewModel: LazyTableViewModel
    private val numberOfPlaylists = 1_000_000

    @BeforeEach
    fun setUp() {
        viewModel = LazyTableViewModel(FakePagingService(numberOfPlaylists = numberOfPlaylists))
    }

    @Test
    fun getLastVisibleIndex() {
    }

    @Test
    fun setLastVisibleIndex() {
    }

    @Test
    fun getCurrentPage() {
    }

    @Test
    fun setCurrentPage() {
    }

    @Test
    fun testGetMaxPages() {
        val numberPlaylistsIsEven = numberOfPlaylists % 2 == 0
        val numberDividedByPageSize = numberOfPlaylists / viewModel.pageSize
        val expected = if (numberPlaylistsIsEven) numberDividedByPageSize else numberDividedByPageSize + 1
        assertEquals(expected, viewModel.maxPages)
    }

    @Test
    fun getScheduler() {
    }

    @Test
    fun getSchedulerCache() {
    }

    @Test
    fun isScrolling() {
        assertEquals(false, viewModel.isScrolling)
    }

    @Test
    fun setScrolling() {
    }

    @Test
    fun loadAllNeededPagesForIndex() {
    }

    @Test
    fun selectPlaylist() {
    }

    @Test
    fun isTimeToLoadPage() {
        assertEquals(false, viewModel.isTimeToLoadPage(0))
    }
}