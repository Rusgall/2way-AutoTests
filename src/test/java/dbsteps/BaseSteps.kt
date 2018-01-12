package dbsteps

import db.entity.external.Clients
import db.entity.external.Roles
import db.query.ExternalQuery
import dbtests.BaseTest
import entity.external.JsonClient
import entity.external.JsonUser
import io.qameta.allure.Step
import utils.adminEmail
import utils.adminLogin
import utils.adminPass

abstract class BaseSteps : BaseTest(){

    @Step("Создаем пользователя")
    fun createUser(name: String = "User name", login: String = adminLogin, password: String = adminPass,
                   superuser: Boolean = true, role: Roles? = adminRole, client: Clients? = adminClient,
                   params: JsonUser? = adminUser?.params, email: String = adminEmail): Int?{
        return ExternalQuery.createUser(name, login, password, superuser, role, client, params, email)

    }

    @Step("Вставляем клиента")
    fun insertClient(name: String = "test client", params: JsonClient = JsonClient()): Clients{
        return ExternalQuery.insertClient(name, params)
    }
}