package demo.bigLazyTable.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection

// TODO: Instead of caseSensitiveFiltering: Boolean a queryParams: List<String> so that multiple query strings can be added and its more dynamic
class SqliteDb(pathToDb: String, caseSensitiveFiltering: Boolean) {

    private val makeSqliteCaseSensitive = "?case_sensitive_like=true"

    private val url = "jdbc:sqlite:$pathToDb${if (caseSensitiveFiltering) makeSqliteCaseSensitive else ""}"
    private val driver = "org.sqlite.JDBC"
    private val isolationLevel = Connection.TRANSACTION_SERIALIZABLE

    fun initializeConnection() {
        Database.connect(url = url, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = isolationLevel
    }

}