package db.entity.entities

import db.entity.JsonColumnType
import entity.entities.JsonAbonents
import entity.entities.abonent_source_type
import entity.entities.abonent_state
import org.jetbrains.exposed.dao.*

object AbonentsObject : IntIdTable("abonents"){
    val abonents_list_id = reference("abonents_list_id", AbonentsListsObject)
    val msisdn = long("msisdn")
    val params = registerColumn<JsonAbonents>("params", JsonColumnType(JsonAbonents::class.java))
    val time_zone = datetime("time_zone")
    val abonent_source = enumerationByName("abonent_source", 10, abonent_source_type::class.java)
    val state = enumerationByName("state", 10, abonent_state::class.java)
}

class Abonents(id: EntityID<Int>): IntEntity(id){
    companion object : IntEntityClass<Abonents>(AbonentsObject)

    var abonents_list_id by AbonentsLists referencedOn  AbonentsObject.abonents_list_id
    var msisdn by AbonentsObject.msisdn
    var params by AbonentsObject.params
    var time_zone by AbonentsObject.time_zone
    var abonent_source by AbonentsObject.abonent_source
    var state by AbonentsObject.state
}