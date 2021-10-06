package data

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

class DataServiceImpl : DataService {

    override fun requestAllData(dataSource: String): List<Map<String, Any>> {
        // see https://github.com/doyaaaaaken/kotlin-csv

        // read from `String`
        val csvData: String = "a,b,c\nd,e,f"
        val rows: List<List<String>> = csvReader().readAll(csvData)

        // read from `java.io.File`
        val file: File = File("test.csv")
        val rowsFromFile: List<List<String>> = csvReader().readAll(file)

        val rowsWithHeader: List<Map<String, String>> = csvReader().readAllWithHeader(csvData)
        println(rows) //[{a=d, b=e, c=f}]

        csvReader().open("test2.csv") {
            readAllWithHeaderAsSequence().forEach { row: Map<String, String> ->
                //Do something
                println(row) //{id=1, name=doyaaaaaken}
            }
        }

        // TODO: async example
//        csvReader().openAsync("test.csv") {
//            val container = mutableListOf<List<String>>()
//            delay(100) //other suspending task
//            readAllAsSequence().asFlow().collect { row ->
//                delay(100) // other suspending task
//                container.add(row)
//            }
//        }

        return rowsWithHeader
    }

}