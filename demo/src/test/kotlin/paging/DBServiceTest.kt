package paging

import demo.bigLazyTable.data.database.DBService
import demo.bigLazyTable.model.Playlist
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.sql.Connection

private val Log = KotlinLogging.logger {}

internal class DBServiceTest {

    private lateinit var dbService: DBService
    private var startIndex = 0
    private val maxSizeTestDb = 1_000
    private var startIndexRandom = (0 until maxSizeTestDb).random() // if number bigger than maxSizeTestDb -> fails the tests
    private var startIndexNegative = -3133
    private val pageSize = 20

    private lateinit var page: List<*>
    private lateinit var randomPage: List<*>
    private lateinit var negativePage: List<*>

    @BeforeEach
    fun setupDatabase() {
        dbService = DBService
        Database.connect("jdbc:sqlite:./src/test/resources/test_spotify_playlist_dataset.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    private fun printTestMethodName(testMethodName: String) = println("### Testing method $testMethodName")

    private fun showStartLogMessage() = Log.info {
        "testing with start index " +
                "page=$startIndex, " +
                "randomPage=$startIndexRandom, " +
                "negativePage=$startIndexNegative. " +
                "Page size = $pageSize"
    }

    @Test
    suspend fun testGetPageReturnsPlaylists() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        showStartLogMessage()

        // when
        page = dbService.getPage(startIndex, pageSize, "")
        randomPage = dbService.getPage(startIndexRandom, pageSize, "")
        negativePage = dbService.getPage(startIndexNegative, pageSize, "")

        // then
        assertDoesNotThrow { page as List<Playlist> }
        assertDoesNotThrow { randomPage as List<Playlist> }
        assertDoesNotThrow { negativePage as List<Playlist> }

        assertTrue(page.first() is Playlist)
        assertTrue(page.last() is Playlist)
        assertTrue(randomPage.first() is Playlist)
        assertTrue(randomPage.last() is Playlist)
        assertTrue(negativePage.first() is Playlist)
        assertTrue(negativePage.last() is Playlist)

        // result logs
        Log.info { "page.size=${page.size}" }
        Log.info { "randomPage.size=${randomPage.size}" }
        Log.info { "negativePage.size=${negativePage.size}" }
        Log.info { "page.first=${page.first()}, page.last=${page.last()}" }
        Log.info { "randomPage.first=${randomPage.first()}, randomPage.last=${randomPage.last()}" }
        Log.info { "negativePage.first=${negativePage.first()}, negativePage.last=${negativePage.last()}" }
    }

    @Test
    suspend fun testGetPageStartsAtStartIndex() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        showStartLogMessage()

        // when
        page = dbService.getPage(startIndex, pageSize, "")
        randomPage = dbService.getPage(startIndexRandom, pageSize, "")
        negativePage = dbService.getPage(startIndexNegative, pageSize, "")

        // then
        assertEquals(startIndex, (page.first() as Playlist).id.toInt())
        assertEquals(startIndexRandom, (randomPage.first() as Playlist).id.toInt())
        assertEquals(0, (negativePage.first() as Playlist).id.toInt())

        // result logs
        Log.info { "page.first=${page.first()}" }
        Log.info { "randomPage.first=${randomPage.first()}" }
        Log.info { "negativePage.first=${negativePage.first()}" }
    }

    @Test
    suspend fun testGetPageReturnsPageSizeElements() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        showStartLogMessage()

        // when
        page = dbService.getPage(startIndex, pageSize, "")
        randomPage = dbService.getPage(startIndexRandom, pageSize, "")
        negativePage = dbService.getPage(startIndexNegative, pageSize, "")

        // then
        assertEquals(pageSize, page.size)
        assertEquals(pageSize, randomPage.size)
        assertEquals(pageSize, negativePage.size)

