package db.entity.logic

import db.entity.ArrayColumnType
import db.entity.JsonColumnType
import db.entity.entities.Abonents
import db.entity.entities.AbonentsObject
import db.entity.entities.Communication
import db.entity.entities.CommunicationObject
import entity.entities.JsonCommunicationTemplate
import entity.logic.JsonTalks
import entity.logic.init_base_type_type
import entity.logic.msg_type_type
import entity.logic.talk_status
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable

object TalksObject : IntIdTable("Talks"){
    val communication_id = reference("communication_id", CommunicationObject)
    val time_stamp = datetime("time_stamp")
    val abonent_id = reference("abonent_id", AbonentsObject)
    val msisdn = long("msisdn")
    val schema = registerColumn<JsonCommunicationTemplate>("schema", JsonColumnType(JsonCommunicationTemplate::class.java))
    val trace = registerColumn<Array<Int>>("trace", ArrayColumnType())
    val receive_sn = text("receive_sn")
    val init_base_type = enumerationByName("init_base_type", 15, init_base_type_type::class.java)
    val params = registerColumn<JsonTalks>("params", JsonColumnType(JsonTalks::class.java))
    val msg_type = enumerationByName("msg_type", 15, msg_type_type::class.java)
    val status = enumerationByName("status", 30, talk_status::class.java)
    val check_time_stamp = datetime("check_time_stamp")
    val resend_time_stamp = datetime("resend_time_stamp")
    val has_answer = bool("has_answer")
}

class Talks(id: EntityID<Int>) : IntEntity(id){
    companion object : IntEntityClass<Talks>(TalksObject)

    var communication by Communication referencedOn TalksObject.communication_id
    val time_stamp by TalksObject.time_stamp
    val abonent by Abonents referencedOn TalksObject.abonent_id
    val msisdn by TalksObject.msisdn
    val schema by TalksObject.schema
    val trace by TalksObject.trace
    val receive_sn by TalksObject.receive_sn
    val init_base_type by TalksObject.init_base_type
    val params by TalksObject.params
    val msg_type by TalksObject.msg_type
    val status by TalksObject.status
    val check_time_stamp by TalksObject.check_time_stamp
    val resend_time_stamp by TalksObject.resend_time_stamp
    val has_answer by TalksObject.has_answer
}