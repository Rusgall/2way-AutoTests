package db.entity.entities

import db.entity.JsonColumnType
import db.entity.external.Clients
import db.entity.external.ClientsObject
import db.entity.external.Users
import db.entity.external.UsersObject
import entity.entities.JsonAbonentsLists
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object AbonentsListsObject : IntIdTable("abonents_lists") {
    val client = reference("client_id", ClientsObject)
    val name = text("name")
    val created_at = datetime("created_at")
    val last_changed_at = datetime("last_changed_at")
    val deleted = bool("deleted")
    val hidden = bool("hidden")
    val params = registerColumn<JsonAbonentsLists>("params", JsonColumnType(JsonAbonentsLists::class.java))
    val owner_user = reference("owner_user_id", UsersObject)
}

class AbonentsLists(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AbonentsLists>(AbonentsListsObject)

    var client by Clients referencedOn  AbonentsListsObject.client
    var name by AbonentsListsObject.name
    var created_at by AbonentsListsObject.created_at
    var last_changed_at by AbonentsListsObject.last_changed_at
    var deleted by AbonentsListsObject.deleted
    var hidden by AbonentsListsObject.hidden
    var params by AbonentsListsObject.params
    var owner_user by Users referencedOn AbonentsListsObject.owner_user
}