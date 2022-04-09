package demo.bigLazyTable.newDemo.model

import composeForms.model.ILabel

enum class SuperstoreLabels(val deutsch: String, val english: String) : ILabel {
    TITLE("Superstore Daten","Superstore data"),
    HEADER_GROUP("Superstore Übersicht", "Superstore Overview"),

    SUPERSTORE_INFO_GROUP("Superstore Informationen", "Superstore Informations"),

    CATEGORY("Kategorie","Category"),
    CITY("Stadt","City"),
    COUNTRY("Land","Country"),
    CUSTOMER_ID("Kunden ID", "Customer ID"),
    CUSTOMER_NAME("Kunden Name", "Customer Name"),
    DISCOUNT("Rabatt", "Discount"),
    ORDER_DATE("Bestelldatum", "Order Date"),
    ORDER_ID("Bestell ID", "Order ID"),
    POSTAL_CODE("Postleitzahl", "Postal Code"),
    PRODUCT_ID("Produkt ID", "Product ID"),
    PRODUCT_NAME("Produkt Name", "Product Name"),
    PROFIT("Profit", "Profit"),
    QUANTITY("Quantität", "Quantity"),
    REGION("Region", "Region"),
    ROW_ID("ID", "ID"),
    SALES("Umsatz", "Sales"),
    SEGMENT("Segment", "Segment"),
    SHIP_DATE("Versanddatum", "Ship Date"),
    SHIP_MODE("Versandmodus", "Ship Mode"),
    STATE("Status", "State"),
    SUB_CATEGORY("Unterkategorie", "Sub Category")
}