package demo.bigLazyTable.model

import model.attributes.Attribute
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Marco Sprenger, Livio Näf
 */
/*
class AttributeStore<T> {

    val store: MutableMap<EntityKey, MutableMap<String, List<Attribute<*, *, *>>>> = ConcurrentHashMap()

    private fun sameAttributes(attribute: Attribute<*,*,*>): List<Attribute<*, *, *>> {
//        val x = store.values[attribute.getLabel()]
        TODO("Check if string is contained in store and return List<Attribute<*,*,*>>")
    }

    fun purge(attributes: List<Attribute<*, *, *>>) {
        synchronized (store) {
            attributes.forEach { attribute ->
                if (attribute.isChanged()) {
                    attribute.setDisposed(true)
                }
                else {
                    sameAttributes(attribute).remove(attribute)
                }
            }
        }
    }

    @Synchronized
    fun syncEqualAttributes(attribute: Attribute<*, *, *>): T {
        synchronized (store) {
            sameAttributes(attribute).forEach { a ->
                if (a != attribute) a.setValue(attribute.getValue())
            }
        }
    }

    fun store(attributes: List<Attribute<*, *, *>>) {
        attributes.forEach { TODO("Code is cut on video :( -> attributes.forEach(attribute...") }
    }

    fun remove(attributes: List<Attribute<*, *, *>>) {
        synchronized (store) {
            attributes.forEach { attribute ->
                sameAttributes(attribute).remove(attribute)
            }
        }
    }



}*/
