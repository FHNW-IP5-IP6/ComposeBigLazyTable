package demo.bigLazyTable.newDemo.model

import androidx.compose.ui.unit.dp
import composeForms.model.BaseModel
import composeForms.model.attributes.Attribute
import composeForms.model.attributes.DoubleAttribute
import composeForms.model.attributes.IntegerAttribute
import composeForms.model.attributes.StringAttribute
import composeForms.model.modelElements.Field
import composeForms.model.modelElements.FieldSize
import composeForms.model.modelElements.Group
import composeForms.model.modelElements.HeaderGroup
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

    private val headerGroup = HeaderGroup(
        model = this,
        title = SuperstoreLabels.HEADER_GROUP,
        Field(id, FieldSize.SMALL),
        Field(City, FieldSize.NORMAL)
    )

    private val superstoreInfoGroup = Group(
        model = this,
        title = SuperstoreLabels.SUPERSTORE_INFO_GROUP,
        Field(City, FieldSize.NORMAL),
        Field(Country, FieldSize.SMALL),
        Field(Category, FieldSize.SMALL),
        Field(CustomerID, FieldSize.SMALL),
        Field(CustomerName, FieldSize.SMALL),
        Field(Profit, FieldSize.SMALL),
        Field(Discount, FieldSize.SMALL),
    )

//    private val track0Group = Group(
//        model = this,
//        title = BLTLabels.TRACK0_GROUP,
//        Field(track0TrackName, FieldSize.BIG),
//        Field(track0ArtistName, FieldSize.NORMAL),
//        Field(track0AlbumName, FieldSize.NORMAL),
//        Field(track0DurationMs, FieldSize.SMALL)
//    )
//
//    private val track1Group = Group(
//        model = this,
//        title = BLTLabels.TRACK1_GROUP,
//        Field(track1TrackName, FieldSize.BIG),
//        Field(track1ArtistName, FieldSize.NORMAL),
//        Field(track1AlbumName, FieldSize.NORMAL),
//        Field(track1DurationMs, FieldSize.SMALL)
//    )
//
//    private val track2Group = Group(
//        model = this,
//        title = BLTLabels.TRACK2_GROUP,
//        Field(track2TrackName, FieldSize.BIG),
//        Field(track2ArtistName, FieldSize.NORMAL),
//        Field(track2AlbumName, FieldSize.NORMAL),
//        Field(track2DurationMs, FieldSize.SMALL)
//    )
//
//    private val track3Group = Group(
//        model = this,
//        title = BLTLabels.TRACK3_GROUP,
//        Field(track3TrackName, FieldSize.BIG),
//        Field(track3ArtistName, FieldSize.NORMAL),
//        Field(track3AlbumName, FieldSize.NORMAL),
//        Field(track3DurationMs, FieldSize.SMALL)
//    )
//
//    private val track4Group = Group(
//        model = this,
//        title = BLTLabels.TRACK4_GROUP,
//        Field(track4TrackName, FieldSize.BIG),
//        Field(track4ArtistName, FieldSize.NORMAL),
//        Field(track4AlbumName, FieldSize.NORMAL),
//        Field(track4DurationMs, FieldSize.SMALL)
//    )
}