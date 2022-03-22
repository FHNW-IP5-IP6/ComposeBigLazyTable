package demo.bigLazyTable.model

import demo.bigLazyTable.controller.AppState
import demo.bigLazyTable.data.database.FakePagingService
import demo.bigLazyTable.data.service.Playlist
import demo.bigLazyTable.data.service.loadingPlaceholderNumber
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

// TODO: Remove this test completely?
internal class PlaylistModelTest {

    lateinit var playlistModel: PlaylistModel

    @BeforeEach
    fun setUp() {
        playlistModel = PlaylistModel(
            playlist = Playlist(),
            appState = AppState(
                pagingService = FakePagingService(
                    numberOfPlaylists = 1_000_000,
                    pageSize = 40
                )
            )
        )
    }

    @Test
    fun getId() {
        assertEquals(loadingPlaceholderNumber.toLong(), playlistModel.id.getValue())
    }

    @Test
    fun getLazyListAttributes() {
        assertTrue(playlistModel.displayedAttributesInTable.isNotEmpty())
    }

    @Test
    fun updateChanges() {
        assertDoesNotThrow {
            playlistModel.updateChanges()
        }
    }
}