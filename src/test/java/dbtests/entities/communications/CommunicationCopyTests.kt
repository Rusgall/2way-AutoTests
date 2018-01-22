package dbtests.entities.communications

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.annotations.Test
import java.text.SimpleDateFormat
import java.util.*

@Feature("ENTITIES")
class CommunicationCopyTests : CommunicationSteps() {
    @Story("web_communication_copy_f")
    @Test(description = "Успешное копирование опроса: разные статусы",
            dataProviderClass = EntitiesProvider::class, dataProvider = "copyCommunicationWithDifferentCommunicationStatus")
    fun successCopyCommunicationDifferentStatus(status: communication_status_type) {
        val copyNameCommunication = "copy-${NameCommunication}"

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))

        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)
        //Ставим статус опроса
        changeStatusCommunication(communication, status)

        //Копируем опрос
        val id = EntitiesQuery.copyCommunication(adminUser, communication)
        val copyCommunication = EntitiesQuery.getCommunication(copyNameCommunication)
        val copyCommunicationTemplate = EntitiesQuery.getCommunicationTemplate(copyNameCommunication)

        //Создаем новый json, с текущей датой для старта и с датой через год для окончания
        val date = Calendar.getInstance()
        date.time = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.setTimeZone(TimeZone.getTimeZone("GMT"))
        date.add(Calendar.YEAR, 1)
        val jsonCommunication = JsonCommunication(date_from = format.format(Date()),
                date_to = format.format(date.time))

        //Проверяем новый опрос
        checkCommunication(copyCommunication, id, adminClient, copyCommunicationTemplate, jsonCommunication,
                communication_status_type.DRAFT, copyNameCommunication, adminUser)
        //Проверяем схему нового опроса
        checkCommunicationTemplate(copyCommunication.id.value, adminClient, copyNameCommunication, JsonCommunicationTemplate(),
                adminUser, copyCommunicationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(alName)
        checkCommunicationAbonentsLists(copyCommunication, Collections.singletonList(abonentsList))
    }

    @Story("web_communication_copy_f")
    @Test(description = "Успешное копирование опроса: разная дата опроса",
            dataProviderClass = EntitiesProvider::class, dataProvider = "copyCommunicationWithDifferentCommunicationDate")
    fun successCopyCommunicationDifferentDate(params: JsonCommunication, copyParams: JsonCommunication?) {
        val copyNameCommunication = "copy-${NameCommunication}"

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = Collections.singletonList(abonentsList.id.value), params = params))

        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        //Копируем опрос
        val id = EntitiesQuery.copyCommunication(adminUser, communication)
        val copyCommunication = EntitiesQuery.getCommunication(copyNameCommunication)
        val copyCommunicationTemplate = EntitiesQuery.getCommunicationTemplate(copyNameCommunication)

        //Создаем новый json, с текущей датой для старта и с датой через год для окончания
        val date = Calendar.getInstance()
        date.time = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.setTimeZone(TimeZone.getTimeZone("GMT"))
        date.add(Calendar.YEAR, 1)
        val jsonCommunication = copyParams ?: JsonCommunication(date_from = format.format(Date()),
                date_to = format.format(date.time))

        //Проверяем новый опрос
        checkCommunication(copyCommunication, id, adminClient, copyCommunicationTemplate, jsonCommunication,
                communication_status_type.DRAFT, copyNameCommunication, adminUser)
        //Проверяем схему нового опроса
        checkCommunicationTemplate(copyCommunication.id.value, adminClient, copyNameCommunication, JsonCommunicationTemplate(),
                adminUser, copyCommunicationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(alName)
        checkCommunicationAbonentsLists(copyCommunication, Collections.singletonList(abonentsList))
    }

    @Story("web_communication_copy_f")
    @Test(description = "Успешное копирование опроса: разные источники абонентов",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodCommunications")
    fun successCopyCommunicationDifferentDBSource(params: JsonCommunication, status: communication_status_type,
                                                  name: String, jsonCommunicationTemplate: JsonCommunicationTemplate,
                                                  msisdns: List<Long>) {
        val copyNameCommunication = "copy-$name"
        //Корректируем список абонентов для опроса
        val abonents = arrayListOf<Int>()
        var abonentsListName = alName
        if (params.db_src == "list") {
            abonents.add(abonentsList.id.value) // если тип выбран лист, берем подготовленный список абонентов
        } else {
            abonentsListName = """"copy-$name"""" // иначе создастся новый список абонентов, с таким же названием как и у опроса
        }

        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = abonents, params = params,
                        schema = jsonCommunicationTemplate, name = name, msisdns = msisdns))

        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(name)

        //Копируем опрос
        val id = EntitiesQuery.copyCommunication(adminUser, communication)
        val copyCommunication = EntitiesQuery.getCommunication(copyNameCommunication)
        val copyCommunicationTemplate = EntitiesQuery.getCommunicationTemplate(copyNameCommunication)

        //Создаем новый json, с текущей датой для старта и с датой через год для окончания
        val date = Calendar.getInstance()
        date.time = Date()
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        format.setTimeZone(TimeZone.getTimeZone("GMT"))
        date.add(Calendar.YEAR, 1)
        val jsonCommunication = JsonCommunication(date_from = format.format(Date()),
                date_to = format.format(date.time), db_src = params.db_src)

        //Проверяем новый опрос
        checkCommunication(copyCommunication, id, adminClient, copyCommunicationTemplate, jsonCommunication,
                communication_status_type.DRAFT, copyNameCommunication, adminUser)
        //Проверяем схему нового опроса
        checkCommunicationTemplate(copyCommunication.id.value, adminClient, copyNameCommunication, JsonCommunicationTemplate(),
                adminUser, copyCommunicationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(abonentsListName)
        checkCommunicationAbonentsLists(copyCommunication, Collections.singletonList(abonentsList))

        //Если номера ввели вручную, проверим что создались абоненты
        if (params.db_src == "manual")
            checkAbonentsWithAbonentsList(msisdns, abonentsList)

        //Если номера ввели вручную или выбран api, проверим что список абонентов скрыт, но не удален
        if (params.db_src == "manual" || params.db_src == "api")
            checkAbonentsListHidden(abonentsList)
    }
}