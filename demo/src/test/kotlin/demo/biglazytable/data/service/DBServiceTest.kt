package demo.biglazytable.data.service

import bigLazyTable.data.paging.*
import demo.bigLazyTable.spotifyPlaylists.data.database.DatabasePlaylists
import demo.bigLazyTable.spotifyPlaylists.data.service.DBService
import demo.bigLazyTable.spotifyPlaylists.data.service.Playlist
import demo.biglazytable.utils.printTestMethodName
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.sql.Connection
import java.util.NoSuchElementException

private val Log = KotlinLogging.logger {}

internal class DBServiceTest {

    private val sizeTestDb = 1_001
    private val lastTestDbIndice = sizeTestDb - 1

    private var startIndex = 0
    private var startIndexRandom = (0 until lastTestDbIndice).random()
    private var startIndexNegative = -3133

    private val pageSize = 20

    private lateinit var randomPage: List<Playlist>
    private lateinit var negativePage: List<Playlist>

    @BeforeEach
    fun setupDatabase() {
        val makeSqliteCaseSensitive = "?case_sensitive_like=true"
        Database.connect(
            "jdbc:sqlite:./src/test/resources/test_spotify_playlist_dataset.db$makeSqliteCaseSensitive",
            "org.sqlite.JDBC"
        )
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        println("total count ${DBService.getTotalCount()}")
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
            val page = DBService.getPage(startIndex, pageSize)

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
    fun `getPage with startIndex 989 returns a list of Playlist`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        runBlocking {
            // when
            val startIndex = 989
            val page = DBService.getPage(startIndex, pageSize)

            // then
            assertTrue(page.isNotEmpty())
            assertEquals(startIndex, page.first().id.toInt())
            assertEquals(lastTestDbIndice, page.last().id.toInt())

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
            randomPage = DBService.getPage(startIndexRandom, pageSize)

            // then
            assertTrue(randomPage.isNotEmpty())
            assertEquals(startIndexRandom, randomPage.first().id.toInt())
            val lastIndex = startIndexRandom + pageSize - 1
            val lastIndexCheck = if (lastIndex > lastTestDbIndice) lastTestDbIndice else lastIndex
            assertEquals(lastIndexCheck, randomPage.last().id.toInt())

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
                negativePage = DBService.getPage(startIndexNegative, pageSize)
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
                DBService.getPage(startIndexNegative, pageSize)
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
                DBService.getPage(lastTestDbIndice - pageSize, pageSize)
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
                DBService.getPage(secondLastIndex, 1)
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
                DBService.getPage(lastTestDbIndice, pageSize)
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
                DBService.getPage(sizeTestDb, pageSize)
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
                DBService.getPage(moreThanMax, pageSize)
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
            val page = DBService.getPage(startIndex, pageSize)
            randomPage = DBService.getPage(startIndexRandom, pageSize)

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
            val page = DBService.getPage(startIndex, pageSize)
            val randomPage = DBService.getPage(startIndexRandom, pageSize)

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
                val stringFilters = listOf(StringFilter("%${filters[i]}%", DatabasePlaylists.name, caseSensitive = false))

                Log.info { "With filter$i=${filters[i]}" }

                val filteredCount = DBService.getFilteredCount(stringFilters)
                val page = DBService.getPage(startIndex, filteredCount, stringFilters)
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
                val stringFilters = listOf(StringFilter("%${filters[i]}%", DatabasePlaylists.name, caseSensitive = true))

                Log.info { "With filter$i=${filters[i]}" }

                val filteredCount = DBService.getFilteredCount(stringFilters)
                val page = DBService.getPage(startIndex, filteredCount, stringFilters)
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
                val stringFilters = listOf(StringFilter("%${filters[i]}%", DatabasePlaylists.name, caseSensitive = true))

                Log.info { "With filter$i=${filters[i]}" }

                val filteredCount = DBService.getFilteredCount(stringFilters)
                val page = DBService.getPage(startIndex, filteredCount, stringFilters)

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
                DBService.getFilteredCount(stringFilters)
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
        val totalCount = DBService.getTotalCount()

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
            val page = DBService.getPage(startIndex, pageSize)

            // when
            for (i in page.indices) {

                // then
                assertEquals(page[i], DBService.get(i.toLong()))
                Log.info {
                    "${page[i].id} == ${DBService.get(i.toLong()).id}"
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
            DBService.get(invalidId)
            Log.info { "Has not thrown Exception!" }
        }
        Log.info { "Has thrown Exception!" }
    }

    @Test
    fun `indexOf with invalid id returns Exception`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // when
        val invalidIndex = -6L

        // then
        assertThrows<IllegalArgumentException> {
            DBService.indexOf(invalidIndex)
            Log.info { "Has not thrown Exception!" }
        }
        Log.info { "Has thrown Exception!" }
    }

    @Test
    fun `greater filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val value = 2L
        val filter = LongFilter(value, DatabasePlaylists.id, FilterOperation.GREATER)

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = null
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertTrue(page.first().id > value)
        assertTrue(page.last().id > value)
    }

    @Test
    fun `greater equals filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val value = 2
        val filter = LongFilter(2, DatabasePlaylists.id, FilterOperation.GREATER_EQUALS)

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = null
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertTrue(page.first().id >= value)
        assertTrue(page.last().id >= value)
    }

