package dbtests.external

import com.sun.org.apache.xpath.internal.operations.Bool
import dataprovider.ExternalProvider
import dbtests.BaseTest
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.Test
import db.entity.external.Users
import db.entity.external.UsersObject
import db.query.ExternalQuery
import dbsteps.external.UserSteps
import entity.external.JsonUser
import io.qameta.allure.Feature
import io.qameta.allure.Step
import io.qameta.allure.Story
import org.jetbrains.exposed.dao.EntityID
import org.postgresql.util.PSQLException
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
    fun blockUser() {
        //Блокируем админа
        val idFromFun = blockUser(adminLogin)

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.blocked, true, "Пользователь не заблокировался")
    }

    @Story("Блокировка")
    @Test(description = "Успешная разблокировка")
    fun unblockUser() {
        //Блокируем админа
        blockUser(adminLogin)
        //Разблокируем админа
        val idFromFun = unblockUser(adminLogin)

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.blocked, false, "Пользователь не разблокировался")
    }

    @Story("Смена пароля")
    @Test(description = "Успешная смена пароля")
    fun changePassword() {
        //Меняем пароль админу
        val idFromFun = changePassword(adminLogin, "password")

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.encrypted_password, "надо подумать", "Пароль не сменился")
    }


    @Story("Создание пользователя")
    @Test(description = "Успешное создание пользователя",
            dataProviderClass = ExternalProvider::class, dataProvider = "goodUser")
    fun createUser(name: String, login:String, pass:String, superuser:Boolean, deleted:Boolean, blocked:Boolean,
                   params:JsonUser, email:String) {

        createUser(name = name, login = login, password = pass, superuser = superuser, params = params, email = email)

        val user = ExternalQuery.getUser(login)

        Assert.assertEquals(user?.name, name, "Не совпадает имя")
        Assert.assertEquals(user?.login, login, "Не совпадает логин")
        Assert.assertEquals(user?.encrypted_password, pass, "Не совпадает пароль")
        Assert.assertEquals(user?.superuser, superuser, "Не совпадает superuser")
        Assert.assertEquals(user?.deleted, deleted, "Не совпадает deleted")
        Assert.assertEquals(user?.blocked, blocked, "Не совпадает blocked")
        Assert.assertEquals(user?.params, params, "Не совпадает имя")
        Assert.assertEquals(user?.email, email, "Не совпадает имя")
    }

    @Story("Создание пользователя")
    @Test(description = "Создание пользователя с уже существующим логином",
            dataProviderClass = ExternalProvider::class, dataProvider = "goodUser")
    fun createUserLoginAlreadyExists(name: String, login:String, pass:String, superuser:Boolean, deleted:Boolean, blocked:Boolean,
                                     params:JsonUser, email:String){
        var actualError: String? = null

        //Пытаемся создать пользователя с логимном админа
        try {
            createUser(name = name, login = adminLogin, password = pass, superuser = superuser, params = params, email = email)
        }catch (e: PSQLException){
            actualError = e.message
        }

        Assert.assertEquals(actualError, "ОШИБКА: User '$adminLogin' already exists!", "Ошибка не совпадает")
    }

    @Story("Удаление")
    @Test(description = "Успешное удаление")
    fun deletUser() {
        //Блокируем админа
        val idFromFun = deleteUser(adminLogin)

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.deleted, true, "Пользователь не удален")
    }


}