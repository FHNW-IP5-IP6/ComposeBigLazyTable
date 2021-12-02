package demo.bigLazyTable.model

import java.util.*

/**
 * @author Marco Sprenger, Livio Näf
 */
class EntityKey(private val type: String, private val id: Long) {
    fun getType() = type

    fun getId() = id

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || this.javaClass != other.javaClass) {
            return false
        }
        val entityKey = other as EntityKey
        return id == entityKey.id && Objects.equals(type, entityKey.type)
    }

    override fun hashCode(): Int = Objects.hash(type, id)

    override fun toString(): String = "Entity{type='$type\', id=$id}"
}
