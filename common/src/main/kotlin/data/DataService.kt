package data

interface DataService {

    fun requestAllData(dataSource: String): List<Map<String, Any>>

    fun fetchData(dataSource: Any, pageSize: Int, data: List<Any>)
}