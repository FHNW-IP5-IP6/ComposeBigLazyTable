package demo.bigLazyTable.ui.table.header

import bigLazyTable.paging.NumberFilterType
import composeForms.model.attributes.Attribute
import demo.bigLazyTable.model.LazyTableController

object NumberTextFieldUtil {

    // TODO: Future improvement -> Add a FilterAttribute
    // TODO: Move to controller
    fun createConcreteNumberFilter(
        newValue: String,
        controller: LazyTableController,
        attribute: Attribute<*, *, *>
    ) {
        try {
            val allowedNonNumberChars = listOf('=', '!', '>', '<', '[', ',', ']')
            val newRestrictedValue =
                newValue.filter { it.isDigit() || allowedNonNumberChars.contains(it) }
            controller.displayedFilterStrings[attribute] = newRestrictedValue
            println("newNumberValue: $newRestrictedValue")
            if (newRestrictedValue.length > 1) {
                when (newRestrictedValue[0]) {
                    '!' -> {
                        if (newRestrictedValue[1] == '=') {
                            if (newRestrictedValue.length > 2) {
                                val value = newRestrictedValue.substring(2).trim()
                                if (value != "") {
                                    createFilter(
                                        controller = controller,
                                        attribute = attribute,
                                        value = value,
                                        filterType = NumberFilterType.NOT_EQUALS
                                    )
                                }
                            }
                        }
                    }
                    '=' -> {
                        if (newRestrictedValue[1] == '!') {
                            if (newRestrictedValue.length > 2) {
                                val value = newRestrictedValue.substring(2).trim()
                                if (value != "") {
                                    createFilter(
                                        controller = controller,
                                        attribute = attribute,
                                        value = value,
                                        filterType = NumberFilterType.NOT_EQUALS
                                    )
                                }
                            }
                        } else if (newRestrictedValue[1].isDigit()) {
                            val value = newRestrictedValue.substring(1).trim()
                            if (value != "") {
                                createFilter(
                                    controller = controller,
                                    attribute = attribute,
                                    value = value,
                                    filterType = NumberFilterType.EQUALS
                                )
                            }
                        }
                    }
                    '>' -> {
                        when {
                            newRestrictedValue[1] == '=' -> {
                                if (newRestrictedValue.length > 2) {
                                    val value = newRestrictedValue.substring(2).trim()
                                    if (value != "") {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = value,
                                            filterType = NumberFilterType.GREATER_EQUALS
                                        )
                                    }
                                }
                            }
                            newRestrictedValue[1].isDigit() -> {
                                val value = newRestrictedValue.substring(1).trim()
                                if (value != "") {
                                    createFilter(
                                        controller = controller,
                                        attribute = attribute,
                                        value = value,
                                        filterType = NumberFilterType.GREATER
                                    )
                                }
                            }
                        }
                    }
                    '<' -> {
                        when {
                            newRestrictedValue[1] == '=' -> {
                                if (newRestrictedValue.length > 2) {
                                    val value = newRestrictedValue.substring(2).trim()
                                    if (value != "") {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = value,
                                            filterType = NumberFilterType.LESS_EQUALS
                                        )
                                    }
                                }
                            }
                            newRestrictedValue[1].isDigit() -> {
                                val value = newRestrictedValue.substring(1).trim()
                                if (value != "") {
                                    createFilter(
                                        controller = controller,
                                        attribute = attribute,
                                        value = value,
                                        filterType = NumberFilterType.LESS
                                    )
                                }
                            }
                        }
                    }
                    '[' -> {
                        val lastChar = newRestrictedValue.trim().last()
                        if (newRestrictedValue.contains(',') && ((lastChar == ']') || lastChar == '[')) {
                            val from = newRestrictedValue.substringAfter('[').substringBefore(',').trim()
                            if (from.isNotBlank()) {
                                println("from $from")

                                val to = newRestrictedValue.substringAfter(',').substringBefore(lastChar).trim()
                                if (to.isNotBlank()) {
                                    println("to $to")
                                    if (lastChar == ']') {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = "",
                                            filterType = NumberFilterType.BETWEEN_BOTH_INCLUDED,
                                            isBetween = true,
                                            from = from,
                                            to = to
                                        )
                                    } else {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = "",
                                            filterType = NumberFilterType.BETWEEN_FROM_INCLUDED,
                                            isBetween = true,
                                            from = from,
                                            to = to
                                        )
                                    }
                                }
                            }
                        }
                    }
                    ']' -> {
                        val lastChar = newRestrictedValue.trim().last()
                        if (newRestrictedValue.contains(',') && ((lastChar == ']') || lastChar == '[')) {
                            val from = newRestrictedValue.substringAfter(']').substringBefore(',').trim()
                            if (from.isNotBlank()) {
                                println("from $from")

                                val to = newRestrictedValue.substringAfter(',').substringBefore(lastChar).trim()
                                if (to.isNotBlank()) {
                                    println("to $to")
                                    if (lastChar == ']') {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = "",
                                            filterType = NumberFilterType.BETWEEN_TO_INCLUDED,
                                            isBetween = true,
                                            from = from,
                                            to = to
                                        )
                                    } else {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = "",
                                            filterType = NumberFilterType.BETWEEN_BOTH_NOT_INCLUDED,
                                            isBetween = true,
                                            from = from,
                                            to = to
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // TODO: Give User hint what went wrong & how to do it right
            // for now we just ignore any occurring exception
        }
    }

}