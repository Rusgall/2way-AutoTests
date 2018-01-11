package dbtests.external

import dataprovider.ExternalProvider
import dbtests.BaseTest
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.Test
import db.entity.external.Users
import db.query.ExternalQuery
import dbsteps.external.UserSteps
import io.qameta.allure.Feature
import io.qameta.allure.Step
import io.qameta.allure.Story
import org.testng.Assert
import utils.adminIncorrectLogin
import utils.adminIncorrectPass
import utils.adminLogin
import utils.adminPass

@Feature("User")
class UserTests : UserSteps() {

    @Story("Аутентификация")
    @Test(description = "Успешная аутентификация")
    fun successAuthentication() {
        successAuthentication(adminLogin, adminPass)
    }

    @Story("Аутентификация")
    @Test(description = "Ошибка аутентификации: юзер заблокирован")
    fun failAuthenticationUserBlocked() {
        blockUser(adminLogin)
        failAuthentication(adminLogin, adminPass, "ОШИБКА: User is blocked: login=$adminLogin")
    }

    @Story("Аутентификация")
    @Test(description = "Ошибка аутентификации: юзер удален")
    fun failAuthenticationUserDeleted() {
        deleteUser(adminLogin)
        failAuthentication(adminLogin, adminPass, "ОШИБКА: No such user: login=$adminLogin")
    }

    @Story("Аутентификация")
    @Test(description = "Ошибка аутентификации: юзера нет")
    fun failAuthenticationIncorrectLogin() {
        failAuthentication(adminIncorrectLogin, adminPass, "ОШИБКА: No such user: login=$adminIncorrectLogin")
    }

    @Story("Аутентификация")
    @Test(description = "Ошибка аутентификации: неправильный пароль")
    fun failAuthenticationIncorrectPassword() {
        failAuthentication(adminLogin, adminIncorrectPass, "ОШИБКА: Incorrect password")
    }

    @Story("Блокировка")
    @Test(description = "Успешная блокировка")
    fun blockUser(){
        //Блокируем админа
        val idFromFun = blockUser(adminLogin)

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.blocked, true, "Пользователь не заблокировался")
    }

    @Story("Смена пароля")
    @Test(description = "Успешная смена пароля")
    fun changePassword(){
        //Меняем пароль админу
        val idFromFun = changePassword(adminLogin, "password")

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.encrypted_password, "\$2a\$06\$c91qWHlcz1ZPTotob49CBOa25d9ENBo0y9Y4CtlORFpm3kzTnHAva", "Пароль не сменился")
    }


}