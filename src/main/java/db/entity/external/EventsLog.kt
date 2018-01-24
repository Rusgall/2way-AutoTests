package db.entity.external

import db.entity.JsonColumnType
import entity.external.JsonEventsLog
import entity.external.operations
import entity.external.entities
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object EventsLogObject : IntIdTable("events_log"){
    val time_stamp = datetime("time_stamp")
    val user_id = reference("user_id", UsersObject)
    val ip_address = text("ip_address")
    val user_agent = text("user_agent")
    val operation = enumerationByName("operation", 15, operations::class.java)
    val entity = enumerationByName("entity", 15, entities::class.java)
    val entity_id = long("entity_id")
    val description = registerColumn<JsonEventsLog>("description", JsonColumnType(JsonEventsLog::class.java))
}

class EventsLog(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventsLog>(EventsLogObject)

    var time_stamp by EventsLogObject.time_stamp
    var user_id by Users referencedOn EventsLogObject.user_id
    var ip_address by EventsLogObject.ip_address
    var user_agent by EventsLogObject.user_agent
    var operation by EventsLogObject.operation
    var entity by EventsLogObject.entity
    var entity_id by EventsLogObject.entity_id
    var description by EventsLogObject.description
}