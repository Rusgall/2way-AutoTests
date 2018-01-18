package db.entity

import entity.JsonEntity
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.jdbc.PgArray
import org.postgresql.util.PGobject
import utils.JsonUtils
import java.sql.PreparedStatement

class ArrayColumnType : ColumnType(){
    override fun sqlType() = "integer[]"

    override fun setParameter(stmt: PreparedStatement, index: Int, value: Any?) {
        var arraySql : java.sql.Array? = null
        transaction {
            arraySql = connection.createArrayOf(sqlType(), value as Array<Int>)
        }
        stmt.setArray(index, arraySql)
    }

    override fun valueFromDB(value: Any): Any {
        if (value is PgArray) {
            return value.array
        }
        throw RuntimeException("Can't parse object: $value")
    }
}