package demo.bigLazyTable.data.database

import bigLazyTable.paging.Filter
import bigLazyTable.paging.StringFilter
import demo.bigLazyTable.model.Playlist
import demo.bigLazyTable.utils.printTestMethodName
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.sql.Connection
import java.util.NoSuchElementException

private val Log = KotlinLogging.logger {}

internal class DBServiceTest {

    private lateinit var dbService: DBService

    private val sizeTestDb = 1_001
    private val lastTestDbIndice = sizeTestDb - 1

    private var startIndex = 0
    private var startIndexRandom = (0 until lastTestDbIndice).random()
    private var startIndexNegative = -3133

    private val pageSize = 20

    private lateinit var page: List<Playlist>
    private lateinit var randomPage: List<Playlist>
    private lateinit var negativePage: List<Playlist>

    @BeforeEach
    fun setupDatabase() {
        dbService = DBService
        val makeSqliteCaseSensitive = "?case_sensitive_like=true"
        Database.connect(
            "jdbc:sqlite:./src/test/resources/test_spotify_playlist_dataset.db$makeSqliteCaseSensitive",
            "org.sqlite.JDBC"
        )
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }

    private fun printFixedTestValues() = Log.info {
        "testing with start index " +
                "page=$startIndex, " +
                "randomPage=$startIndexRandom, " +
                "negativePage=$startIndexNegative, " +
                "Page size = $pageSize, " +
                "Size Test DB = $sizeTestDb"
    }

    @Test
    fun `getPage with startIndex 0 returns a list of Playlist`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            page = dbService.getPage(startIndex, pageSize)

            // then
            assertTrue(page.isNotEmpty())
            assertEquals(0, page.first().id)
            assertEquals(pageSize - 1, page.last().id.toInt())

