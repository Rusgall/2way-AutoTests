package db

import db.entity.entities.AbonentsObject
import db.entity.entities.CommunicationObject
import db.entity.entities.CommunicationAbonentsListsObject
import db.entity.entities.CommunicationTemplatesObject
import db.entity.entities.AbonentsListsObject
import db.entity.logic.TalksObject
import db.entity.Schema.*
import db.entity.Schema
import db.entity.external.Clients
import db.entity.external.ClientsObject
import db.entity.external.RoleObject
import db.entity.external.UsersObject
import db.entity.external.EventsLogObject
import db.query.EntitiesQuery
import db.query.ExternalQuery
import entity.external.JsonClient
import entity.external.JsonRole
import entity.external.JsonUser
import io.qameta.allure.Step
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.ds.PGPoolingDataSource
import utils.*
import utils.ResUtils.getProperties


object DBUtil {
    init {
        val source = PGPoolingDataSource()
        with(source) {
            dataSourceName = getProperties("dataSourceName")
            serverName = getProperties("serverName")
            databaseName = getProperties("databaseName")
            user = getProperties("user")
            password = getProperties("password")
        }
        Database.connect(source)
    }

    @Step("Чистим базу")
    fun clearDB() {
        transaction {
            setSchema(public, external, entities, logic)
            EventsLogObject.deleteAll()
            TalksObject.deleteAll()
            CommunicationAbonentsListsObject.deleteAll()
            CommunicationObject.deleteAll()
            CommunicationTemplatesObject.deleteAll()
            AbonentsObject.deleteAll()
            AbonentsListsObject.deleteAll()
            UsersObject.deleteAll()
            ClientsObject.deleteAll()
            RoleObject.deleteAll()
        }
    }

    @Step("Генерим начального юзера и клиента")
    fun insertInitialUser() {
        val client = ExternalQuery.insertClient(name = adminClientName, params = JsonClient())
        val role = ExternalQuery.insertRole(name = adminRoleName, description = "admin descr", super_role = true,
                params = JsonRole())
        ExternalQuery.createUser(name = "adminName", login = adminLogin, password = adminPass, superuser = true, role = role,
                client = client, params = JsonUser(), email = "email@test.ru")
    }

    fun setSchema(vararg values: Schema){
        var sb = StringBuilder()
        values.forEach { sb.append("'$it',") }
        sb.deleteCharAt(sb.length - 1)
        TransactionManager.current().exec("SET search_path TO ${sb}")
    }

}