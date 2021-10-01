package service

interface DataService {

    fun requestAllData(dataSource: String): List<Map<String, Any>>
}