package db.query

import db.DBUtil.setSchema
import db.entity.Schema.entities
import db.entity.Schema.logic
import db.entity.entities.Communication
import db.entity.entities.CommunicationObject
import db.entity.logic.Talks
import db.entity.logic.TalksObject
import io.qameta.allure.Step
import org.jetbrains.exposed.sql.transactions.transaction

object LogicQuery {

    @Step("Ищем разговоры")
    fun getTalks(communication: Communication): List<Talks> {
        var talks: List<Talks>? = null
        transaction {
            setSchema(logic)
            talks = Talks.find { TalksObject.communication_id eq communication.id }.toList()
        }

        return talks!!
    }
}