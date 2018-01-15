package db.query

import db.entity.entities.AbonentsListsObject
import db.entity.entities.AbonentsObject
import db.entity.Schema.*
import db.entity.entities.AbonentsLists
import db.entity.external.Clients
import db.entity.external.Roles
import db.entity.external.Users
import entity.entities.JsonAbonentsLists
import entity.external.JsonUser
import db.DBUtil.setSchema
import db.entity.entities.Abonents
import entity.entities.JsonAbonents
import entity.entities.abonent_source_type
import entity.entities.abonent_state
import io.qameta.allure.Step
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.BatchUpdateException

object EntitiesQuery {

    @Step("Чистим абонетов")
    fun clearDB(){
        transaction {
            setSchema(entities)
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

    @Step("Вставляем абонентов")
    fun insertAbonent(al: AbonentsLists, msisdn: Long, params :JsonAbonents, abonentSource: abonent_source_type, state: abonent_state): Abonents {
        var abonent: Abonents? = null
        try{
            transaction {
                setSchema(entities)

                abonent = Abonents.new {
                    this.abonents_list_id = al
                    this.msisdn = msisdn
                    this.params = params
                    this.abonent_source = abonentSource
                    this.state = state
                }
            }
        }catch (e : BatchUpdateException){
            println(e.nextException)
            throw e
        }


        abonent ?: Exception("Abonents list is null!!")
        return abonent!!
    }
}