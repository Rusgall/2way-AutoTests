package db.query

import db.entity.Schema.*
import db.entity.external.Clients
import db.entity.logic.TalksObject
import db.entity.external.Roles
import db.entity.external.Users
import entity.external.JsonUser
import db.DBUtil.setSchema
import db.entity.entities.*
import entity.entities.*
import entity.logic.ResultStartCommunication
import io.qameta.allure.Step
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.util.PGobject
import java.sql.BatchUpdateException
import java.sql.ResultSet

object EntitiesQuery {

    @Step("Чистим абонетов и опросы")
    fun clearDB() {
        transaction {
            setSchema(entities, logic)
            TalksObject.deleteAll()
            CommunicationAbonentsListsObject.deleteAll()
            CommunicationObject.deleteAll()
            CommunicationTemplatesObject.deleteAll()
            AbonentsObject.deleteAll()
            AbonentsListsObject.deleteAll()

        }
    }

    @Step("Вставляем список абонентов")
    fun insertAbonentsList(client: Clients, name: String, deleted: Boolean, hidden: Boolean, params: JsonAbonentsLists, user: Users): AbonentsLists {
        var al: AbonentsLists? = null
        transaction {
            setSchema(entities)

            al = AbonentsLists.new {
                this.client = client
                this.name = name
                this.deleted = deleted
                this.hidden = hidden
                this.params = params
                this.owner_user = user
            }
        }

        al ?: Exception("Abonents list is null!!")
        return al!!
    }

    @Step("Вставляем абонента")
    fun insertAbonent(al: AbonentsLists, msisdn: Long, params: JsonAbonents, abonentSource: abonent_source_type, state: abonent_state): Abonents {
        var abonent: Abonents? = null
        transaction {
            setSchema(entities)
            TransactionManager.current().exec("INSERT INTO  entities.abonents (abonents_list_id, msisdn, params, abonent_source, state) " +
                    "VALUES('${al.id.value}', $msisdn, '$params', '$abonentSource', '$state');")

            abonent = Abonents.find { (AbonentsObject.abonents_list_id eq al.id) and (AbonentsObject.msisdn eq msisdn)}.first()
        }

        abonent ?: Exception("Abonent is null!!")

        return abonent!!
    }

    @Step("Создаем опрос")
    fun createCommunication(user : Users, json:JsonCommunicationData): Int? {
        var id: Int? = null
        transaction {
            setSchema(entities)
            TransactionManager.current().exec("SELECT entities.web_communication_add_f('${user.id.value}','$json');") { it.next(); id = it.getInt(1) }
        }

        return id
    }

    @Step("Ищем опрос")
    fun getCommunication(name: String): Communication {
        var communication: Communication? = null
        transaction {
            setSchema(entities)
            communication = Communication.find { CommunicationObject.name eq name }.first()
        }

        return communication!!
    }

    @Step("Ищем схему")
    fun getCommunicationTemplate(name: String): CommunicationTemplates? {
        var communicationTemplate: CommunicationTemplates? = null
        transaction {
            setSchema(entities)
            communicationTemplate = CommunicationTemplates.find { CommunicationTemplatesObject.name eq name }.first()
        }

        return communicationTemplate
    }

    @Step("Ищем списки абонентов опроса")
    fun getCommunicationAbonentsLists(communication: Communication) : List<CommunicationAbonentsLists>{
        var communicationAbonentsLists : List<CommunicationAbonentsLists>? = null
        transaction {
            setSchema(entities)
            communicationAbonentsLists = CommunicationAbonentsLists.find{CommunicationAbonentsListsObject.communication eq communication.id}.toList()
        }

        return communicationAbonentsLists!!
    }

    @Step("Ищем список абонентов")
    fun getAbonentsList(name: String): AbonentsLists{
        var abonentsList : AbonentsLists? = null
        transaction {
            setSchema(entities)
            abonentsList = AbonentsLists.find{AbonentsListsObject.name eq name}.first()
        }

        return abonentsList!!
    }

    @Step("Ищем абонентов по списку абонентов")
    fun getAbonents(abonentsLists: AbonentsLists): List<Abonents>{
        var abonents : List<Abonents>? = null
        transaction {
            setSchema(entities)
            abonents = Abonents.find{AbonentsObject.abonents_list_id eq abonentsLists.id}.toList()
        }

        return abonents!!
    }

    @Step("Запускаем опрос")
    fun startCommunication(user: Users, communication: Communication) : ResultStartCommunication {
        var result : Any? = null
        transaction {
            setSchema(entities)
            TransactionManager.current().exec("SELECT entities.web_communication_start_f('${user.id.value}','${communication.id.value}');",
                    { it.next(); result = it.getObject(1) })
        }

        if (result is PGobject){
            return ResultStartCommunication((result as PGobject).value)
        } else {
            return ResultStartCommunication()
        }


    }


}