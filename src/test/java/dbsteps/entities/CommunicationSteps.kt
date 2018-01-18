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
import db.entity.logic.Talks
import entity.logic.*

abstract class CommunicationSteps : BaseSteps() {

    lateinit var abonentsList: AbonentsLists
    lateinit var abonent: Abonents
    val alName = "Base AL"
    val msisdnDefault = 89818235391


    @BeforeMethod(description = "Генерим начальных абонентов")
    fun beforeMethodSteps() {
        abonentsList = EntitiesQuery.insertAbonentsList(adminClient, alName,
                false, false, JsonAbonentsLists(), adminUser)
        abonent = EntitiesQuery.insertAbonent(abonentsList, msisdnDefault, JsonAbonents(), abonent_source_type.DB_LIST, abonent_state.UNCHECKED)
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

    @Step("Проверяем, что абоненты привязаны к списку абонентов")
    fun checkAbonentsWithAbonentsList(msisdns: List<Long>, abonentsLists: AbonentsLists) {
        val abonentsMsisnds = EntitiesQuery.getAbonents(abonentsLists).map { it.msisdn }
        Assert.assertEquals(abonentsMsisnds.size, msisdns.size, "Кол-во абонентов не совпадает")
        Assert.assertTrue(abonentsMsisnds.containsAll(msisdns), "Номера абонентов не совпадают")
    }

    @Step("Проверяем, что список абонентов скрыт")
    fun checkAbonentsListHidden(abonentsLists: AbonentsLists) {
        Assert.assertTrue(abonentsLists.hidden, "Список абонентов не скрыт")
        Assert.assertFalse(abonentsLists.deleted, "Список абонентов удален")
    }

    @Step("Проверяем результат старта опроса")
    fun checkResultSartCommunication(actualResult: ResultStartCommunication, expectResult: ResultStartCommunication) {
        Assert.assertEquals(actualResult, expectResult)
    }

    @Step("Проверяем создание разговора")
    fun checkCreateTalks(communication: Communication, abonent: Abonents, msisdn: Long, schema: JsonCommunicationTemplate,
                         trace: Array<Int>, receiveSn: String, init_base_type_type: init_base_type_type,
                         msgType: msg_type_type, status: talk_status, hasAnswer: Boolean, talk:Talks) {
        transaction {
            DBUtil.setSchema(entities, logic)
            Assert.assertEquals(talk.communication.id.value, communication.id.value, "Разговор из другого опроса")
            Assert.assertEquals(talk.abonent.id.value, abonent.id.value, "Разговор другого абонента")
            Assert.assertEquals(talk.msisdn, msisdn, "Не совпадает номер телефона")
            Assert.assertEquals(talk.schema, schema, "Схема разговора не совпадает")
            Assert.assertEquals(talk.trace, trace, "trace не совпадает")
            Assert.assertEquals(talk.receive_sn, receiveSn, "receive_sn не совпадает")
            Assert.assertEquals(talk.status, status, "status неправильный")
            Assert.assertEquals(talk.has_answer, hasAnswer, "hasAnswer не совпадает")
        }


    }

}