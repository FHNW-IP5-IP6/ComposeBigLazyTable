package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.FakePagingService
import demo.bigLazyTable.utils.printTestMethodName
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

private val Log = KotlinLogging.logger {}

internal class LazyTableViewModelTest {

    private lateinit var viewModel: LazyTableViewModel
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
        viewModel = LazyTableViewModel(
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

    // TODO: Why is it not returning false?
//    @Test
//    fun `isTimeToLoadPage returns false with 0 as firstVisibleItemIndex when already loaded first page`() {
//        printTestMethodName(object {}.javaClass.enclosingMethod.name)
//
//        // given
//        val firstVisibleItemIndex = 0
//
//        // when
//        assertEquals(true, viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
//        viewModel.loadAllNeededPagesForIndex(firstVisibleItemIndex = firstVisibleItemIndex)
//
//        // then
//        assertEquals(false, viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex))
//        Log.info { "testing with firstVisibleItemIndex $firstVisibleItemIndex" }
//    }

    @Test
    fun `isTimeToLoadPage throws IllegalArgumentException with negative firstVisibleItemIndex`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val firstVisibleItemIndex = -44232

        Log.info { "testing with firstVisibleItemIndex $firstVisibleItemIndex" }
        // then
        assertThrows<IllegalArgumentException> {
            // when
            viewModel.isTimeToLoadPage(firstVisibleItemIndex = firstVisibleItemIndex)
        }
        // TODO: Log statement is surrounded by
        //  SLF4J: A number (23) of logging calls during the initialization phase have been intercepted and are
        //SLF4J: now being replayed. These are subject to the filtering rules of the underlying logging system.
        //SLF4J: See also http://www.slf4j.org/codes.html#replay
        //2022-01-21 18:23:41,449 INFO  - testing with firstVisibleItemIndex -44232
        //Exception in thread "AWT-EventQueue-0 @coroutine#1" java.lang.ExceptionInInitializerError
        //	at demo.bigLazyTable.model.LazyTableViewModel.addPageToCache(LazyTableViewModel.kt:57)
        //	at demo.bigLazyTable.model.LazyTableViewModel.access$addPageToCache(LazyTableViewModel.kt:15)
        //	at demo.bigLazyTable.model.LazyTableViewModel$1.invokeSuspend(LazyTableViewModel.kt:39)
        //	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
        //	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:106)
        //	at java.desktop/java.awt.event.InvocationEvent.dispatch(InvocationEvent.java:316)
        //	at java.desktop/java.awt.EventQueue.dispatchEventImpl(EventQueue.java:770)
        //	at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:721)
        //	at java.desktop/java.awt.EventQueue$4.run(EventQueue.java:715)
        //	at java.base/java.security.AccessController.doPrivileged(AccessController.java:391)
        //	at java.base/java.security.ProtectionDomain$JavaSecurityAccessImpl.doIntersectionPrivilege(ProtectionDomain.java:85)
        //	at java.desktop/java.awt.EventQueue.dispatchEvent(EventQueue.java:740)
        //	at java.desktop/java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:203)
        //	at java.desktop/java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:124)
        //	at java.desktop/java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:113)
        //	at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:109)
        //	at java.desktop/java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:101)
        //	at java.desktop/java.awt.EventDispatchThread.run(EventDispatchThread.java:90)
        //Caused by: java.lang.IllegalStateException: Please call Database.connect() before using this code
        //	at org.jetbrains.exposed.sql.transactions.NotInitializedManager.currentOrNull(TransactionApi.kt:38)
        //	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt.keepAndRestoreTransactionRefAfterRun(ThreadLocalTransactionManager.kt:219)
        //	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt.transaction(ThreadLocalTransactionManager.kt:134)
        //	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt.transaction(ThreadLocalTransactionManager.kt:131)
        //	at org.jetbrains.exposed.sql.transactions.ThreadLocalTransactionManagerKt.transaction$default(ThreadLocalTransactionManager.kt:130)
        //	at demo.bigLazyTable.data.database.DBService.getTotalCount(DBService.kt:59)
        //	at demo.bigLazyTable.model.AppState.<clinit>(AppState.kt:34)
        //	... 18 more
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
    fun `currentPage is 0 without doing any work`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertEquals(0, viewModel.currentPage)
    }

