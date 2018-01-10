package db.entity

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import entity.JsonEntity
import org.jetbrains.exposed.sql.ColumnType
import org.postgresql.util.PGobject
import utils.JsonUtils
import java.sql.PreparedStatement
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass

class JsonColumnType(val gsonClass: Class<*>) : ColumnType() {
    private val gson = Gson()

    override fun sqlType() = "jsonb"

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        val json = JsonUtils.convertToJSON(value, gsonClass)
        val obj = PGobject()
        obj.type = "jsonb"
        obj.value = json
        stmt.setObject(index, obj)
    }

    override fun valueFromDB(value: Any): Any {
        if (value is PGobject) {
            return gson.fromJson(value.value, gsonClass)
        } else if (value is JsonEntity) {
            return value
        }
        throw RuntimeException("Can't parse object: $value")
    }
}