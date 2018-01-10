package utils

import com.google.gson.Gson

object JsonUtils {
    private val gson = Gson()

    fun convertToJSON(value: Any?, gsonClass: Class<*>): String{
        return gson.toJson(value, gsonClass)
    }
}