    @Test
    fun `currentPage is 24_999 after loadAllNeededPagesForIndex 999_999`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        viewModel.loadAllNeededPagesForIndex(999_999)
        assertEquals(24_999, viewModel.currentPage)
    }

    // TODO: When there is no Exception thrown at loadAllNeededPagesForIndex then too big indexes are possible
    @Test
    fun `currentPage is 25_000 after loadAllNeededPagesForIndex 1_000_000`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        viewModel.loadAllNeededPagesForIndex(1_000_000)
        assertEquals(25_000, viewModel.currentPage)
    }

    // TODO: When there is no Exception thrown at loadAllNeededPagesForIndex then too big indexes are possible
    //  and set currentPage to a value which is higher than the last possible page nr
    @Test
    fun `currentPage is 25_001 after loadAllNeededPagesForIndex 1_000_050`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        viewModel.loadAllNeededPagesForIndex(1_000_050)
        assertEquals(25_001, viewModel.currentPage)
    }

    // TODO: Exception in thread "AWT-EventQueue-0 @coroutine#4" java.lang.NoClassDefFoundError: Could not initialize class demo.bigLazyTable.model.AppState
//    @Test
//    fun `throws IllegalArgumentException after loadAllNeededPagesForIndex 1_000_000`() {
//        printTestMethodName(object {}.javaClass.enclosingMethod.name)
//        assertThrows<IllegalArgumentException> {
//            viewModel.loadAllNeededPagesForIndex(1_000_000)
//        }
//    }

    @Test
    fun `nbrOfTotalPages is rounded to the next integer when numberOfPlaylists or pageSize are not even`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        val isNumberOfPlaylistsAndPageSizeEven = numberOfPlaylists % 2 == 0 && viewModel.pageSize % 2 == 0
        val numberDividedByPageSize = numberOfPlaylists / viewModel.pageSize
        val expected = if (isNumberOfPlaylistsAndPageSizeEven) numberDividedByPageSize else numberDividedByPageSize + 1
        assertEquals(expected, viewModel.nbrOfTotalPages)
        Log.info { "expected: $expected == actual ${viewModel.nbrOfTotalPages}" }
    }

    @Test
    fun `isScrolling should always return false`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        assertFalse(viewModel.isScrolling)
    }

    // TODO: Sometimes it works & sometimes it dont
//    @Test
//    fun `selectPlaylist changes language correctly to deutsch`() {
//        printTestMethodName(object {}.javaClass.enclosingMethod.name)
//
//        // given
//        val playlistModel = PlaylistModel(Playlist(name = "test deutsch"), appState)
//
//        appState.selectedPlaylistModel.setCurrentLanguage("english")
//
//        // when
//        playlistModel.setCurrentLanguage("deutsch")
//        viewModel.selectPlaylist(playlistModel = playlistModel)
//
//        // then
//        assertEquals(playlistModel, appState.selectedPlaylistModel)
//        assertEquals("deutsch", appState.selectedPlaylistModel.getCurrentLanguage())
//    }

    // TODO: Sometimes it works & sometimes it dont
