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
class CommunicationSaveTests : CommunicationSteps() {

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: параметры + схема (без списка абонентов)",
            dataProviderClass = EntitiesProvider::class, dataProvider = "saveCommunications")
    fun successCommunicationSaveParametersAndScheme(jsonCommunication: JsonCommunication,
                                                    jsonCommunicationTemplate: JsonCommunicationTemplate,name: String) {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(name = name, id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value, params = jsonCommunication,
                schema = jsonCommunicationTemplate, abonents_list_id = Collections.singletonList(abonentsList.id.value)))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(name)
        // Ищем схему опроса
        communicationTemplate = EntitiesQuery.getCommunicationTemplate(name)

        //Проверяем опрос
        checkCommunication(communication, communication.id.value, adminClient, communicationTemplate, jsonCommunication,
                communication_status_type.DRAFT, name, adminUser)
        //Проверяем схему опроса
        checkCommunicationTemplate(communication.id.value, adminClient, name, jsonCommunicationTemplate, adminUser,
                communicationTemplate)
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: изменение источника абонентов с листа на другой",
            dataProviderClass = EntitiesProvider::class, dataProvider = "saveCommunicationsFromList")
    fun successCommunicationSaveDBSrcFromList(db_src : String, msisdns : List<Long>?) {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                params = JsonCommunication(db_src = db_src),msisdns = msisdns, id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(NameCommunication)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
        //Если номера ввели вручную, проверим что создались абоненты
        if (db_src == "manual")
            checkAbonentsWithAbonentsList(msisdns!!, abonentsList)

        //Если номера ввели вручную или выбран api, проверим что список абонентов скрыт, но не удален
        if (db_src == "manual" || db_src == "api")
            checkAbonentsListHidden(abonentsList)
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: изменение источника абонентов с ручного на лист")
    fun successCommunicationSaveDBSrcFromManualToList() {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = arrayListOf(), msisdns = arrayListOf(1000001, 1000002),
                params = JsonCommunication(db_src = "manual")))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value),
                params = JsonCommunication(db_src = "list"), id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(alName)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: изменение источника абонентов с ручного на api")
    fun successCommunicationSaveDBSrcFromManualToApi() {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = arrayListOf(), msisdns = arrayListOf(1000001, 1000002),
                params = JsonCommunication(db_src = "manual")))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                params = JsonCommunication(db_src = "api"), id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(NameCommunication)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
        checkAbonentsListHidden(abonentsList)
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: изменение источника абонентов с api на list")
    fun successCommunicationSaveDBSrcFromApiToList() {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                params = JsonCommunication(db_src = "api")))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value),
                params = JsonCommunication(db_src = "list"), id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(alName)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: изменение источника абонентов с api на manual")
    fun successCommunicationSaveDBSrcFromApiToManual() {
        val msisdns = arrayListOf<Long>(1000001, 1000003, 1000002)
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                params = JsonCommunication(db_src = "api")))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                params = JsonCommunication(db_src = "manual"), id = communication.id.value, msisdns = msisdns,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(NameCommunication)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
        checkAbonentsListHidden(abonentsList)

        //Проверим что создались абоненты
        checkAbonentsWithAbonentsList(msisdns, abonentsList)
        checkAbonentsListHidden(abonentsList)
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: добавить еще списки абонентов")
    fun successCommunicationSaveDBSrcAddList() {
        //Создадим 3 списка абонентов, часть номеров будет пересекаться
        val msisdns_1 : List<Long> = arrayListOf(1000001, 1000002, 1000003)
        val msisdns_2 : List<Long> = arrayListOf(1000002, 1000003, 1000004)
        val msisdns_3 : List<Long> = arrayListOf(1000005, 1000006, 1000007)
        val abonentsLists = createAbonentsLists(arrayListOf(msisdns_1, msisdns_2, msisdns_3))

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = Collections.singletonList(abonentsLists.first().id.value)))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = abonentsLists.map { it.id.value },
                params = JsonCommunication(db_src = "list"), id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        checkCommunicationAbonentsLists(communication, abonentsLists)
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: убрать списки абонентов")
    fun successCommunicationSaveDBSrcRemoveList() {
        //Создадим 3 списка абонентов, часть номеров будет пересекаться
        val msisdns_1 : List<Long> = arrayListOf(1000001, 1000002, 1000003)
        val msisdns_2 : List<Long> = arrayListOf(1000002, 1000003, 1000004)
        val msisdns_3 : List<Long> = arrayListOf(1000005, 1000006, 1000007)
        val abonentsLists = createAbonentsLists(arrayListOf(msisdns_1, msisdns_2, msisdns_3))

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = abonentsLists.map { it.id.value }))
        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(abonentsLists.first().id.value),
                params = JsonCommunication(db_src = "list"), id = communication.id.value,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        checkCommunicationAbonentsLists(communication, arrayListOf(abonentsLists.first()))
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: добавить еще ручных номеров")
    fun successCommunicationSaveDBSrcAddManual() {
        //Исходный список номеров
        val originMsisdns = arrayListOf<Long>(1000001,1000002,1000003)
        //Новый список номеров
        val newMsisdns = arrayListOf<Long>(1000001,1000002,1000003,1000004,1000005)

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                msisdns = originMsisdns, params = JsonCommunication(db_src = "manual")))

        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)
        //Ищем список абонентов
        var abonentsList = EntitiesQuery.getAbonentsList(""""$NameCommunication"""")

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value),
                params = JsonCommunication(db_src = "manual"), id = communication.id.value, msisdns = newMsisdns,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        abonentsList = EntitiesQuery.getAbonentsList(""""$NameCommunication"""")
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
        checkAbonentsListHidden(abonentsList)

        //Проверим что создались абоненты
        checkAbonentsWithAbonentsList(newMsisdns, abonentsList)
        checkAbonentsListHidden(abonentsList)
    }

    @Story("web_communication_save_f")
    @Test(description = "Успешное изменение опроса: удаление ручных номеров")
    fun successCommunicationSaveDBSrcRemoveManual() {
        //Исходный список номеров
        val originMsisdns = arrayListOf<Long>(1000001,1000002,1000003)
        //Новый список номеров
        val newMsisdns = arrayListOf<Long>(1000004,1000005)

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(),
                msisdns = originMsisdns, params = JsonCommunication(db_src = "manual")))

        //Ищем опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)
        // Ищем схему опроса
        var communicationTemplate = EntitiesQuery.getCommunicationTemplate(NameCommunication)
        //Ищем список абонентов
        var abonentsList = EntitiesQuery.getAbonentsList(""""$NameCommunication"""")

        //Изменяем опрос
        EntitiesQuery.saveCommunication(adminUser, JsonCommunicationData(abonents_list_id = arrayListOf(abonentsList.id.value),
                params = JsonCommunication(db_src = "manual"), id = communication.id.value, msisdns = newMsisdns,
                communication_template_id = communicationTemplate?.id?.value))

        //Ищем опрос
        communication = EntitiesQuery.getCommunication(NameCommunication)

        //Проверяем что списки абонентов привязались к разговору
        abonentsList = EntitiesQuery.getAbonentsList(""""$NameCommunication"""")
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))
        checkAbonentsListHidden(abonentsList)

        //Проверим что создались абоненты
        checkAbonentsWithAbonentsList(newMsisdns, abonentsList)
        checkAbonentsListHidden(abonentsList)
    }
}