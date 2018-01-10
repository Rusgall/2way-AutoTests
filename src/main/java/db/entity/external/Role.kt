package db.entity.external

import db.entity.JsonColumnType
import entity.external.JsonRole
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object RoleObject : IntIdTable("roles") {
    val name = text("name")
    val description = text("description")
    val super_role = bool("super_role")
    val params  = registerColumn<JsonRole>("params", JsonColumnType(JsonRole::class.java))
}

class Roles(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Roles>(RoleObject)

    var name by RoleObject.name
    var description by RoleObject.description
    var super_role by RoleObject.super_role
    var params by RoleObject.params
}