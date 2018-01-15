package entity.entities

import entity.JsonEntity

class JsonAbonents : JsonEntity() {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other is JsonAbonents) return true
        return false
    }
}