//    @Test
//    fun `selectPlaylist changes language correctly to english`() {
//        printTestMethodName(object {}.javaClass.enclosingMethod.name)
//
//        // given
//        val playlistModel = PlaylistModel(Playlist(name = "test english"), appState)
//
//        appState.selectedPlaylistModel.setCurrentLanguage("deutsch")
//
//        // when
//        playlistModel.setCurrentLanguage("english")
//        viewModel.selectPlaylist(playlistModel = playlistModel)
//
//        // then
//        assertEquals(playlistModel, appState.selectedPlaylistModel)
//        assertEquals("english", appState.selectedPlaylistModel.getCurrentLanguage())
//    }

    @Test
    fun `selectPlaylist sets the given playlistModel as selected`() {
        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"), appState)

        // when
        viewModel.selectPlaylist(playlistModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedPlaylistModel)
    }

    @Test
    fun `selectPlaylist does not throw an Exception`() {
        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"), appState)

        // then
        assertDoesNotThrow {
            // when
            viewModel.selectPlaylist(playlistModel = playlistModel)
        }
    }

    @Test
    fun `what happens if we pass an empty playlistModel`() {
        // given
        val playlistModel = PlaylistModel(Playlist(), appState)

        // when
        viewModel.selectPlaylist(playlistModel = playlistModel)

        // then
        assertEquals(playlistModel, appState.selectedPlaylistModel)
    }

    /*
    TODO: https://medium.com/mindorks/how-to-unit-test-private-methods-in-java-and-kotlin-d3cae49dccd
    properties:
        oldFirstVisibleItemIndex
        cache
    methods:
        suspend fun loadPageAndMapToPlaylistModels(startIndexOfPage: Int): List<PlaylistModel>
        fun addPageToCache(pageNr: Int, pageOfPlaylistModels: List<PlaylistModel>)
        fun loadPage(pageNrToLoad: Int, scrolledDown: Boolean)
        fun updateAppStateList(pageStartIndexToLoad: Int, pageToLoad: Int, isEnd: Boolean)
        fun addToAppStateList(startIndex: Int, newPageNr: Int)
        fun removeFromAppStateList(index: Int, isEnd: Boolean
        fun calculateStartIndexOfOldPage(index: Int, isEnd: Boolean): Int
        fun removeOldPageFromList(startIndexOldPage: Int
        fun calculatePageNumberForListIndex(listIndex: Int): Int
        calculatePageStartIndexToLoad(pageNr: Int): Int
        fun isPageInCache(pageNr: Int): Boolean
     */

    // TODO: Why is it returning emptyList?
//    @Test
//    fun `loadPageAndMapToPlaylistModels works with 0 as startIndexOfPage`() {
//        // when
////        // TODO: java.lang.NoSuchMethodException: demo.bigLazyTable.model.LazyTableViewModel.loadPageAndMapToPlaylistModels(int)
////        val loadPageAndMapToPlaylistModels =
////            LazyTableViewModel::class.java.getDeclaredMethod("loadPageAndMapToPlaylistModels", Int::class.java).apply {
////                isAccessible = true
////            }
////        val startIndexOfPage = 0
////        val returnValue = loadPageAndMapToPlaylistModels.invoke(viewModel, startIndexOfPage)
////        println("returnValue = ${returnValue.javaClass.name} $returnValue")
////        assertTrue(returnValue is List<*>)
//        runBlocking {
//            // TODO: Why do both return an empty list???
//            val playlistModels = viewModel.loadPageAndMapToPlaylistModels(startIndexOfPage = 5665)
//            val x = pagingService.getPage(startIndex = 0, pageSize = pageSize)
//            println(x)
//            assertEquals(0, x.first())
//        }
//    }

    @Test
    fun `addPageToCache works with page 0`() {
        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        viewModel.addPageToCache(pageNr = 0, pageOfPlaylistModels = playlistModels)

        assertTrue(viewModel.isPageInCache(0))
    }

    @Test
    fun `addPageToCache works with page 1`() {
        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        viewModel.addPageToCache(pageNr = 1, pageOfPlaylistModels = playlistModels)

        assertTrue(viewModel.isPageInCache(1))
    }

    @Test
    fun `addPageToCache doesnt work with different pages in load & check if it is in cache`() {
        val playlistModels = listOf(
            PlaylistModel(playlist = Playlist(), appState = appState)
        )
        viewModel.addPageToCache(pageNr = 1, pageOfPlaylistModels = playlistModels)

        assertFalse(viewModel.isPageInCache(45))
    }

}