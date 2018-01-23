package dbtests.entities.communications

import dataprovider.EntitiesProvider
import db.entity.entities.Abonents
import db.query.EntitiesQuery
import db.query.ExternalQuery
import db.query.LogicQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import entity.logic.ResultCommunication
import entity.logic.init_base_type_type
import entity.logic.msg_type_type
import entity.logic.talk_status
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

@Feature("ENTITIES")
class CommunicationStartTests : CommunicationSteps() {
    @Story("web_communication_start_f")
    @Test(description = "Успешный запуск",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodTalks")
    fun succsessStartCommunication(trace: Array<Int>, receiveSn: String, ibs: init_base_type_type,
                                   mt: msg_type_type, talkStatus: talk_status, hasAnswer: Boolean) {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Проверяем статус опроса
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(communication.status, communication_status_type.ACTIVE, "Статус опроса неверный")

        //Ищем разговоры
        val talks = LogicQuery.getTalks(communication)

        //Проверяем что создан 1 разговор
        Assert.assertEquals(talks.size, 1, "Было создано 0 или > 1 разговора")
        checkCreateTalks(communication, Collections.singletonList(abonent), msisdnDefault, JsonCommunicationTemplate(),
                trace, receiveSn, ibs, mt, talkStatus, hasAnswer, talks.first())
    }

    @Story("web_communication_start_f")
    @Test(description = "Успешный запуск: несколько списков абонентов",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodTalks")
    fun succsessStartCommunicationSeveralAbonentsLists(trace: Array<Int>, receiveSn: String, ibs: init_base_type_type,
                                                       mt: msg_type_type, talkStatus: talk_status, hasAnswer: Boolean) {
        //Создадим 3 списка абонентов, часть номеров будет пересекаться
        val msisdns_1: List<Long> = arrayListOf(1000001, 1000002, 1000003)
        val msisdns_2: List<Long> = arrayListOf(1000002, 1000003, 1000004)
        val msisdns_3: List<Long> = arrayListOf(1000005, 1000006, 1000007)
        val abonentsLists = createAbonentsLists(arrayListOf(msisdns_1, msisdns_2, msisdns_3))
        //Ожидаем, что создадутся всего 7 разговоров с данными номерами
        val msisndsExpected: List<Long> = arrayListOf(1000001, 1000002, 1000003, 1000004, 1000005, 1000006, 1000007)
        //Список созданных абонентов
        val abonentsExpected: MutableList<Abonents> = arrayListOf()
        abonentsLists.forEach { abonentsExpected.addAll(EntitiesQuery.getAbonents(it)) }

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = abonentsLists.map { it.id.value }))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Проверяем статус опроса
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(communication.status, communication_status_type.ACTIVE, "Статус опроса неверный")

        //Ищем разговоры
        val talks = LogicQuery.getTalks(communication)

        //Проверяем что создано 7 разговоров
        Assert.assertEquals(talks.size, 7, "Было создано не 7 разговоров")
        for ((i, talk) in talks.withIndex()) {
            checkCreateTalks(communication, abonentsExpected, msisndsExpected[i], JsonCommunicationTemplate(), trace,
                    receiveSn, ibs, mt, talkStatus, hasAnswer, talk)
        }
    }

    @Story("web_communication_start_f")
    @Test(description = "Успешный запуск: ручной ввод номеров",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodTalks")
    fun successCommunicationStartDBSrcManual(trace: Array<Int>, receiveSn: String, ibs: init_base_type_type,
                                             mt: msg_type_type, talkStatus: talk_status, hasAnswer: Boolean) {
        val msisdns = arrayListOf<Long>(1000001, 1000002)
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(params = JsonCommunication(db_src = "manual"),
                abonents_list_id = arrayListOf(), msisdns = msisdns))

        //Ищем опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)
        //Ищем абонентов
        val abonents = EntitiesQuery.getAbonents(EntitiesQuery.getAbonentsList(""""$NameCommunication""""))

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)
        //Проверяем результат запуска
        checkResultSartCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Ищем разговоры
        val talks = LogicQuery.getTalks(communication)

        //Проверяем что создано 2 разговора
        Assert.assertEquals(talks.size, 2, "Было создано не 2 разговора")
        for ((i, talk) in talks.withIndex()) {
            checkCreateTalks(communication, abonents, msisdns[i], JsonCommunicationTemplate(), trace,
                    receiveSn, ibs, mt, talkStatus, hasAnswer, talk)
        }
    }

    @Story("web_communication_start_f")
    @Test(description = "Успешный запуск: API")
    fun successCommunicationStartDBSrcAPI() {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(params = JsonCommunication(db_src = "api"),
                abonents_list_id = arrayListOf()))

        //Ищем опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)
        //Проверяем результат запуска
        checkResultSartCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Ищем разговоры
        val talks = LogicQuery.getTalks(communication)

        //Проверяем разговоров нету
        Assert.assertEquals(talks.size, 0, "Был создан разговор")
    }

    @Story("web_communication_start_f")
    @Test(description = "Ошибка запуска: AUTH_ERROR")
    fun failedStartCommunicationAuthError() {
        //Создаем клиента и пользователя
        insertClient(name = "failedStartCommunicationAuthError")
        createUser(login = "failedStartCommunicationAuthError", client = ExternalQuery.getClient("failedStartCommunicationAuthError"))
        val user = ExternalQuery.getUser("failedStartCommunicationAuthError")

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(user, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(false, result_code_type.AUTH_ERROR))

        //Проверяем отсутсвие разговоров
        val actuaclCount = LogicQuery.getTalks(communication).size
        Assert.assertEquals(actuaclCount, 0)
    }

    @Story("web_communication_start_f")
    @Test(description = "Ошибка запуска: ATTEMPT_TO_START_NO_DRAFT_COMMUNICATION",
            dataProviderClass = EntitiesProvider::class, dataProvider = "startCommunicationWithNoDraftStatus")
    fun failedStartCommunicationAttemptToStartNoDraftCommunication(status: communication_status_type) {

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        //Меняем статус опроса
        changeStatusCommunication(communication, status)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(false, result_code_type.ATTEMPT_TO_START_NO_DRAFT_COMMUNICATION))

        //Проверяем отсутсвие разговоров
        val actuaclCount = LogicQuery.getTalks(communication).size
        Assert.assertEquals(actuaclCount, 0)
    }

    @Story("web_communication_start_f")
    @Test(description = "Ошибка запуска: ATTEMPT_TO_START_COMMUNICATION_IN_THE_PAST")
    fun failedStartCommunicationAttemptToStarCommunicationInThePast() {

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value),
                        params = JsonCommunication(date_to = "2018-01-01")))
        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(false, result_code_type.ATTEMPT_TO_START_COMMUNICATION_IN_THE_PAST))

        //Проверяем отсутсвие разговоров
        val actuaclCount = LogicQuery.getTalks(communication).size
        Assert.assertEquals(actuaclCount, 0)
    }
}