package db.entity.entities

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object CommunicationAbonentsListsObject : IntIdTable("communication_abonents_lists"){
    val communication = reference("communication_id", CommunicationObject)
    val abonentsList = reference("abonents_list_id", AbonentsListsObject)
}

class CommunicationAbonentsLists(id : EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<CommunicationAbonentsLists>(CommunicationAbonentsListsObject)

    val communication by Communication referencedOn CommunicationAbonentsListsObject.communication
    val abonentsList by AbonentsLists referencedOn CommunicationAbonentsListsObject.abonentsList

}