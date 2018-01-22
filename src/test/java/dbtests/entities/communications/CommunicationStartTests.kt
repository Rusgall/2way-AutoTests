package dbtests.entities.communications

import dataprovider.EntitiesProvider
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
class CommunicationStartTests : CommunicationSteps()  {
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
        checkCreateTalks(communication, abonent, msisdnDefault, JsonCommunicationTemplate(), trace, receiveSn, ibs, mt,
                talkStatus, hasAnswer, talks.first())

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