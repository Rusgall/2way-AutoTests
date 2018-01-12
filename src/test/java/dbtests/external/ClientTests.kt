package dbtests.external

import db.query.ExternalQuery
import dbsteps.external.ClientSteps
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.postgresql.util.PSQLException
import org.testng.Assert
import org.testng.annotations.Test

@Feature("Client")
class ClientTests : ClientSteps() {

    @Story("Удаление")
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
}