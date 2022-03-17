package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.FakePagingService
import demo.bigLazyTable.utils.printTestMethodName
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private val Log = KotlinLogging.logger {}

internal class LazyTableControllerTest {

    private lateinit var viewModel: LazyTableController
    private lateinit var appState: AppState

    private val numberOfPlaylists = 5_000 //1_000_000
    private val pageSize = 40

    private var pagingService: FakePagingService = FakePagingService(
        numberOfPlaylists = numberOfPlaylists,
        pageSize = pageSize
    )

    @BeforeEach
    fun setUp() {
        appState = AppState(pagingService = pagingService)
        viewModel = LazyTableController(
            pagingService = pagingService,
            pageSize = pageSize,
            appState = appState
        )
    }

    @Test
    fun `isTimeToLoadPage returns true with 0 as firstVisibleItemIndex without loading any page`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val firstVisibleItemIndex = 0

        // then
        assertEquals(true, viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
        Log.info { "testing with firstVisibleItemIndex $firstVisibleItemIndex" }
    }

    @Test
    fun `isTimeToLoadPage returns false with 0 as firstVisibleItemIndex when already loaded first page`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val firstVisibleItemIndex = 0

        // when
        assertEquals(true, viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
        viewModel.loadNewPages(firstVisibleItemIndex = firstVisibleItemIndex)

        // then
        assertEquals(false, viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
        Log.info { "testing with firstVisibleItemIndex $firstVisibleItemIndex" }
    }

    @Test
    fun `isTimeToLoadPage throws IllegalArgumentException with negative firstVisibleItemIndex`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val firstVisibleItemIndex = -44232

        // then
        assertThrows<IllegalArgumentException> {
            // when
            viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex)
        }
    }

    @Test
    fun `isTimeToLoadPage throws IllegalArgumentException with total number of Playlists as firstVisibleItemIndex`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val firstVisibleItemIndex = numberOfPlaylists

        Log.info { "testing with firstVisibleItemIndex $firstVisibleItemIndex" }
        // then
        assertThrows<IllegalArgumentException> {
            // when
            viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex)
        }
    }

    @Test
    fun `nbrOfTotalPages is rounded to the next integer when numberOfPlaylists or pageSize are not even`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        val isNumberOfPlaylistsAndPageSizeEven = numberOfPlaylists % 2 == 0 && viewModel.pageSize % 2 == 0
        val numberDividedByPageSize = numberOfPlaylists / viewModel.pageSize
        val expected = if (isNumberOfPlaylistsAndPageSizeEven) numberDividedByPageSize else numberDividedByPageSize + 1
        assertEquals(expected, viewModel.totalPages)
        Log.info { "expected: $expected == actual ${viewModel.totalPages}" }
    }

    @Test
    fun `selectPlaylist sets the given playlistModel as selected`() {
        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"), appState)

        // when
        viewModel.selectModel(tableModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedTableModel)
    }

    @Test
    fun `selectPlaylist does not throw an Exception`() {
        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"), appState)

        // then
        assertDoesNotThrow {
            // when
            viewModel.selectModel(tableModel = playlistModel)
        }
    }

    @Test
    fun `what happens if we pass an empty playlistModel`() {
        // given
        val playlistModel = PlaylistModel(Playlist(), appState)

        // when
        viewModel.selectModel(tableModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedTableModel)
    }

    @Test
    fun `addPageToCache works with page 0`() {
        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        viewModel.addPageToCache(pageNr = 0, pageOfModels = playlistModels)

        assertTrue(viewModel.isPageInCache(0))
    }

    @Test
    fun `addPageToCache works with page 1`() {
        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        viewModel.addPageToCache(pageNr = 1, pageOfModels = playlistModels)

        assertTrue(viewModel.isPageInCache(1))
    }

    @Test
    fun `addPageToCache doesnt work with different pages in load & check if it is in cache`() {
        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        viewModel.addPageToCache(pageNr = 1, pageOfModels = playlistModels)

        assertFalse(viewModel.isPageInCache(45))
    }

    @Test
    fun `loadPage works with pageNrToLoad 0`() {
        assertDoesNotThrow {
            viewModel.loadSinglePage(pageNrToLoad = 0)
        }
    }

    @Test
    fun `loadPage works with pageNrToLoad 100`() {
        assertDoesNotThrow {
            viewModel.loadSinglePage(pageNrToLoad = 100)
        }
    }

    @Test
    fun `updateAppStateList works with currentVisiblePageNr=0`() {
        viewModel.loadNewPages(0)
        assertDoesNotThrow {
            viewModel.updateAppStateList(currentVisiblePageNr = 0)
        }
    }

    @Test
    fun `addToAppStateList works`() {
        viewModel.loadNewPages(0)
        assertDoesNotThrow {
            viewModel.addNewModelsToAppState()
        }
    }

    @Test
    fun `removeFromAppStateList works with index=0`() {
        assertDoesNotThrow {
            viewModel.removeOldModelsFromAppState(pageNr = 0)
        }
    }

    @Test
    fun `removeFromAppStateList works with index=24960`() {
        assertDoesNotThrow {
            viewModel.removeOldModelsFromAppState(pageNr = 24_960)
        }
    }

    @Test
    fun `calculateStartIndexToRemove works with index 0`() {
        val oldStartIndex = viewModel.calculateStartIndexToRemove(pageNr = 0,)
        assertEquals(0, oldStartIndex)
    }

    @Disabled("Enable when testing with numberOfPlaylists = 1_000_000")
    @Test
    fun `calculateStartIndexToRemove works with index 25000`() {
        val oldStartIndex = viewModel.calculateStartIndexToRemove(pageNr = 25000)
        assertEquals(999_920, oldStartIndex)
    }

    @Test
    fun `removeOldPagesFromList works with startIndexToRemove=0`() {
        assertDoesNotThrow {
            viewModel.removeOldPagesFromList(startIndexToRemove = 0)
        }
    }

    @Test
    fun `calculatePageNumberForListIndex works with list index 0`() {
        val pageNumber = viewModel.getVisiblePageNr(firstVisibleItemIndex = 0)
        assertEquals(0, pageNumber)
    }

    @Test
    fun `calculatePageNumberForListIndex works with list index 40`() {
        val pageNumber = viewModel.getVisiblePageNr(firstVisibleItemIndex = 40)
        assertEquals(1, pageNumber)
    }

    @Test
    fun `calculatePageNumberForListIndex works with list index 25_000`() {
        val pageNumber = viewModel.getVisiblePageNr(firstVisibleItemIndex = 25_000)
        assertEquals(625, pageNumber)
    }

    @Test
    fun `calculatePageNumberForListIndex works with list index 999_960`() {
        val pageNumber = viewModel.getVisiblePageNr(firstVisibleItemIndex = 999_960)
        assertEquals(24_999, pageNumber)
    }

    @Test
    fun `calculatePageNumberForListIndex works with list index 999_961`() {
        val pageNumber = viewModel.getVisiblePageNr(firstVisibleItemIndex = 999_961)
        assertEquals(24_999, pageNumber)
    }

    @Test
    fun `calculatePageNumberForListIndex works with list index 1_000_000`() {
        val pageNumber = viewModel.getVisiblePageNr(firstVisibleItemIndex = 1_000_000)
        assertEquals(25_000, pageNumber)
    }

    @Test
    fun `calculatePageStartIndexToLoad works with pageNr 0`() {
        val startIndexToLoad = viewModel.getFirstIndexOfPage(pageNr = 0)
        assertEquals(0, startIndexToLoad)
    }

    @Test
    fun `calculatePageStartIndexToLoad works with pageNr 1`() {
        val startIndexToLoad = viewModel.getFirstIndexOfPage(pageNr = 1)
        assertEquals(40, startIndexToLoad)
    }

    @Test
    fun `calculatePageStartIndexToLoad works with pageNr 2`() {
        val startIndexToLoad = viewModel.getFirstIndexOfPage(pageNr = 2)
        assertEquals(80, startIndexToLoad)
    }

    @Test
    fun `calculatePageStartIndexToLoad works with pageNr 625`() {
        val startIndexToLoad = viewModel.getFirstIndexOfPage(pageNr = 625)
        assertEquals(25_000, startIndexToLoad)
    }

    @Test
    fun `isPageInCache works with pageNr 1`() {
        viewModel.loadNewPages(0)
        val isInCache = viewModel.isPageInCache(1)
        assertEquals(true, isInCache)
    }

    @Test
    fun `isPageInCache doesnt work with pageNr 5345`() {
        viewModel.loadNewPages(0)
        val isInCache = viewModel.isPageInCache(5345)
        assertEquals(false, isInCache)
    }

}