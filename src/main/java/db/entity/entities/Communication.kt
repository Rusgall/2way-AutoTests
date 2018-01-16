package db.entity.entities

import db.entity.JsonColumnType
import db.entity.external.Clients
import db.entity.external.ClientsObject
import db.entity.external.Users
import db.entity.external.UsersObject
import entity.entities.JsonCommunication
import entity.entities.communication_status_type
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object CommunicationObject : IntIdTable("communications"){
    val client = reference("client_id", ClientsObject)
    val communication_template = reference("communication_template_id", CommunicationTemplatesObject)
    val abonents_list = long("abonents_list_id")
    val params = registerColumn<JsonCommunication>("params", JsonColumnType(JsonCommunication::class.java))
    val status = enumerationByName("status", 10, communication_status_type::class.java)
    val created_at = datetime("created_at")
    val last_changed_at = datetime("last_changed_at")
    val name = text("name")
    val owner_user = reference("owner_user_id", UsersObject)
}

class Communication(id:EntityID<Int>):IntEntity(id){
    companion object : IntEntityClass<Communication>(CommunicationObject)
    val client by Clients referencedOn CommunicationObject.client
    val communication_template by CommunicationTemplates referencedOn CommunicationObject.communication_template
    val abonents_list by CommunicationObject.abonents_list
    val params by CommunicationObject.params
    val status by CommunicationObject.status
    val created_at by CommunicationObject.created_at
    val last_changed_at by CommunicationObject.last_changed_at
    val name by CommunicationObject.name
    val owner_user by Users referencedOn CommunicationObject.owner_user
}