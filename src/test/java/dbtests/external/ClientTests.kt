package dbtests.external

import db.query.ExternalQuery
import dbsteps.external.ClientSteps
import entity.external.JsonClient
import entity.external.JsonClientData
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.postgresql.util.PSQLException
import org.testng.Assert
import org.testng.annotations.Test

@Feature("EXTERNAL")
class ClientTests : ClientSteps() {

    @Story("client_delete")
    @Test(description = "Успешно удаляем клиента")
    fun deleteClient() {
        //Вставляем клиента
        val client = insertClient()
        //Удаляем клиента
        val answer = deleteClient(client.id.value)
        //Ищем клиента
        val updateClient = ExternalQuery.getClient(client.name)

        Assert.assertEquals(answer, true, "Функция вернула неверный ответ")
        Assert.assertEquals(updateClient, null, "Клиент не удален")
    }

    @Story("client_add")
    @Test(description = "Успешно создаем клиента")
    fun createClient(){
        //Создаем клиента
        val name = "Test Client Name"
        val idFromFun = ExternalQuery.createClient(JsonClientData(name = name))

        //Ищем клиента
        val client = ExternalQuery.getClient(name)

        //Проверяем клиента
        Assert.assertEquals(client?.id?.value, idFromFun, "Функция вернула неправильный id")
        Assert.assertEquals(client?.name, name, "Неправильное имя")
        Assert.assertEquals(client?.params, JsonClient(), "Не совпадает json")
    }
}