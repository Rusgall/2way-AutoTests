package dbsteps.external

import db.DBUtil
import db.query.ExternalQuery
import dbsteps.BaseSteps
import dbtests.BaseTest
import io.qameta.allure.Step
import org.postgresql.util.PSQLException
import org.testng.Assert
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import utils.adminEmail
import utils.adminRoleName
import utils.blockedLogin
import utils.blockedPass

abstract class UserSteps : BaseSteps() {

    @BeforeMethod(description = "Подготовка User тестов")
    fun beforeUserTests() {
//        ExternalQuery.createUser("name", blockedLogin, blockedPass, true, adminRole, adminClient, adminUserJson, adminEmail)
    }

    @Step("Успешная аутентификация.")
    fun successAuthentication(login: String, pass: String) {
        val user = ExternalQuery.getUser(login)
        val exceptId = user?.id?.value
        val actualId = ExternalQuery.authenticationUser(login, pass)
        Assert.assertEquals(actualId, exceptId, "Результат функции отличается от id пользователя")
    }

    @Step("Аутентификация с ошибкой")
    fun failAuthentication(login: String, pass: String, exceptError: String) {
        var actualError: String? = null
        try {
            ExternalQuery.authenticationUser(login, pass)
        } catch (e: PSQLException) {
            actualError = e.message
        }
        Assert.assertEquals(actualError, exceptError, "Функция вернула неправильную ошибку")
    }

    @Step("Блокируем пользователя")
    fun blockUser(login: String): Int? {
        return ExternalQuery.blockUser(login)
    }

    @Step("Разблокируем пользователя")
    fun unblockUser(login: String): Int? {
        return ExternalQuery.unblockUser(login)
    }

    @Step("Удаляем пользователя")
    fun deleteUser(login: String): Int? {
        return ExternalQuery.deleteUser(login)
    }

    @Step("Меняем пароль")
    fun changePassword(login: String, newPass: String): Int? {
        return ExternalQuery.changePassword(login, newPass)
    }

}