package demo.bigLazyTable.data.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import java.sql.Connection
import kotlin.system.exitProcess

class SqliteDb(
    pathToDb: String,
    caseSensitiveFiltering: Boolean = true,
    listOfPragmas: List<String>? = null
) {
    private val makeSqliteCaseSensitive = "?case_sensitive_like=true"

    private val handleCaseSensitive = { caseSensitive: Boolean ->
        if (caseSensitive) makeSqliteCaseSensitive else ""
    }
    private val handlePragmas = { pragmas: List<String>? ->
        var params = ""
        pragmas?.onEach { param -> params += "&$param" }
        params
    }

    private val url =
        "jdbc:sqlite:$pathToDb${handleCaseSensitive(caseSensitiveFiltering)}${handlePragmas(listOfPragmas)}"
    private val driver = "org.sqlite.JDBC"
    private val isolationLevel = Connection.TRANSACTION_SERIALIZABLE

    fun initializeConnection() {
        println(url)
        Database.connect(url = url, driver = driver)
        TransactionManager.manager.defaultIsolationLevel = isolationLevel
    }
}