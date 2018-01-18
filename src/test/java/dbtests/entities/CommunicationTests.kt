package dbtests.entities

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import db.query.LogicQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import entity.logic.ResultStartCommunication
import entity.logic.init_base_type_type
import entity.logic.msg_type_type
import entity.logic.talk_status
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

@Feature("ENTITIES")
class CommunicationTests : CommunicationSteps() {

    @Story("web_communication_add_f")
    @Test(description = "Успешное создание опроса",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodCommunications")
    fun createCommunication(jsonCommunication: JsonCommunication, status: communication_status_type,
                            name: String, jsonCommunicationTemplate: JsonCommunicationTemplate,
                            msisdns: List<Long>) {
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
        checkCommunicationTemplate(communication?.id?.value, adminClient, name, jsonCommunicationTemplate,
                adminUser, communcationTemplate)
        //Проверяем что списки абонентов привязались к разговору
        val abonentsList = EntitiesQuery.getAbonentsList(abonentsListName)
        checkCommunicationAbonentsLists(communication, Collections.singletonList(abonentsList))

        //Если номера ввели вручную, проверим что создались абоненты
        if (jsonCommunication.db_src == "manual")
            checkAbonentsWithAbonentsList(msisdns, abonentsList)

        //Если номера ввели вручную или выбран api, проверим что список абонентов скрыт, но не удален
        if (jsonCommunication.db_src == "manual" || jsonCommunication.db_src == "api")
            checkAbonentsListHidden(abonentsList)
    }

    @Story("web_communication_start_f")
    @Test(description = "Успешный запуск",
            dataProviderClass = EntitiesProvider::class, dataProvider = "goodTalks" )
    fun succsessStartCommunication(trace:Array<Int>, receiveSn:String, ibs:init_base_type_type,
                                   mt:msg_type_type, talkStatus: talk_status, hasAnswer:Boolean) {
        //Создаем опрос
        val idFromFun = EntitiesQuery.createCommunication(adminUser,
                JsonCommunicationData(abonents_list_id = Collections.singletonList(abonentsList.id.value)))
        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        //Запускаем опрос
        val result = EntitiesQuery.startCommunication(adminUser, communication)

        //Проверяем результат функции
        checkResultSartCommunication(result, ResultStartCommunication(true, result_code_type.NO_ERROR))

        //Ищем разговоры
        val talks = LogicQuery.getTalks(communication)

        //Проверяем что создан 1 разговор
        Assert.assertEquals(talks.size, 1, "Было создано 0 или > 1 разговора")
        checkCreateTalks(communication, abonent, msisdnDefault, JsonCommunicationTemplate(), trace, receiveSn, ibs, mt,
                talkStatus, hasAnswer, talks.first())

    }

}