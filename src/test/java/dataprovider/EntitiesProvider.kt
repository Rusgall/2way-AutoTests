package dataprovider

import db.DBUtil
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.JsonCommunication
import entity.entities.JsonCommunicationTemplate
import entity.entities.communication_status_type
import org.testng.annotations.DataProvider

object EntitiesProvider {

    @DataProvider(name = "goodCommunications")
    @JvmStatic
    fun goodCommunications(): Array<Array<Any>> {
        val msisnds = arrayListOf<Long>(1000001,1000002,10000003)

        return arrayOf(
                arrayOf(JsonCommunication(db_src = "list"),communication_status_type.DRAFT, "Источник - Списки абонентов",
                        JsonCommunicationTemplate(), arrayListOf<Long>()),
                arrayOf(JsonCommunication(db_src = "manual"),communication_status_type.DRAFT, "Источник - Вручную",
                        JsonCommunicationTemplate(), msisnds),
                arrayOf(JsonCommunication(db_src = "api"),communication_status_type.DRAFT, "Источник - Api",
                        JsonCommunicationTemplate(), arrayListOf<Long>()))
    }
}