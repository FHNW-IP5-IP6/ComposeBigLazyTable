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

    private lateinit var controller: LazyTableController
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
        controller = LazyTableController(
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
        assertEquals(true, controller.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
        Log.info { "testing with firstVisibleItemIndex $firstVisibleItemIndex" }
    }

    @Test
    fun `isTimeToLoadPage returns false with 0 as firstVisibleItemIndex when already loaded first page`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val firstVisibleItemIndex = 0

        // when
        assertEquals(true, controller.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
        controller.loadNewPages(firstVisibleItemIndex = firstVisibleItemIndex)

        // then
        assertEquals(false, controller.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
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
            controller.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex)
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
            controller.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex)
        }
    }

    @Test
    fun `nbrOfTotalPages is rounded to the next integer when numberOfPlaylists or pageSize are not even`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        val isNumberOfPlaylistsAndPageSizeEven = numberOfPlaylists % 2 == 0 && controller.pageSize % 2 == 0
        val numberDividedByPageSize = numberOfPlaylists / controller.pageSize
        val expected = if (isNumberOfPlaylistsAndPageSizeEven) numberDividedByPageSize else numberDividedByPageSize + 1
        assertEquals(expected, controller.totalPages)
        Log.info { "expected: $expected == actual ${controller.totalPages}" }
    }

    @Test
    fun `selectModel sets the given playlistModel as selected`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"), appState)

        // when
        controller.selectModel(tableModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedTableModel)
    }

    @Test
    fun `selectModel does not throw an Exception`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"), appState)

        // then
        assertDoesNotThrow {
            // when
            controller.selectModel(tableModel = playlistModel)
        }
    }

    @Test
    fun `what happens if we pass an empty playlistModel`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val playlistModel = PlaylistModel(Playlist(), appState)

        // when
        controller.selectModel(tableModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedTableModel)
    }

    @Test
    fun `addPageToCache works with page 0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        controller.addPageToCache(pageNr = 0, pageOfModels = playlistModels)

        assertTrue(controller.isPageInCache(0))
    }

    @Test
    fun `addPageToCache works with page 1`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        controller.addPageToCache(pageNr = 1, pageOfModels = playlistModels)

        assertTrue(controller.isPageInCache(1))
    }

    @Test
    fun `addPageToCache doesnt work with different pages in load & check if it is in cache`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        controller.addPageToCache(pageNr = 1, pageOfModels = playlistModels)

        assertFalse(controller.isPageInCache(45))
    }

    @Test
    fun `loadSinglePage works with pageNrToLoad 0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        assertDoesNotThrow {
            controller.loadSinglePage(pageNrToLoad = 0)
        }
    }

    @Test
    fun `loadSinglePage works with pageNrToLoad 100`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        assertDoesNotThrow {
            controller.loadSinglePage(pageNrToLoad = 100)
        }
    }

    @Test
    fun `updateAppStateList works with currentVisiblePageNr=0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        controller.loadNewPages(0)
        assertDoesNotThrow {
            controller.updateAppStateList(currentVisiblePageNr = 0)
        }
    }

    @Test
    fun `addNewModelsToAppState works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        controller.loadNewPages(0)
        assertDoesNotThrow {
            controller.addNewModelsToAppState()
        }
    }

    @Test
    fun `removeOldModelsFromAppState works with index=0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        assertDoesNotThrow {
            controller.removeOldModelsFromAppState(pageNr = 0)
        }
    }

    @Test
    fun `removeOldModelsFromAppState works with index=24960`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        assertDoesNotThrow {
            controller.removeOldModelsFromAppState(pageNr = 24_960)
        }
    }

    @Test
    fun `calculateStartIndexToRemove works with index 0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val oldStartIndex = controller.calculateStartIndexToRemove(pageNr = 0,)
        assertEquals(0, oldStartIndex)
    }

    @Disabled("Enable when testing with numberOfPlaylists = 1_000_000")
    @Test
    fun `calculateStartIndexToRemove works with index 25000`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val oldStartIndex = controller.calculateStartIndexToRemove(pageNr = 25000)
        assertEquals(999_920, oldStartIndex)
    }

    @Test
    fun `removeOldPagesFromList works with startIndexToRemove=0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        assertDoesNotThrow {
            controller.removeOldPagesFromList(startIndexToRemove = 0)
        }
    }

    @Test
    fun `getVisiblePageNr works with list index 0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val pageNumber = controller.getVisiblePageNr(firstVisibleItemIndex = 0)
        assertEquals(0, pageNumber)
    }

    @Test
    fun `getVisiblePageNr works with list index 40`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val pageNumber = controller.getVisiblePageNr(firstVisibleItemIndex = 40)
        assertEquals(1, pageNumber)
    }

    @Test
    fun `getVisiblePageNr works with list index 25_000`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val pageNumber = controller.getVisiblePageNr(firstVisibleItemIndex = 25_000)
        assertEquals(625, pageNumber)
    }

    @Test
    fun `getVisiblePageNr works with list index 999_960`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val pageNumber = controller.getVisiblePageNr(firstVisibleItemIndex = 999_960)
        assertEquals(24_999, pageNumber)
    }

    @Test
    fun `getVisiblePageNr works with list index 999_961`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val pageNumber = controller.getVisiblePageNr(firstVisibleItemIndex = 999_961)
        assertEquals(24_999, pageNumber)
    }

    @Test
    fun `getVisiblePageNr works with list index 1_000_000`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val pageNumber = controller.getVisiblePageNr(firstVisibleItemIndex = 1_000_000)
        assertEquals(25_000, pageNumber)
    }

    @Test
    fun `getFirstIndexOfPage works with pageNr 0`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val startIndexToLoad = controller.getFirstIndexOfPage(pageNr = 0)
        assertEquals(0, startIndexToLoad)
    }

    @Test
    fun `getFirstIndexOfPage works with pageNr 1`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val startIndexToLoad = controller.getFirstIndexOfPage(pageNr = 1)
        assertEquals(40, startIndexToLoad)
    }

    @Test
    fun `getFirstIndexOfPage works with pageNr 2`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val startIndexToLoad = controller.getFirstIndexOfPage(pageNr = 2)
        assertEquals(80, startIndexToLoad)
    }

    @Test
    fun `getFirstIndexOfPage works with pageNr 625`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        val startIndexToLoad = controller.getFirstIndexOfPage(pageNr = 625)
        assertEquals(25_000, startIndexToLoad)
    }

    @Test
    fun `isPageInCache works with pageNr 1`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        controller.loadNewPages(0)
        val isInCache = controller.isPageInCache(1)
        assertEquals(true, isInCache)
    }

    @Test
    fun `isPageInCache doesnt work with pageNr 5345`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        controller.loadNewPages(0)
        val isInCache = controller.isPageInCache(5345)
        assertEquals(false, isInCache)
    }

}