        // result logs
        Log.info { "page.size=${page.size}" }
        Log.info { "randomPage.size=${randomPage.size}" }
        Log.info { "negativePage.size=${negativePage.size}" }
    }

    @Test
    suspend fun testGetPageReturnsOnlyFilteredItems() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        showStartLogMessage()

        // when
        val filters = arrayOf("ert", "jdqd", "Boom", "I Put A Spell On You") // "" leads to java.lang.OutOfMemoryError: Java heap space
        Log.info { "With filter1=${filters[0]}, filter2=${filters[1]}, filter3=${filters[2]}" }

        // then
        for (i in filters.indices) {
            val filteredCount = dbService.getFilteredCount(filters[i])

            page = dbService.getPage(startIndex, filteredCount, filters[i])
            randomPage = dbService.getPage(startIndexRandom, filteredCount, filters[i])
            negativePage = dbService.getPage(startIndexNegative, filteredCount, filters[i])

            assertEquals(filteredCount, page.size)
            assertEquals(filteredCount, randomPage.size)
            assertEquals(filteredCount, negativePage.size)

            // TODO: If ignore case = false -> test fails!
            if (filteredCount != 0) {
                assertTrue((page as List<Playlist>).first().name.contains(filters[i], true))
                assertTrue((page as List<Playlist>).last().name.contains(filters[i], true))
                assertTrue((randomPage as List<Playlist>).first().name.contains(filters[i], true))
                assertTrue((randomPage as List<Playlist>).last().name.contains(filters[i], true))
                assertTrue((negativePage as List<Playlist>).first().name.contains(filters[i], true))
                assertTrue((negativePage as List<Playlist>).last().name.contains(filters[i], true))
            }

            // result logs
            Log.info { "filter$i=${filters[i]} filteredCount=$filteredCount" }
            Log.info { "page.size=${page.size}" }
            Log.info { "randomPage.size=${randomPage.size}" }
            Log.info { "negativePage.size=${negativePage.size}" }
            (page as List<Playlist>).apply { Log.info { "page: first.name=${first().name}, last.name=${last().name}" } }
            (randomPage as List<Playlist>).apply { Log.info { "randomPage: first.name=${first().name}, last.name=${last().name}" } }
            (negativePage as List<Playlist>).apply { Log.info { "negativePage: first.name=${first().name}, last.name=${last().name}" } }
        }
    }

    // TODO: How can we know if a returned value is "correct"?
    @Test
    suspend fun testGetFilteredCountReturnsCorrectSize() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        showStartLogMessage()

        // when
        val filters = arrayOf("ert", "jdqd", "Boom", "I Put A Spell On You") // "" leads to java.lang.OutOfMemoryError: Java heap space
        Log.info { "With filter1=${filters[0]}, filter2=${filters[1]}, filter3=${filters[2]}" }

        // then
        for (i in filters.indices) {
            val filteredCount = dbService.getFilteredCount(filters[i])

            page = dbService.getPage(startIndex, filteredCount, filters[i])
            randomPage = dbService.getPage(startIndexRandom, filteredCount, filters[i])
            negativePage = dbService.getPage(startIndexNegative, filteredCount, filters[i])

            assertEquals(filteredCount, page.size)
            assertEquals(filteredCount, randomPage.size)
            assertEquals(filteredCount, negativePage.size)

            // result logs
            Log.info { "filter$i=${filters[i]} filteredCount=$filteredCount" }
            Log.info { "page.size=${page.size}" }
            Log.info { "randomPage.size=${randomPage.size}" }
            Log.info { "negativePage.size=${negativePage.size}" }
        }
    }

    @Test
    fun testGetTotalCountReturnsCorrectSize() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        // given

        // when

        // then

    }

    @Test
    fun testGetWithValidIdReturnsOneObject() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        // given

        // when

        // then

    }

    @Test
    fun testGetWithInvalidIdReturnsError() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        // given

        // when

        // then

    }

    @Test
    fun testIndexOfWithValidIdReturnsCorrectIndex() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        // given

        // when

        // then

    }

    @Test
    fun testIndexOfWithInvalidIdReturnsError() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        // given

        // when

        // then

    }

}