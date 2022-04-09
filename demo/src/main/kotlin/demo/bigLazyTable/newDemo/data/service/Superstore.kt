package demo.bigLazyTable.newDemo.data.service

import demo.bigLazyTable.newDemo.data.database.SuperstoreDatabase
import org.jetbrains.exposed.sql.ResultRow

data class Superstore(
    val Category: String = "",
    val City: String = "",
    val Country: String = "",
    val CustomerID: String = "",
    val CustomerName: String = "",
    val Discount: Int = 0,
    val OrderDate: String = "",
    val OrderID: String = "",
    val PostalCode: Int = 0,
    val ProductID: String = "",
    val Profit: Double = 0.0,
    val Quantity: Int = 0,
    val Region: String = "",
    val RowID: Int = 0,
    val Sales: Double = 0.0,
    val ProductName: String = "",
    val Segment: String = "",
    val ShipDate: String = "",
    val ShipMode: String = "",
    val State: String = "",
//    val SubCategory: String = ""
)

data class SuperstoreDto(val resultRow: ResultRow) {
    fun toSuperstore(): Superstore = resultRow.let {
        Superstore(
            Category = it[SuperstoreDatabase.Category],
            City = it[SuperstoreDatabase.City],
            Country = it[SuperstoreDatabase.Country],
            CustomerID = it[SuperstoreDatabase.CustomerID],
            CustomerName = it[SuperstoreDatabase.CustomerName],
            Discount = it[SuperstoreDatabase.Discount],
            OrderDate = it[SuperstoreDatabase.OrderDate],
            OrderID = it[SuperstoreDatabase.OrderID],
            PostalCode = it[SuperstoreDatabase.PostalCode],
            ProductID = it[SuperstoreDatabase.ProductID],
            ProductName = it[SuperstoreDatabase.ProductName],
            Profit = it[SuperstoreDatabase.Profit],
            Quantity = it[SuperstoreDatabase.Quantity],
            Region = it[SuperstoreDatabase.Region],
            RowID = it[SuperstoreDatabase.RowID],
            Sales = it[SuperstoreDatabase.Sales],
            Segment = it[SuperstoreDatabase.Segment],
            ShipDate = it[SuperstoreDatabase.ShipDate],
            ShipMode = it[SuperstoreDatabase.ShipMode],
            State = it[SuperstoreDatabase.State],
//            SubCategory = it[SuperstoreDatabase.SubCategory],
        )
    }
}