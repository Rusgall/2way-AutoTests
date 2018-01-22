package dbtests.entities.communications

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import db.query.ExternalQuery
import db.query.LogicQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import entity.logic.ResultCommunication
import entity.logic.talk_status
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

@Feature("ENTITIES")
class CommunicationStopTests : CommunicationSteps(){
    @Story("web_communication_stop_f")
    @Test(description = "Успешная остановка опроса",
            dataProviderClass = EntitiesProvider::class, dataProvider = "stopCommunicationWithDifferentTalksStatus")
    fun successStopCommunication(status: talk_status, statusAfterStopCommunication: talk_status) {

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        EntitiesQuery.startCommunication(adminUser, communication)

        //Ищем разговор
        var talk = LogicQuery.getTalks(communication).first()

        //Меняем статус разговора
        changeStatusTalk(talk, status)

        //Останваливем опрос
        val result = EntitiesQuery.stopCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Проверяем статус опроса
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(communication.status, communication_status_type.STOPPED, "Статус опроса неверный")

        //Проверяем статус разговора
        talk = LogicQuery.getTalks(communication).first()
        Assert.assertEquals(talk.status, statusAfterStopCommunication, "Статус разговора неверный")
    }

    @Story("web_communication_stop_f")
    @Test(description = "Успешная остановка опроса: 2 опроса по 2 разговора",
            dataProviderClass = EntitiesProvider::class, dataProvider = "stopCommunicationSeveralTalks")
    fun successStopCommunicationSeveralTalks(nameCommunication_1: String, nameCommunication_2: String, msisdns: List<Long>) {
        //Создаем 2 опроса
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(), msisdns = msisdns, name = nameCommunication_1,
                        params = JsonCommunication(db_src = "manual")))
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = arrayListOf(), msisdns = msisdns, name = nameCommunication_2,
                        params = JsonCommunication(db_src = "manual")))

        //Ищем первый опрос
        var communication_1 = EntitiesQuery.getCommunication(nameCommunication_1)
        //Ищем первый опрос
        var communication_2 = EntitiesQuery.getCommunication(nameCommunication_2)

        //Запускаем первый опрос
        EntitiesQuery.startCommunication(adminUser, communication_1)
        //Запускаем второй опрос
        EntitiesQuery.startCommunication(adminUser, communication_2)

        //Останваливем первый опрос
        val result = EntitiesQuery.stopCommunication(adminUser, communication_1)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(true, result_code_type.NO_ERROR))

        //Проверяем статус первого опроса
        communication_1 = EntitiesQuery.getCommunication(nameCommunication_1)
        Assert.assertEquals(communication_1.status, communication_status_type.STOPPED, "Статус первого опроса неверный")

        //Проверяем статус второго опроса
        communication_2 = EntitiesQuery.getCommunication(nameCommunication_2)
        Assert.assertEquals(communication_2.status, communication_status_type.ACTIVE, "Статус второго опроса неверный")

        //Проверяем статусы разговоров первого опроса
        val talks_1 = LogicQuery.getTalks(communication_1)
        Assert.assertEquals(talks_1.size, msisdns.size)
        for (talk in talks_1)
            Assert.assertEquals(talk.status, talk_status.STOPPED, "Статус разговора первого опроса неверный")

        //Проверяем статусы разговоров второго опроса
        val talks_2 = LogicQuery.getTalks(communication_2)
        Assert.assertEquals(talks_2.size, msisdns.size)
        for (talk in talks_2)
            Assert.assertEquals(talk.status, talk_status.READY_FOR_START, "Статус разговора второго опроса неверный")
    }

    @Story("web_communication_stop_f")
    @Test(description = "Остановка опроса с ошибкой: AUTH_ERROR")
    fun failedStopCommunicationAuthError() {
        //Создаем клиента и пользователя
        insertClient(name = "failedStopCommunicationAuthError")
        createUser(login = "failedStopCommunicationAuthError", client = ExternalQuery.getClient("failedStopCommunicationAuthError"))
        val user = ExternalQuery.getUser("failedStopCommunicationAuthError")

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        EntitiesQuery.startCommunication(adminUser, communication)

        //Останваливем опрос
        val result = EntitiesQuery.stopCommunication(user, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(false, result_code_type.AUTH_ERROR))

        //Проверяем статус опроса
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(communication.status, communication_status_type.ACTIVE, "Статус опроса неверный")

        //Проверяем статус разговора
        val talk = LogicQuery.getTalks(communication).first()
        Assert.assertEquals(talk.status, talk_status.READY_FOR_START, "Статус разговора неверный")
    }

    @Story("web_communication_stop_f")
    @Test(description = "Ошибка остановки опроса: ATTEMPT_TO_STOP_NO_ACTIVE_COMMUNICATION",
            dataProviderClass = EntitiesProvider::class, dataProvider = "stopCommunicationWithDifferentCommunicationStatus")
    fun failedStopCommunicationAttemptToStopNoActiveCommunication(status: communication_status_type) {

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        EntitiesQuery.startCommunication(adminUser, communication)

        //Меняем статус опроса
        changeStatusCommunication(communication, status)

        //Останваливем опрос
        val result = EntitiesQuery.stopCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultCommunication(false, result_code_type.ATTEMPT_TO_STOP_NO_ACTIVE_COMMUNICATION))

        //Проверяем статус опроса
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(communication.status, status, "Статус опроса неверный")

        //Проверяем статус разговора
        val talk = LogicQuery.getTalks(communication).first()
        Assert.assertEquals(talk.status, talk_status.READY_FOR_START, "Статус разговора неверный")
    }
}