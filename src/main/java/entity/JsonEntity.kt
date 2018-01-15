package entity

import utils.JsonUtils

abstract class JsonEntity {

    override fun toString(): String {
        return JsonUtils.convertToJSON(this, this::class.java)
    }
}