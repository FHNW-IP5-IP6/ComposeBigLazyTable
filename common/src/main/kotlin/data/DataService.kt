package data

interface DataService {

    fun requestAllData(dataSource: String): List<Map<String, Any>>
}