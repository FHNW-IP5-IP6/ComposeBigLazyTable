package demo.bigLazyTable.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

object Db {
    // TODO: Pass path as param not hardcoded
    private const val url = "jdbc:sqlite:./demo/src/main/resources/spotify_playlist_dataset.db"
    private const val driver = "org.sqlite.JDBC"
    private const val isolationLevel = Connection.TRANSACTION_SERIALIZABLE

    fun initializeConnection() {
        Database.connect(url = url, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = isolationLevel
    }
}