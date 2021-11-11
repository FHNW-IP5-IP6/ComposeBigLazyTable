package demo.bigLazyTable.model

/**
 * @author Marco Sprenger, Livio Näf
 */
data class Playlist(
    val name: String = "",
    val collaborative: Boolean = false,
    val modifiedAt: String = "",
    val id: Long = 0
)