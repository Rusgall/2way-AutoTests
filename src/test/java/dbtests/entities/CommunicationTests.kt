package dbtests.entities

import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.JsonCommunicationData
import entity.entities.NameCommunication
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test

@Feature("Опрос")
class CommunicationTests : CommunicationSteps(){

    @Story("Создание")
    @Test(description = "Успешное создание опроса")
    fun createCommunicationTemplate(){
        val abonents = arrayListOf<Int>(abonentsList?.id.value)
        //Создаем опрос
        val idFromFun = EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(abonents_list_id = abonents))

        //Ищем наш опрос
        val communication = EntitiesQuery.getCommunication(NameCommunication)

        Assert.assertEquals(communication?.id?.value, idFromFun, "Функция вернула неверный id")
//        Assert.assertEquals(communication)

    }

}