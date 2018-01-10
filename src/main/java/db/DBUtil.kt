package db

import db.entity.Schema.*
import db.entity.Schema
import db.entity.external.Clients
import db.entity.external.ClientsObject
import db.entity.external.RoleObject
import db.entity.external.UsersObject
import db.query.ExternalQuery
import entity.external.JsonClient
import entity.external.JsonRole
import entity.external.JsonUser
import io.qameta.allure.Step
import org.jetbrains.exposed.sql.Database
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
            connection.schema = "external"
            UsersObject.deleteAll()
            ClientsObject.deleteAll()
            RoleObject.deleteAll()
        }
    }

    @Step("Генерим начальные данные")
    fun insertInitialData() {
        val client = ExternalQuery.insertClient(name = adminClientName, params = JsonClient("1", "json name"))
        val role = ExternalQuery.insertRole(name = adminRoleName, description = "admin descr", super_role = true,
                params = JsonRole("2", "json name"))
        ExternalQuery.createUser(name = "testName", login = adminLogin, password = adminPass, superuser = true, role = role,
                client = client, params = JsonUser("3", "json name"), email = "email@test.ru")
    }

    fun setSchema(vararg values: Schema){
        var sb = StringBuilder()
        values.forEach { sb.append("'$it',") }
        sb.deleteCharAt(sb.length - 1)
        TransactionManager.current().exec("SET search_path TO ${sb}")
    }

}