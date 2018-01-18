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
import io.qameta.allure.Epic
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

@Feature("EXTERNAL")
class UserTests : UserSteps() {

    @Story("authentication_user")
    @Test(description = "Успешная аутентификация")
    fun successAuthentication() {
        successAuthentication(adminLogin, adminPass)
    }

    @Story("authentication_user")
    @Test(description = "Ошибка аутентификации: юзер заблокирован")
    fun failAuthenticationUserBlocked() {
        blockUser(adminLogin)
        failAuthentication(adminLogin, adminPass, "ОШИБКА: User is blocked: login=$adminLogin")
    }

    @Story("authentication_user")
    @Test(description = "Ошибка аутентификации: юзер удален")
    fun failAuthenticationUserDeleted() {
        deleteUser(adminLogin)
        failAuthentication(adminLogin, adminPass, "ОШИБКА: No such user: login=$adminLogin")
    }

    @Story("authentication_user")
    @Test(description = "Ошибка аутентификации: юзера нет")
    fun failAuthenticationIncorrectLogin() {
        failAuthentication(adminIncorrectLogin, adminPass, "ОШИБКА: No such user: login=$adminIncorrectLogin")
    }

    @Story("authentication_user")
    @Test(description = "Ошибка аутентификации: неправильный пароль")
    fun failAuthenticationIncorrectPassword() {
        failAuthentication(adminLogin, adminIncorrectPass, "ОШИБКА: Incorrect password")
    }

    @Story("block_user")
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

    @Story("unblock_user")
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

    @Story("change_user_password")
    @Test(description = "Успешная смена пароля")
    fun changePassword() {
        //Меняем пароль админу
        val idFromFun = changePassword(adminLogin, "password")

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        successAuthentication(adminLogin, "password")
    }


    @Story("create_user")
    @Test(description = "Успешное создание пользователя",
            dataProviderClass = ExternalProvider::class, dataProvider = "goodUser")
    fun createUser(name: String, login:String, pass:String, superuser:Boolean, deleted:Boolean, blocked:Boolean,
                   params:JsonUser, email:String) {

        createUser(name = name, login = login, password = pass, superuser = superuser, params = params, email = email)

        val user = ExternalQuery.getUser(login)

        Assert.assertEquals(user?.name, name, "Не совпадает имя")
        Assert.assertEquals(user?.login, login, "Не совпадает логин")
        Assert.assertEquals(user?.superuser, superuser, "Не совпадает superuser")
        Assert.assertEquals(user?.deleted, deleted, "Не совпадает deleted")
        Assert.assertEquals(user?.blocked, blocked, "Не совпадает blocked")
        Assert.assertEquals(user?.params, params, "Не совпадает params")
        Assert.assertEquals(user?.email, email, "Не совпадает email")

        successAuthentication(login, pass)
    }

    @Story("create_user")
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

    @Story("delete_user")
    @Test(description = "Успешное удаление")
    fun deleteUser() {
        //Блокируем админа
        val idFromFun = deleteUser(adminLogin)

        //Ищем админа и его id
        val admin = ExternalQuery.getUser(adminLogin)
        val idUser = admin?.id?.value

        Assert.assertEquals(idFromFun, idUser, "Функция вернула другой id")
        Assert.assertEquals(admin?.deleted, true, "Пользователь не удален")
    }


}