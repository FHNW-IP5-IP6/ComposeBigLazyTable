package demo.bigLazyTable.model

/**
 * @author Marco Sprenger, Livio Näf
 */
data class Playlist(
    val id: Long = -1,
    val name: String = "",
    val collaborative: Boolean = false,
    val modifiedAt: String = ""
)