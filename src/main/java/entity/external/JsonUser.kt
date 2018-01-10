package entity.external

import entity.JsonEntity
import utils.JsonUtils

class JsonUser(var id: String, var name: String) : JsonEntity() {

    override fun toString(): String {
        return JsonUtils.convertToJSON(this, this::class.java)
    }
}