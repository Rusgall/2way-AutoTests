package dbtests.entities.communications

import dataprovider.EntitiesProvider
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.JsonCommunicationData
import entity.entities.NameCommunication
import entity.entities.communication_status_type
import io.qameta.allure.Feature
import io.qameta.allure.Story
import org.testng.Assert
import org.testng.annotations.Test
import java.util.*

@Feature("ENTITIES")
class CommunicationDeleteTests : CommunicationSteps(){
    @Story("web_communication_delete_f")
    @Test(description = "Удаление черновика опроса")
    fun successCommunicationDelete() {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = Collections.singletonList(abonentsList.id.value)))

        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        val idFromFun = EntitiesQuery.deleteCommunication(adminUser, communication)

        //Проверяем что опрос удален
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(idFromFun, communication.id.value)
        Assert.assertEquals(communication.status, communication_status_type.DELETED)
    }

    @Story("web_communication_delete_f")
    @Test(description = "Удаление не черновика опроса",
            dataProviderClass = EntitiesProvider::class, dataProvider = "deleteCommunicationWithNoDraftStatus")
    fun communicationDeleteNoDraft(status: communication_status_type) {
        //Создаем опрос
        EntitiesQuery.createCommunication(adminUser, JsonCommunicationData(
                abonents_list_id = Collections.singletonList(abonentsList.id.value)))

        //Ищем наш опрос
        var communication = EntitiesQuery.getCommunication(NameCommunication)

        //Меняем статус опроса
        changeStatusCommunication(communication, status)

        val idFromFun = EntitiesQuery.deleteCommunication(adminUser, communication)

        //Проверяем что опрос не удален
        communication = EntitiesQuery.getCommunication(NameCommunication)
        Assert.assertEquals(idFromFun, 0)
        Assert.assertEquals(communication.status, status)
    }
}