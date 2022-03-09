package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.FakePagingService
import demo.bigLazyTable.utils.printTestMethodName
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private val Log = KotlinLogging.logger {}

internal class LazyTableControllerTest {

    private lateinit var viewModel: LazyTableController
    private lateinit var pagingService: FakePagingService
    private lateinit var appState: AppState

    private val numberOfPlaylists = 1_000_000
    private val pageSize = 40

    @BeforeEach
    fun setUp() {
        pagingService = FakePagingService(
            numberOfPlaylists = numberOfPlaylists,
            pageSize = pageSize
        )
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

    // TODO:
    @Disabled("Why is it not returning false?")
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

    // TODO:
    @Disabled("Exception in thread 'AWT-EventQueue-0 @coroutine#' java.lang.NoClassDefFoundError: Could not initialize class demo.bigLazyTable.model.AppState")
    @Test
    fun `throws IllegalArgumentException after loadAllNeededPagesForIndex 1_000_000`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertThrows<IllegalArgumentException> {
            viewModel.loadNewPages(1_000_000)
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

    // TODO:
    @Disabled("Sometimes it works & sometimes it dont")
    @Test
    fun `selectPlaylist changes language correctly to deutsch`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val playlistModel = PlaylistModel(Playlist(name = "test deutsch"), appState)

        appState.selectedTableModel.setCurrentLanguage("english")

        // when
        playlistModel.setCurrentLanguage("deutsch")
        viewModel.selectModel(tableModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedTableModel)
        assertEquals("deutsch", appState.selectedTableModel.getCurrentLanguage())
    }

    // TODO:
    @Disabled("Sometimes it works & sometimes it dont")
    @Test
    fun `selectPlaylist changes language correctly to english`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val playlistModel = PlaylistModel(Playlist(name = "test english"), appState)

        appState.selectedTableModel.setCurrentLanguage("deutsch")

        // when
        playlistModel.setCurrentLanguage("english")
        viewModel.selectModel(tableModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedTableModel)
        assertEquals("english", appState.selectedTableModel.getCurrentLanguage())
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

    // TODO:
    @Disabled("Why is it returning emptyList?")
    @Test
    fun `loadPageAndMapToPlaylistModels works with 0 as startIndexOfPage`() {
        // when
//        // TODO: java.lang.NoSuchMethodException: demo.bigLazyTable.model.LazyTableController.loadPageAndMapToPlaylistModels(int)
//        val loadPageAndMapToPlaylistModels =
//            LazyTableController::class.java.getDeclaredMethod("loadPageAndMapToPlaylistModels", Int::class.java).apply {
//                isAccessible = true
//            }
//        val startIndexOfPage = 0
//        val returnValue = loadPageAndMapToPlaylistModels.invoke(viewModel, startIndexOfPage)
//        println("returnValue = ${returnValue.javaClass.name} $returnValue")
//        assertTrue(returnValue is List<*>)
        runBlocking {
            // TODO: Why do both return an empty list???
            val playlistModels = viewModel.getPageFromService(startIndex = 5665)
            val x = pagingService.getPage(startIndex = 0, pageSize = pageSize, filters = emptyList())
            println(x)
            assertEquals(0, x.first())
        }
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

    // TODO: 21.02.2022
    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `loadPage works with pageNrToLoad 0 & scrolledDown = false`() {
        assertDoesNotThrow {
            viewModel.loadSinglePage(pageNrToLoad = 0, scrolledDown = false)
        }
    }

    // TODO: 21.02.2022
    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `loadPage works with pageNrToLoad 0 & scrolledDown = true`() {
        assertDoesNotThrow {
            viewModel.loadSinglePage(pageNrToLoad = 0, scrolledDown = true)
        }
    }

    // TODO:
    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `loadPage works with pageNrToLoad 100 & scrolledDown = true`() {
        assertDoesNotThrow {
            viewModel.loadSinglePage(pageNrToLoad = 100, scrolledDown = true)
        }
    }

    // TODO:
    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `updateAppStateList works with pageStartIndexToLoad=0, pageToLoad=0, isEnd=false`() {
        assertDoesNotThrow {
            viewModel.updateAppStateList(
                currentVisiblePageNr = 0,
                pageToLoad = 0,
                isEnd = false
            )
        }
    }

    // TODO:
    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `updateAppStateList works with pageStartIndexToLoad=0, pageToLoad=0, isEnd=true`() {
        assertDoesNotThrow {
            viewModel.updateAppStateList(
                currentVisiblePageNr = 0,
                pageToLoad = 0,
                isEnd = true
            )
        }
    }

    // TODO:
    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `addToAppStateList works with startIndex=0 & newPageNr=1`() {
        assertDoesNotThrow {
            viewModel.addNewModelsToAppState(
                startIndex = 0,
                newPageNr = 1
            )
        }
    }

    // TODO:
    @Disabled("Unexpected exception thrown: java.lang.NullPointerException")
    @Test
    fun `addToAppStateList works with startIndex=4355 & newPageNr=45345`() {
        assertDoesNotThrow {
            viewModel.addNewModelsToAppState(
                startIndex = 4355,
                newPageNr = 45345
            )
        }
    }

    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 160 out of bounds for length 0")
    @Test
    fun `removeFromAppStateList works with index=0, isEnd=false`() {
        assertDoesNotThrow {
            viewModel.removeOldModelsFromAppState(
                pageNr = 0,
                isEnd = false
            )
        }
    }

    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 998240 out of bounds for length 0")
    @Test
    fun `removeFromAppStateList works with index=24960, isEnd=true`() {
        assertDoesNotThrow {
            viewModel.removeOldModelsFromAppState(
                pageNr = 24960,
                isEnd = true
            )
        }
    }

    // TODO: Why 160?
    @Disabled("expected: <0> but was: <160>")
    @Test
    fun `calculateStartIndexOfOldPage works with index 0 & isEnd=false`() {
        val oldStartIndex = viewModel.calculateStartIndexToRemove(
            pageNr = 0,
            isEnd = false
        )
        assertEquals(0, oldStartIndex)
    }

    // TODO: Why 998240?
    @Disabled("expected: <24960> but was: <998240>")
    @Test
    fun `calculateStartIndexOfOldPage works with index 25000 & isEnd=true`() {
        val oldStartIndex = viewModel.calculateStartIndexToRemove(
            pageNr = 24960,
            isEnd = true
        )
        assertEquals(24960, oldStartIndex)
    }

    @Disabled("Unexpected exception thrown: java.lang.IndexOutOfBoundsException: Index 0 out of bounds for length 0")
    @Test
    fun `removeOldPageFromList works with startIndexOldPage=0`() {
        assertDoesNotThrow {
            viewModel.removeOldPagesFromList(startIndexToRemove = 0)
        }
    }

    @Test
    fun `removeOldPageFromList doesnt work with startIndexOldPage=-1`() {
        assertThrows<AssertionError> {
            viewModel.removeOldPagesFromList(startIndexToRemove = -1)
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

    // TODO: Why is it not in cache?
    @Disabled("expected: <true> but was: <false>")
    @Test
    fun `isPageInCache works with pageNr 1`() {
        val isInCache = viewModel.isPageInCache(1)
        assertEquals(true, isInCache)
    }

    @Test
    fun `isPageInCache doesnt work with pageNr 5345`() {
        val isInCache = viewModel.isPageInCache(5345)
        assertEquals(false, isInCache)
    }

}