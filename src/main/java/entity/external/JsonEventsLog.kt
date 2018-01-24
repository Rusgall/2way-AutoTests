package entity.external

import entity.JsonEntity

class JsonEventsLog : JsonEntity() {
    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other is JsonEventsLog) return true
        return false
    }

    override fun hashCode(): Int {
        return 0
    }
}