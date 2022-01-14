package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.FakePagingService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class LazyTableViewModelTest {

    lateinit var viewModel: LazyTableViewModel

    @BeforeEach
    fun setUp() {
        viewModel = LazyTableViewModel(FakePagingService)
    }

    @AfterEach
    fun tearDown() {
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
    fun getMaxPages() {
        assertEquals(1, viewModel.maxPages)
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