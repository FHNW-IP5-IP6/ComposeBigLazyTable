package demo.bigLazyTable.data

import org.jetbrains.exposed.sql.Table

/**
 * @author Marco Sprenger
 * @author Livio Näf
 */
object DatabasePlaylists : Table() {
    val id = long("id")
    val name = varchar("name", length = 100)
    val collaborative = bool("collaborative")
    val modified_at = varchar("modified_at", length = 20)

    override val primaryKey = PrimaryKey(id, name = "PK_DatabasePlaylist_ID")
}