package dbsteps.entities

import db.entity.entities.Abonents
import db.entity.entities.AbonentsLists
import db.query.EntitiesQuery
import dbsteps.BaseSteps
import entity.entities.JsonAbonents
import entity.entities.JsonAbonentsLists
import entity.entities.abonent_source_type
import entity.entities.abonent_state
import org.jetbrains.exposed.sql.transactions.transaction
import org.testng.annotations.AfterMethod
import org.testng.annotations.BeforeMethod

abstract class CommunicationSteps : BaseSteps() {

    lateinit var abonentsList: AbonentsLists
    lateinit var abonent: Abonents


    @BeforeMethod(description = "Генерим начальных абонентов")
    fun beforeMethodSteps() {
        abonentsList = EntitiesQuery.insertAbonentsList(adminClient, "Base AL",
                false, false, JsonAbonentsLists(), adminUser)
        abonent = EntitiesQuery.insertAbonent(abonentsList, 89818235391, JsonAbonents(), abonent_source_type.DB_LIST, abonent_state.UNCHECKED)
    }

    @AfterMethod(description = "Чистим базу от абонентов")
    fun afterMethodSteps(){
//        EntitiesQuery.clearDB()
    }
}