            // result logs
            Log.info { "page.size=${page.size}" }
            Log.info { "page.first=${page.first().id}, page.last=${page.last().id}" }
        }
    }

    @Test
    fun `getPage with random startIndex returns a list of Playlist`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            randomPage = dbService.getPage(startIndexRandom, pageSize)

            // then
            assertTrue(randomPage.isNotEmpty())
            assertEquals(startIndexRandom, randomPage.first().id.toInt())
            assertEquals(startIndexRandom + pageSize - 1, randomPage.last().id.toInt())

            // result logs
            Log.info { "randomPage.size=${randomPage.size}" }
            Log.info { "randomPage.first=${randomPage.first().id}, randomPage.last=${randomPage.last().id}" }
        }
    }

    @Test
    fun `getPage with invalid startIndex throws an IllegalArgumentException`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            assertThrows<IllegalArgumentException> {
                negativePage = dbService.getPage(startIndexNegative, pageSize)
                Log.info { "Has not thrown Exception!" }
            }
            Log.info { "Has thrown Exception!" }
        }
    }

    @Test
    fun `getPage throws IllegalArgumentException on negative startIndex`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            assertThrows<IllegalArgumentException> {
                Log.info { "Try to call getPage with negative startIndex $startIndexNegative" }
                dbService.getPage(startIndexNegative, pageSize)
                Log.info { "Has not thrown Exception!" }
            }
            Log.info { "Has thrown Exception!" }
        }
    }

    @Test
    fun `getPage does not throw IllegalArgumentException on last index - pageSize`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        Log.info { "Try to call getPage with last index - pageSize($pageSize) ${lastTestDbIndice - pageSize}" }
        assertDoesNotThrow {
            runBlocking {
                dbService.getPage(lastTestDbIndice - pageSize, pageSize)
            }
        }
        Log.info { "Has not thrown Exception!" }
    }

    @Test
    fun `getPage does not throw IllegalArgumentException on second last index & pageSize=1`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        val secondLastIndex = lastTestDbIndice - 1
        Log.info { "Try to call getPage with second last index & pageSize=1 $secondLastIndex" }
        assertDoesNotThrow {
            runBlocking {
                dbService.getPage(secondLastIndex, 1)
            }
        }
        Log.info { "Has not thrown Exception!" }
    }

    @Test
    fun `getPage does not throw IllegalArgumentException on last index & pageSize`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        Log.info { "Try to call getPage with last index $lastTestDbIndice" }
        assertDoesNotThrow {
            runBlocking {
                dbService.getPage(lastTestDbIndice, pageSize)
            }
        }
        Log.info { "Has not thrown Exception!" }
    }

    @Test
    fun `getPage throws IllegalArgumentException with size of test db as startIndex`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            assertThrows<IllegalArgumentException> {
                // when
                Log.info { "Try to call getPage with too big startIndex $sizeTestDb" }
                dbService.getPage(sizeTestDb, pageSize)
                Log.info { "Has not thrown Exception!" }
            }
            Log.info { "Has thrown Exception!" }
        }
    }

    @Test
    fun `getPage throws IllegalArgumentException with value bigger than size of test db as startIndex`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            val moreThanMax = sizeTestDb + 100
            assertThrows<IllegalArgumentException> {
                // when
                Log.info { "Try to call getPage with too big startIndex $moreThanMax" }
                dbService.getPage(moreThanMax, pageSize)
                Log.info { "Has not thrown Exception!" }
            }
            Log.info { "Has thrown Exception!" }
        }
    }

    @Test
    fun `getPage starts at start index`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            page = dbService.getPage(startIndex, pageSize)
            randomPage = dbService.getPage(startIndexRandom, pageSize)

            // then
            assertEquals(startIndex, page.first().id.toInt())
            assertEquals(startIndexRandom, randomPage.first().id.toInt())

            // result logs
            Log.info { "page.first=${page.first().id}" }
            Log.info { "randomPage.first=${randomPage.first().id}" }
        }
    }

    @Test
    fun `getPage returns pageSize elements`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            page = dbService.getPage(startIndex, pageSize)
            randomPage = dbService.getPage(startIndexRandom, pageSize)

            // then
            assertEquals(pageSize, page.size)
            assertEquals(pageSize, randomPage.size)

            // result logs
            Log.info { "page.size=${page.size}" }
            Log.info { "randomPage.size=${randomPage.size}" }
        }
    }

    @Test
    fun `getPage returns only filtered items (no case sensitive)`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            val filters = arrayOf("A", "a", "E", "e", "O", "o", "I Put A Spell On You")

            // then
            for (i in filters.indices) {
                val stringFilters = listOf(StringFilter(filters[i], DatabasePlaylists.name, caseSensitive = false))

                Log.info { "With filter$i=${filters[i]}" }

                val filteredCount = dbService.getFilteredCount(stringFilters)
                page = dbService.getPage(startIndex, filteredCount, stringFilters)
                assertEquals(filteredCount, page.size)

                if (filteredCount != 0) {
                    assertTrue((page).first().name.contains(filters[i], ignoreCase = true))
                    assertTrue((page).last().name.contains(filters[i], ignoreCase = true))
                }

                // result logs
                Log.info { "filter$i=${filters[i]} filteredCount=$filteredCount" }
                Log.info { "page.size=${page.size}" }
                Log.info { "page: first.name=${page.first().name}, last.name=${page.last().name}" }
            }
        }
    }

    @Test
    fun `getPage returns only filtered items (case sensitive)`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            val filters = arrayOf("A", "a", "E", "e", "O", "o", "I Put A Spell On You")

            // then
            for (i in filters.indices) {
                val stringFilters = listOf(StringFilter(filters[i], DatabasePlaylists.name, caseSensitive = true))

                Log.info { "With filter$i=${filters[i]}" }

                val filteredCount = dbService.getFilteredCount(stringFilters)
                page = dbService.getPage(startIndex, filteredCount, stringFilters)
                assertEquals(filteredCount, page.size)

                if (filteredCount != 0) {
                    assertTrue((page).first().name.contains(filters[i], ignoreCase = false))
                    assertTrue((page).last().name.contains(filters[i], ignoreCase = false))
                }

                // result logs
                Log.info { "filter$i=${filters[i]} filteredCount=$filteredCount" }
                Log.info { "page.size=${page.size}" }
                Log.info { "page: first.name=${page.first().name}, last.name=${page.last().name}" }
            }
        }
    }

    @Test
    fun `getFilteredCount returns correct size`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            val filters = arrayOf("A", "a", "E", "e", "O", "o", "I Put A Spell On You")

            // then
            for (i in filters.indices) {
                val stringFilters = listOf(StringFilter(filters[i], DatabasePlaylists.name, caseSensitive = true))

                Log.info { "With filter$i=${filters[i]}" }

                val filteredCount = dbService.getFilteredCount(stringFilters)
                page = dbService.getPage(startIndex, filteredCount, stringFilters)

                assertEquals(filteredCount, page.size)

                // result logs
                Log.info { "filter$i=${filters[i]} filteredCount=$filteredCount" }
                Log.info { "page.size=${page.size}" }
            }
        }
    }

    @Test
    fun `getFilteredCount throws IllegalArgumentException on empty stringFilter list`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // when
            val stringFilters = emptyList<Filter>()

            // then
            Log.info { "With filter=empty filter" }

            assertThrows<IllegalArgumentException> {
                // when
                dbService.getFilteredCount(stringFilters)
                Log.info { "Has not thrown Exception!" }
            }
            Log.info { "Has thrown Exception!" }
        }
    }


    @Test
    fun `getTotalCount returns correct size`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        // when
        val totalCount = dbService.getTotalCount()

        // then
        assertEquals(sizeTestDb, totalCount)
        Log.info { "maxSizeTestDb=$sizeTestDb == getTotalCount()=$totalCount" }
    }

    @Test
    fun `get with valid id returns that object`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        runBlocking {
            // given
            page = dbService.getPage(startIndex, pageSize)

            // when
            for (i in page.indices) {

                // then
                assertEquals(page[i], dbService.get(i.toLong()))
                Log.info {
                    "${page[i].id} == ${dbService.get(i.toLong()).id}"
                }
            }
        }
    }

    @Test
    fun `get with invalid id returns NoSuchElementException`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)
        printFixedTestValues()

        // given
        val invalidId = -1L

        // then
        assertThrows<NoSuchElementException> {
            // when
            Log.info { "DB Service to get $invalidId" }
            dbService.get(invalidId)
            Log.info { "Has not thrown Exception!" }
        }
        Log.info { "Has thrown Exception!" }
    }

    // TODO: Implement indexOf method first!
//    @Test
//    fun `indexOf with valid id returns correct index`() {
//        printTestMethodName(object {}.javaClass.enclosingMethod.name)
//
//        // given
//        val correctIndex = -1
//
//        // when
//        val randomItemIndex = dbService.indexOf(startIndexRandom.toLong())
//
//        // then
//        assertEquals(correctIndex, randomItemIndex)
//        Log.info { "index $correctIndex == randomItemIndexForTest $randomItemIndex" }
//    }

    @Test
    fun `indexOf with invalid id returns Exception`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // when
        val invalidIndex = -6L

        // then
        assertThrows<IllegalArgumentException> {
            dbService.indexOf(invalidIndex)
            Log.info { "Has not thrown Exception!" }
        }
        Log.info { "Has thrown Exception!" }
    }

}