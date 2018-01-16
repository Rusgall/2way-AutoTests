package db.entity.entities

import db.entity.JsonColumnType
import db.entity.external.Clients
import db.entity.external.ClientsObject
import db.entity.external.Users
import db.entity.external.UsersObject
import entity.entities.JsonCommunicationTemplate
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object CommunicationTemplatesObject : IntIdTable("communication_templates") {
    val client = reference("client_id", ClientsObject)
    val name = text("name")
    val schema = registerColumn<JsonCommunicationTemplate>("params", JsonColumnType(JsonCommunicationTemplate::class.java))
    val created_at = datetime("created_at")
    val last_changed_at = datetime("last_changed_at")
    val owner_user = reference("owner_user_id", UsersObject)
}

class CommunicationTemplates(id:EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<CommunicationTemplates>(CommunicationTemplatesObject)

    var client by Clients referencedOn  CommunicationTemplatesObject.client
    val name by CommunicationTemplatesObject.name
    val schema by CommunicationTemplatesObject.schema
    val created_at by CommunicationTemplatesObject.created_at
    val last_changed_at by CommunicationTemplatesObject.last_changed_at
    val owner_user by Users referencedOn CommunicationTemplatesObject.owner_user
}