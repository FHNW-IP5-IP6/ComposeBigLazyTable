package bigLazyTable.view.table.header

import bigLazyTable.controller.LazyTableController
import bigLazyTable.data.paging.FilterOperation
import composeForms.model.attributes.Attribute

/**
 * @author Marco Sprenger, Livio Näf
 */
object NumberTextFieldUtil {

    // TODO-Future: Add a FilterAttribute
    fun createConcreteNumberFilter(
        newValue: String,
        controller: LazyTableController<*>,
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
                                        filterType = FilterOperation.NOT_EQUALS
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
                                        filterType = FilterOperation.NOT_EQUALS
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
                                    filterType = FilterOperation.EQUALS
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
                                            filterType = FilterOperation.GREATER_EQUALS
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
                                        filterType = FilterOperation.GREATER
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
                                            filterType = FilterOperation.LESS_EQUALS
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
                                        filterType = FilterOperation.LESS
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
                                            filterType = FilterOperation.BETWEEN_BOTH_INCLUDED,
                                            isBetween = true,
                                            from = from,
                                            to = to
                                        )
                                    } else {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = "",
                                            filterType = FilterOperation.BETWEEN_FROM_INCLUDED,
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
                                            filterType = FilterOperation.BETWEEN_TO_INCLUDED,
                                            isBetween = true,
                                            from = from,
                                            to = to
                                        )
                                    } else {
                                        createFilter(
                                            controller = controller,
                                            attribute = attribute,
                                            value = "",
                                            filterType = FilterOperation.BETWEEN_BOTH_NOT_INCLUDED,
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
            // TODO-Future: Give User hint what went wrong & how to do it right
            // for now we just ignore any occurring exception
        }
    }

}