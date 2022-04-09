package demo.bigLazyTable.newDemo.data.database

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

// Must be the same name as the SQLite Table Name!
object SuperstoreDatabase: Table() {
    val Category: Column<String> = varchar("Category", length = 100)
    val City: Column<String> = varchar("City", length = 100)
    val Country: Column<String> = varchar("Country", length = 100)
    val CustomerID: Column<String> = varchar("CustomerID", length = 100)
    val CustomerName: Column<String> = varchar("CustomerName", length = 100)
    val Discount: Column<Int> = integer("Discount")
    val OrderDate: Column<String> = varchar("OrderDate", length = 100)
    val OrderID: Column<String> = varchar("OrderID", length = 100)
    val PostalCode: Column<Int> = integer("PostalCode")
    val ProductID: Column<String> = varchar("ProductID", length = 100)
    val ProductName: Column<String> = varchar("ProductName", length = 100)
    val Profit: Column<Double> = double("Profit")
    val Quantity: Column<Int> = integer("Quantity")
    val Region: Column<String> = varchar("Region", length = 100)
    val RowID: Column<Int> = integer("RowID")
    val Sales: Column<Double> = double("Sales")
    val Segment: Column<String> = varchar("Segment", length = 100)
    val ShipDate: Column<String> = varchar("ShipDate", length = 100)
    val ShipMode: Column<String> = varchar("ShipMode", length = 100)
    val State: Column<String> = varchar("State", length = 100)
//    val SubCategory: Column<String> = varchar("SubCategory", length = 100)

    override val primaryKey = PrimaryKey(SuperstoreDatabase.RowID, name = "PK_SuperstoreDatabase_ID")
}