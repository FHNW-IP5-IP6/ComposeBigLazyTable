package demo.bigLazyTable.newDemo.model

import androidx.compose.ui.unit.dp
import composeForms.model.BaseModel
import composeForms.model.attributes.Attribute
import composeForms.model.attributes.DoubleAttribute
import composeForms.model.attributes.IntegerAttribute
import composeForms.model.attributes.StringAttribute
import demo.bigLazyTable.newDemo.data.database.SuperstoreDatabase
import demo.bigLazyTable.newDemo.data.service.Superstore
import demo.bigLazyTable.spotifyPlaylists.data.database.DatabasePlaylists
import demo.bigLazyTable.spotifyPlaylists.model.BLTLabels

class SuperstoreModel(superstore: Superstore) : BaseModel<SuperstoreLabels>(SuperstoreLabels.TITLE) {

    override val id = IntegerAttribute(
        model = this,
        label = SuperstoreLabels.ROW_ID,
        value = superstore.RowID,
        databaseField = SuperstoreDatabase.RowID
    )

    val Category = StringAttribute(
        model = this,
        label = SuperstoreLabels.CATEGORY,
        value = superstore.Category,
        databaseField = SuperstoreDatabase.Category
    )

    val City = StringAttribute(
        model = this,
        label = SuperstoreLabels.CITY,
        value = superstore.City,
        databaseField = SuperstoreDatabase.City
    )

    val Country = StringAttribute(
        model = this,
        label = SuperstoreLabels.COUNTRY,
        value = superstore.Country,
        databaseField = SuperstoreDatabase.Country
    )

    val CustomerID = StringAttribute(
        model = this,
        label = SuperstoreLabels.CUSTOMER_ID,
        value = superstore.CustomerID,
        databaseField = SuperstoreDatabase.CustomerID
    )

    val CustomerName = StringAttribute(
        model = this,
        label = SuperstoreLabels.CUSTOMER_NAME,
        value = superstore.CustomerName,
        databaseField = SuperstoreDatabase.CustomerName
    )

    val Discount = IntegerAttribute(
        model = this,
        label = SuperstoreLabels.DISCOUNT,
        value = superstore.Discount,
        databaseField = SuperstoreDatabase.Discount
    )

    val OrderDate = StringAttribute(
        model = this,
        label = SuperstoreLabels.ORDER_DATE,
        value = superstore.OrderDate,
        databaseField = SuperstoreDatabase.OrderDate
    )

    val ProductID = StringAttribute(
        model = this,
        label = SuperstoreLabels.PRODUCT_ID,
        value = superstore.ProductID,
        databaseField = SuperstoreDatabase.ProductID
    )

    val Profit = DoubleAttribute(
        model = this,
        label = SuperstoreLabels.PROFIT,
        value = superstore.Profit,
        databaseField = SuperstoreDatabase.Profit
    )

//    val Quantity = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val Region = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val Sales = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val ProductName = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val Segment = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val ShipDate = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val ShipMode = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val State = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID
//    )
//
//    val SubCategory = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID,
//        tableColumnWidth = 200.dp
//    )
//
//    val OrderID = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.ROW_ID,
//        value = superstore.RowID,
//        databaseField = SuperstoreDatabase.RowID,
//    )
//
//    val PostalCode = IntegerAttribute(
//        model = this,
//        label = SuperstoreLabels.POSTAL_CODE,
//        value = superstore.PostalCode,
//        databaseField = SuperstoreDatabase.PostalCode,
//    )

    override val displayedAttributesInTable = listOf(
        id,
        Category,
        City,
        Country,
        CustomerID,
        CustomerName,
        Discount,
        OrderDate,
//        OrderID,
//        PostalCode,
//        ProductID,
//        Profit,
//        Quantity,
//        Region,
//        Sales,
//        ProductName,
//        Segment,
//        ShipDate,
//        ShipMode,
//        State,
//        SubCategory
    )
}