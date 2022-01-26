package demo.bigLazyTable.model

import demo.bigLazyTable.data.database.FakePagingService
import demo.bigLazyTable.utils.printTestMethodName
import mu.KotlinLogging
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

private val Log = KotlinLogging.logger {}

internal class LazyTableViewModelTest {

    private lateinit var viewModel: LazyTableViewModel
    private val numberOfPlaylists = 1_000_000
    private val pageSize = 40

    @BeforeEach
    fun setUp() {
        val pagingService = FakePagingService(
            numberOfPlaylists = numberOfPlaylists,
            pageSize = pageSize
        )
        viewModel = LazyTableViewModel(
            pagingService = pagingService,
            pageSize = pageSize
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

//    // Exception in thread "AWT-EventQueue-0 @coroutine#4" java.lang.NoClassDefFoundError: Could not initialize class demo.bigLazyTable.model.AppState
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

    // Could not initialize class demo.bigLazyTable.model.AppState
    @Test
    fun `selectPlaylist changes language correctly`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"))

        // when
        playlistModel.setCurrentLanguage("english")
        viewModel.selectPlaylist(playlistModel = playlistModel)

        // then
        assertEquals("english", AppState.selectedPlaylistModel.getCurrentLanguage())
    }

    // TODO: Could not initialize class demo.bigLazyTable.model.AppState
    //   java.lang.NoClassDefFoundError: Could not initialize class demo.bigLazyTable.model.AppState
    //	 at demo.bigLazyTable.model.LazyTableViewModel.selectPlaylist(LazyTableViewModel.kt:143)
    //	 at demo.bigLazyTable.model.LazyTableViewModelTest.selectPlaylist sets the given playlistModel as selected(LazyTableViewModelTest.kt:199)
    @Test
    fun `selectPlaylist sets the given playlistModel as selected`() {
        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"))

        // when
        viewModel.selectPlaylist(playlistModel = playlistModel)

        // then
        assertEquals(playlistModel, AppState.selectedPlaylistModel)
    }

    // TODO: Unexpected exception thrown: java.lang.NoClassDefFoundError: Could not initialize class demo.bigLazyTable.model.AppState
    @Test
    fun `selectPlaylist does not throw an Exception`() {
        // given
        val playlistModel = PlaylistModel(Playlist(name = "test"))

        // then
        assertDoesNotThrow {
            // when
            viewModel.selectPlaylist(playlistModel = playlistModel)
        }
    }

    @Test
    fun `what happens if we pass an empty playlistModel`() {
        // given
        val playlistModel = PlaylistModel(Playlist())

        // when
        viewModel.selectPlaylist(playlistModel = playlistModel)

        // then
        assertEquals(playlistModel, AppState.selectedPlaylistModel)
    }

}