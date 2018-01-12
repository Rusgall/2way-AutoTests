package entity.external

import entity.JsonEntity
import utils.JsonUtils

class JsonUser() : JsonEntity() {

    override fun toString(): String {
        return JsonUtils.convertToJSON(this, this::class.java)
    }
}