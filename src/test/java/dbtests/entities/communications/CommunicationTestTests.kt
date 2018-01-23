package dbtests.entities.communications

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import db.query.ExternalQuery
import db.query.LogicQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import entity.logic.init_base_type_type
import entity.logic.msg_type_type
import entity.logic.talk_status
import entity.results.ResultCommunication
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

@Feature("ENTITIES")
class CommunicationTestTests : CommunicationSteps() {

    @Story("web_communication_test_f")
    @Test(description = "Успешный запуск тестирования опроса",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodTalks")
    fun successCommunicationTest(trace: Array<Int>, receiveSn: String, ibs: init_base_type_type,
                                 mt: msg_type_type, talkStatus: talk_status, hasAnswer: Boolean) {
        //Номер для тестирования
        val msisdn: Long = 1000001
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        //Запускаем тестирование опроса
        val result = EntitiesQuery.testCommunication(adminUser, communication, msisdn, JsonAbonents())

        //Проверяем результат функции
        checkResultCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Ищем список абонентов
        val abonentsList = EntitiesQuery.getAbonentsList(testCommunicationName + communication.id.value)
        //Ищем абонентов
        val abonents = EntitiesQuery.getAbonents(abonentsList)

        //Проверяем что абоненты создались
        checkAbonentsWithAbonentsList(arrayListOf(msisdn), abonentsList)
        checkAbonentsListHidden(abonentsList)

        //Проверяем статус опроса
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(communication.status, communication_status_type.DRAFT, "Статус опроса неверный")

        //Ищем разговоры
        val talks = LogicQuery.getTalks(communication)

        //Проверяем что создан 1 разговор
        Assert.assertEquals(talks.size, 1, "Было создано 0 или > 1 разговора")
        checkCreateTalks(communication, abonents, msisdn, JsonCommunicationTemplate(),
                trace, receiveSn, init_base_type_type.TEST, mt, talkStatus, hasAnswer, talks.first())
    }

    @Story("web_communication_test_f")
    @Test(description = "Ошибка запуска тестирования опроса: AUTH_ERROR")
    fun failedCommunicationTestAuthError() {
        //Номер для тестирования
        val msisdn: Long = 1000001
        //Создаем клиента и пользователя
        insertClient(name = "failedCommunicationTestAuthError")
        createUser(login = "failedCommunicationTestAuthError", client = ExternalQuery.getClient("failedCommunicationTestAuthError"))
        val user = ExternalQuery.getUser("failedCommunicationTestAuthError")

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        //Запускаем тестирование опроса
        val result = EntitiesQuery.testCommunication(user, communication, msisdn, JsonAbonents())

        //Проверяем результат функции
        checkResultCommunication(result, ResultCommunication(false, result_code_type.AUTH_ERROR))

        //Проверяем отсутсвие разговоров
        val actuaclCount = LogicQuery.getTalks(communication).size
        Assert.assertEquals(actuaclCount, 0)
    }

    @Story("web_communication_test_f")
    @Test(description = "Ошибка запуска тестирования опроса: ATTEMPT_TO_TEST_NO_DRAFT_COMMUNICATION",
            dataProviderClass = EntitiesProvider::class, dataProvider = "startCommunicationWithNoDraftStatus")
    fun failedCommunicationTestAttemptToTestNoDraftCommunication(status: communication_status_type) {
        //Номер для тестирования
        val msisdn: Long = 1000001

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Меняем статус опроса
        changeStatusCommunication(communication, status)

        //Запускаем тестирование опроса
        val result = EntitiesQuery.testCommunication(adminUser, communication, msisdn, JsonAbonents())

        //Проверяем результат функции
        checkResultCommunication(result, ResultCommunication(false, result_code_type.ATTEMPT_TO_TEST_NO_DRAFT_COMMUNICATION))

        //Проверяем отсутсвие разговоров
        val actuaclCount = LogicQuery.getTalks(communication).size
        Assert.assertEquals(actuaclCount, 0)
    }

    @Story("web_communication_test_f")
    @Test(description = "Ошибка запуска тестирования опроса: ATTEMPT_TO_TEST_ON_BUSY_NUMBER")
    fun failedCommunicationTestAttemptToTestOnBusyNumber() {
        val name = "failedCommunicationTestAttemptToTestOnBusyNumber"
        //Создаем первый опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value)))

        //Запускаем первый опрос
        EntitiesQuery.startCommunication(adminUser, EntitiesQuery.getCommunication(NameCommunication))

        //Создаем второй опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value), name = name))

        //Ищем второй опрос
        val communication = EntitiesQuery.getCommunication(name)

        //Запускаем тестирование второго опроса
        val result = EntitiesQuery.testCommunication(adminUser, communication, msisdnDefault, JsonAbonents())

        //Проверяем результат функции
        checkResultCommunication(result, ResultCommunication(false, result_code_type.ATTEMPT_TO_TEST_ON_BUSY_NUMBER))

        //Проверяем отсутсвие разговоров у опроса
        val actualCount = LogicQuery.getTalks(communication).size
        Assert.assertEquals(actualCount, 0)
    }
}