    @Test
    fun `less filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val value = 40L
        val filter = LongFilter(value, DatabasePlaylists.id, FilterOperation.LESS)

        // when
        val page = DBService.getPage(
            value.toInt()-3, // must be smaller than defined value
            pageSize,
            filters = listOf(filter),
            sort = null
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertTrue(page.first().id < value)
        assertTrue(page.last().id < value)
    }

    @Test
    fun `less equals filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val value = 40L
        val filter = LongFilter(value, DatabasePlaylists.id, FilterOperation.LESS_EQUALS)

        // when
        val page = DBService.getPage(
            value.toInt()-3,
            pageSize,
            filters = listOf(filter),
            sort = null
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertTrue(page.first().id <= value)
        assertTrue(page.last().id <= value)
    }

    @Test
    fun `equals filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val value = 40L
        val filter = LongFilter(value, DatabasePlaylists.id, FilterOperation.EQUALS)

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = null
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertEquals(value, page.first().id)
        assertEquals(value, page.last().id)
    }

    @Test
    fun `not equals filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val value = 40L
        val filter = LongFilter(value, DatabasePlaylists.id, FilterOperation.NOT_EQUALS)

        // when
        val page = DBService.getPage(
            value.toInt(),
            pageSize,
            filters = listOf(filter),
            sort = null
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertNotEquals(value, page.first().id)
        assertNotEquals(value, page.last().id)
    }

    @Test
    fun `between both included filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val from = 1L
        val to = 10L
        val fromFilter = LongFilter(from, DatabasePlaylists.id, FilterOperation.BETWEEN_BOTH_INCLUDED)
        val toFilter = LongFilter(to, DatabasePlaylists.id, FilterOperation.BETWEEN_BOTH_INCLUDED)
        val filter = LongFilter(
            0,
            DatabasePlaylists.id,
            FilterOperation.BETWEEN_BOTH_INCLUDED,
            between = Between(fromFilter, toFilter)
        )

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = Sort(DatabasePlaylists.id, SortOrder.ASC)
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertEquals(from, page.first().id)
        assertEquals(to, page.last().id)
    }

    @Test
    fun `between both not included filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val from = 1L
        val to = 10L
        val fromFilter = LongFilter(from, DatabasePlaylists.id, FilterOperation.BETWEEN_BOTH_NOT_INCLUDED)
        val toFilter = LongFilter(to, DatabasePlaylists.id, FilterOperation.BETWEEN_BOTH_NOT_INCLUDED)
        val filter = LongFilter(
            6,
            DatabasePlaylists.id,
            FilterOperation.BETWEEN_BOTH_NOT_INCLUDED,
            between = Between(fromFilter, toFilter)
        )

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = Sort(DatabasePlaylists.id, SortOrder.ASC)
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertTrue(from < page.first().id)
        assertTrue(to > page.last().id)
    }

    @Test
    fun `between from included filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val from = 1L
        val to = 10L
        val fromFilter = LongFilter(from, DatabasePlaylists.id, FilterOperation.BETWEEN_FROM_INCLUDED)
        val toFilter = LongFilter(to, DatabasePlaylists.id, FilterOperation.BETWEEN_FROM_INCLUDED)
        val filter = LongFilter(
            6,
            DatabasePlaylists.id,
            FilterOperation.BETWEEN_FROM_INCLUDED,
            between = Between(fromFilter, toFilter)
        )

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = Sort(DatabasePlaylists.id, SortOrder.ASC)
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertEquals(from, page.first().id)
        assertTrue(to > page.last().id)
    }

    @Test
    fun `between to included filter works`() {
        printTestMethodName(object {}.javaClass.enclosingMethod.name)

        // given
        val from = 1L
        val to = 10L
        val fromFilter = LongFilter(from, DatabasePlaylists.id, FilterOperation.BETWEEN_TO_INCLUDED)
        val toFilter = LongFilter(to, DatabasePlaylists.id, FilterOperation.BETWEEN_TO_INCLUDED)
        val filter = LongFilter(
            6,
            DatabasePlaylists.id,
            FilterOperation.BETWEEN_TO_INCLUDED,
            between = Between(fromFilter, toFilter)
        )

        // when
        val page = DBService.getPage(
            startIndex,
            pageSize,
            filters = listOf(filter),
            sort = Sort(DatabasePlaylists.id, SortOrder.ASC)
        )

        // then
        Log.info { "first ${page.first().id} last ${page.last().id}" }
        assertTrue(page.isNotEmpty())
        assertTrue(from < page.first().id)
        assertEquals(to, page.last().id)
    }

}