package dbtests.entities.communications

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.annotations.Test
import java.util.*

@Feature("ENTITIES")
class CommunicationCreateTests : CommunicationSteps() {
    @Story("web_communication_add_f")
    @Test(description = "Успешное создание опроса: разный источник абонентов",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodCommunications")
    fun createCommunication(jsonCommunication: JsonCommunication, status: communication_status_type,
                            name: String, jsonCommunicationTemplate: JsonCommunicationTemplate,
                            msisdns: List<Long>?) {
        //Корректируем список абонентов для опроса
        val abonents = arrayListOf<Int>()
        var abonentsListName = alName
        if (jsonCommunication.db_src == "list") {
            abonents.add(abonentsList.id.value) // если тип выбран лист, берем подготовленный список абонентов
        } else {
            abonentsListName = """"$name"""" // иначе создастся новый список абонентов, с таким же названием как и у опроса
        }

        //Создаем опрос
        val idFromFun = EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = abonents, params = jsonCommunication,
                        schema = jsonCommunicationTemplate, name = name, msisdns = msisdns))

        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(name)
        //Ищем схему опроса
        val communcationTemplate = EntitiesQuery.getCommunicationTemplate(name)

        //Проверяем опрос
        checkCommunication(communication, idFromFun, adminClient, communcationTemplate, jsonCommunication,
                status, name, adminUser)
        //Проверяем схему опроса
        checkCommunicationTemplate(communication.id.value, adminClient, name, jsonCommunicationTemplate,
                adminUser, communcationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(abonentsListName)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))

        //Если номера ввели вручную, проверим что создались абоненты
        if (jsonCommunication.db_src == "manual")
            checkAbonentsWithAbonentsList(msisdns!!, abonentsList)

        //Если номера ввели вручную или выбран api, проверим что список абонентов скрыт, но не удален
        if (jsonCommunication.db_src == "manual" || jsonCommunication.db_src == "api")
            checkAbonentsListHidden(abonentsList)
    }

    @Story("web_communication_add_f")
    @Test(description = "Успешное создание опроса: несколько списков абонентов")
    fun createCommunicationSeveralAbonentLists() {

        //Создадим 3 списка абонентов, часть номеров будет пересекаться
        val msisdns_1 : List<Long> = arrayListOf(1000001, 1000002, 1000003)
        val msisdns_2 : List<Long> = arrayListOf(1000002, 1000003, 1000004)
        val msisdns_3 : List<Long> = arrayListOf(1000005, 1000006, 1000007)
        val abonentsLists = createAbonentsLists(arrayListOf(msisdns_1, msisdns_2, msisdns_3))

        //Создаем опрос
        val idFromFun = EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = abonentsLists.map { it.id.value }))

        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)
        //Ищем схему опроса
        val communcationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Проверяем опрос
        checkCommunication(communication, idFromFun, adminClient, communcationTemplate, JsonCommunication(),
                communication_status_type.DRAFT, NameCommunication, adminUser)
        //Проверяем схему опроса
        checkCommunicationTemplate(communication.id.value, adminClient, NameCommunication, JsonCommunicationTemplate(),
                adminUser, communcationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        checkCommunicationAbonentsLists(communication, abonentsLists)
    }
}