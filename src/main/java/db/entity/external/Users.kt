package db.entity.external

import db.entity.JsonColumnType
import entity.external.JsonUser
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object UsersObject : IntIdTable("users") {
    val name = text("name")
    val login = text("login")
    val role_id_fk = reference("role_id_fk", RoleObject)
    val client_id = reference("client_id", ClientsObject)
    val created_at = datetime("created_at")
    val encrypted_password = varchar("encrypted_password",100)
    val blocked = bool("blocked")
    val superuser = bool("superuser")
    val params  = registerColumn<JsonUser>("params", JsonColumnType(JsonUser::class.java))
    val deleted = bool("deleted")
    val email = text("email")
}

class Users(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Users>(UsersObject)

    var name by UsersObject.name
    var login by UsersObject.login
    var role_id_fk by Roles referencedOn UsersObject.role_id_fk
    var client_id by Clients referencedOn UsersObject.client_id
    var created_at by UsersObject.created_at
    var encrypted_password by UsersObject.encrypted_password
    var blocked by UsersObject.blocked
    var superuser by UsersObject.superuser
    var params by UsersObject.params
    var deleted by UsersObject.deleted
    var email by UsersObject.email

    override fun equals(other: Any?): Boolean {
        if (other == this) return true
        if(other is Users){
            return other.name == name && other.login == login && other.role_id_fk.id.value == role_id_fk.id.value
                    && other.client_id.id.value == client_id.id.value && other.encrypted_password == encrypted_password
                    && other.blocked == blocked && other.superuser == superuser && other.params == params
                    && other.deleted == deleted && other.email == email
        } else {
            return false
        }

    }

}