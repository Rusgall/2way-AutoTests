package db.query

import db.entity.Schema.*
import db.DBUtil.setSchema
import db.entity.external.Clients
import db.entity.external.Roles
import db.entity.external.*
import entity.external.JsonClient
import entity.external.JsonRole
import entity.external.JsonUser
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import io.qameta.allure.Step

object ExternalQuery {
    @Step("Вставляем клиента")
    fun insertClient(name: String, params: JsonClient): Clients {
        var client: Clients? = null
        transaction {
            setSchema(external)

            client = Clients.new {
                this.name = name
                this.params = params
            }
        }
        client ?: Exception("Client is null!!")
        return client!!
    }

    @Step("Вставляем роль")
    fun insertRole(name: String, description: String, super_role: Boolean, params: JsonRole): Roles {
        var role: Roles? = null
        transaction {
            setSchema(external)

            role = Roles.new {
                this.name = name
                this.description = description
                this.super_role = super_role
                this.params = params
            }
        }
        role ?: Exception("Role is null!!")
        return role!!
    }

    @Step("Создаем пользователя")
    fun createUser(name: String, login: String, password: String, superuser: Boolean, role: Roles?, client: Clients?, params: JsonUser?, email: String): Int? {
        var id: Int? = null
        transaction {
            setSchema(public, external)
            TransactionManager.current().exec("Select external.create_user('$name', '$login', '$password', $superuser, ${role?.id?.value}, ${client?.id?.value}, '$params', '$email');") { it.next(); id = it.getInt(1) }
        }

        return id
    }

    @Step("Аутентификация пользователя")
    fun authenticationUser(login: String, password: String): Int? {
        var id: Int? = null
        transaction {
            setSchema(public, external)
            TransactionManager.current().exec("SELECT external.authentication_user('$login','$password');") { it.next(); id = it.getInt(1) }
        }

        return id
    }

    @Step("Ищем юзера")
    fun getUser(login: String): Users? {
        var user: Users? = null
        transaction {
            setSchema(external)
            user = Users.find { UsersObject.login eq login }.first()
        }

        return user
    }

    @Step("Ищем клиента")
    fun getClient(name: String): Clients? {
        var client: Clients? = null
        transaction {
            setSchema(external)
            client = Clients.find { ClientsObject.name eq name }.first()
        }

        return client
    }

    @Step("Ищем роль")
    fun getRole(name: String): Roles? {
        var role: Roles? = null
        transaction {
            setSchema(external)
            role = Roles.find { RoleObject.name eq name }.first()
        }

        return role
    }

    @Step("Блокируем пользователя")
    fun blockUser(login: String): Int? {
        var id: Int? = null
        transaction {
            setSchema(external)
            TransactionManager.current().exec("SELECT external.block_user('$login');") { it.next(); id = it.getInt(1) }
        }

        return id
    }

    @Step("Удаляем пользователя")
    fun deleteUser(login: String): Int? {
        var id: Int? = null
        transaction {
            setSchema(external)
            TransactionManager.current().exec("SELECT external.delete_user('$login');") { it.next(); id = it.getInt(1) }
        }

        return id
    }

    @Step("Меняем пароль")
    fun changePassword(login: String, newPass: String): Int? {
        var id: Int? = null
        transaction {
            setSchema(public, external)
            TransactionManager.current().exec("SELECT external.change_user_password('$login', '$newPass');") { it.next(); id = it.getInt(1) }
        }

        return id

    }
}