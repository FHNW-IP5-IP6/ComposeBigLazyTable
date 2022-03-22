package bigLazyTable.view.table.header

import bigLazyTable.controller.LazyTableController
import bigLazyTable.data.paging.*
import composeForms.model.attributes.*

fun createFilter(
    controller: LazyTableController<*>,
    attribute: Attribute<*, *, *>,
    value: String,
    filterType: FilterOperation,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    when (attribute) {
        is ShortAttribute -> createShortFilter(
            controller = controller,
            attribute = attribute,
            value = value,
            filterType = filterType,
            isBetween = isBetween,
            from = from,
            to = to
        )
        is IntegerAttribute -> createIntFilter(
            controller = controller,
            attribute = attribute,
            value = value,
            filterType = filterType,
            isBetween = isBetween,
            from = from,
            to = to
        )
        is LongAttribute -> createLongFilter(
            controller = controller,
            attribute = attribute,
            value = value,
            filterType = filterType,
            isBetween = isBetween,
            from = from,
            to = to
        )
        is FloatAttribute -> createFloatFilter(
            controller = controller,
            attribute = attribute,
            value = value,
            filterType = filterType,
            isBetween = isBetween,
            from = from,
            to = to
        )
        is DoubleAttribute -> createDoubleFilter(
            controller = controller,
            attribute = attribute,
            value = value,
            filterType = filterType,
            isBetween = isBetween,
            from = from,
            to = to
        )
    }
}

fun createShortFilter(
    controller: LazyTableController<*>,
    attribute: ShortAttribute<*>,
    value: String,
    filterType: FilterOperation,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    controller.attributeFilter[attribute] = ShortFilter(
        filter = if (isBetween) 0 else value.toShort(),
        dbField = attribute.databaseField!!,
        filterOperation = filterType,
        between = if (isBetween) {
            Between(
                fromFilter = ShortFilter(
                    filter = from.toShort(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                ),
                toFilter = ShortFilter(
                    filter = to.toShort(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                )
            )
        } else null
    )
}

fun createIntFilter(
    controller: LazyTableController<*>,
    attribute: IntegerAttribute<*>,
    value: String,
    filterType: FilterOperation,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    controller.attributeFilter[attribute] = IntFilter(
        filter = if (isBetween) 0 else value.toInt(),
        dbField = attribute.databaseField!!,
        filterOperation = filterType,
        between = if (isBetween) {
            Between(
                fromFilter = IntFilter(
                    filter = from.toInt(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                ),
                toFilter = IntFilter(
                    filter = to.toInt(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                )
            )
        } else null
    )
}

fun createLongFilter(
    controller: LazyTableController<*>,
    attribute: LongAttribute<*>,
    value: String,
    filterType: FilterOperation,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    controller.attributeFilter[attribute] = LongFilter(
        filter = if (isBetween) 0 else value.toLong(),
        dbField = attribute.databaseField!!,
        filterOperation = filterType,
        between = if (isBetween) {
            Between(
                fromFilter = LongFilter(
                    filter = from.toLong(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                ),
                toFilter = LongFilter(
                    filter = to.toLong(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                )
            )
        } else null
    )
}

fun createFloatFilter(
    controller: LazyTableController<*>,
    attribute: FloatAttribute<*>,
    value: String,
    filterType: FilterOperation,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    controller.attributeFilter[attribute] = FloatFilter(
        filter = if (isBetween) 0f else value.toFloat(),
        dbField = attribute.databaseField!!,
        filterOperation = filterType,
        between = if (isBetween) {
            Between(
                fromFilter = FloatFilter(
                    filter = from.toFloat(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                ),
                toFilter = FloatFilter(
                    filter = to.toFloat(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                )
            )
        } else null
    )
}

fun createDoubleFilter(
    controller: LazyTableController<*>,
    attribute: DoubleAttribute<*>,
    value: String,
    filterType: FilterOperation,
    isBetween: Boolean = false,
    from: String = "",
    to: String = ""
) {
    controller.attributeFilter[attribute] = DoubleFilter(
        filter = if (isBetween) 0.0 else value.toDouble(),
        dbField = attribute.databaseField!!,
        filterOperation = filterType,
        between = if (isBetween) {
            Between(
                fromFilter = DoubleFilter(
                    filter = from.toDouble(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                ),
                toFilter = DoubleFilter(
                    filter = to.toDouble(),
                    dbField = attribute.databaseField!!,
                    filterOperation = filterType
                )
            )
        } else null
    )
}