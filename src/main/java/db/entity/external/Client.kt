package db.entity.external

import db.entity.JsonColumnType
import entity.external.JsonClient
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object ClientsObject : IntIdTable("Clients") {
    val name = text("name")
    val params  = registerColumn<JsonClient>("params", JsonColumnType(JsonClient::class.java))
}
class Clients(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Clients>(ClientsObject)

    var name by ClientsObject.name
    var params by ClientsObject.params

    override fun toString(): String {
        return "id: $id, name: $name, params:$params"
    }
}