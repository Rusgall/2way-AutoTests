package dbtests.entities

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

@Feature("Опрос")
class CommunicationTests : CommunicationSteps() {

    @Story("Создание")
    @Test(description = "Успешное создание опроса",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodCommunications")
    fun createCommunicationTemplate(jsonCommunication: JsonCommunication, status: communication_status_type,
                                    name: String, jsonCommunicationTemplate: JsonCommunicationTemplate,
                                    msisdns:List<Long>) {
        //Корректируем список абонентов для опроса
        val abonents = arrayListOf<Int>()
        var abonentsListName = alName
        if (jsonCommunication.db_src == "list"){
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
        checkCommunicationTemplate(communication?.id?.value, adminClient, name, jsonCommunicationTemplate,
                adminUser, communcationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(abonentsListName)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))

        //Если номера ввели вручную, проверим что создались абоненты
        if(jsonCommunication.db_src == "manual")
    }

}