package dbsteps.entities

import db.entity.entities.Abonents
import db.entity.entities.AbonentsLists
import db.entity.entities.Communication
import db.entity.entities.CommunicationTemplates
import db.entity.external.Clients
import db.entity.external.Users
import db.query.EntitiesQuery
import db.DBUtil
import dbsteps.BaseSteps
import entity.entities.*
import io.qameta.allure.Step
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.Assert
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod
import db.entity.Schema.*

abstract class CommunicationSteps : BaseSteps() {

    lateinit var abonentsList: AbonentsLists
    lateinit var abonent: Abonents
    val alName = "Base AL"


    @BeforeMethod(description = "Генерим начальных абонентов")
    fun beforeMethodSteps() {
        abonentsList = EntitiesQuery.insertAbonentsList(adminClient, alName,
                false, false, JsonAbonentsLists(), adminUser)
        abonent = EntitiesQuery.insertAbonent(abonentsList, 89818235391, JsonAbonents(), abonent_source_type.DB_LIST, abonent_state.UNCHECKED)
    }

    @AfterMethod(description = "Чистим базу от абонентов")
    fun afterMethodSteps() {
        EntitiesQuery.clearDB()
    }

    @Step("Проверяем опрос")
    fun checkCommunication(communication: Communication?, id: Int?, client: Clients, communicationTemplate: CommunicationTemplates?,
                           params: JsonCommunication, status: communication_status_type, name: String, user: Users) {

        transaction {
            DBUtil.setSchema(entities, external)
            Assert.assertEquals(communication?.id?.value, id, "Функция вернула неверный id")
            Assert.assertEquals(communication?.client?.id?.value, client.id.value, "У опроса не тот клиент")
            Assert.assertEquals(communication?.communication_template?.id?.value, communicationTemplate?.id?.value, "У опроса не совпадает схема")
            Assert.assertEquals(communication?.params, params, "У опроса не совпадают параметры")
            Assert.assertEquals(communication?.status, status, "У опроса не совпадают статус")
            Assert.assertEquals(communication?.name, name, "У опроса не совпадает имя")
            Assert.assertEquals(communication?.owner_user?.id?.value, user.id.value, "У опроса не совпадает юзер")
        }
    }

    @Step("Проверяем схему опроса")
    fun checkCommunicationTemplate(id: Int?, client: Clients, name: String, schema: JsonCommunicationTemplate,
                                   user: Users, communicationTemplate: CommunicationTemplates?) {

        transaction {
            DBUtil.setSchema(entities, external)
            Assert.assertEquals(communicationTemplate?.id?.value, id, "Функция вернула неверный id")
            Assert.assertEquals(communicationTemplate?.client?.id?.value, client.id.value, "У схемы не тот клиент")
            Assert.assertEquals(communicationTemplate?.name, name, "У схемы не совпадает имя")
            Assert.assertEquals(communicationTemplate?.schema, schema, "У схемы не совпадает схема")
            Assert.assertEquals(communicationTemplate?.owner_user?.id?.value, user.id.value, "У схемы не совпадает юзер")
        }

    }

    @Step("Проверяем списки абонентов опросов")
    fun checkCommunicationAbonentsLists(communication: Communication, abonentsLists: List<AbonentsLists>) {

        val cal = EntitiesQuery.getCommunicationAbonentsLists(communication)
        for (al in cal) {
            transaction {
                Assert.assertEquals(al.communication.id.value, communication.id.value, "Абонент лист привязался не к тому опросу")
                Assert.assertTrue(abonentsLists.filter { it.id.value == al.abonentsList.id.value }.size == 1,
                        "Абонент лист ${al.id.value} не привязался к опросу")
            }
        }
    }
}