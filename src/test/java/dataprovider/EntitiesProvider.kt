package dataprovider

import db.DBUtil
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.JsonCommunication
import entity.entities.JsonCommunicationTemplate
import entity.entities.communication_status_type
import entity.logic.init_base_type_type
import entity.logic.msg_type_type
import entity.logic.talk_status
import org.testng.annotations.DataProvider

object EntitiesProvider {

    @DataProvider(name = "goodCommunications")
    @JvmStatic
    fun goodCommunications(): Array<Array<Any>> {
        val msisnds = arrayListOf<Long>(1000001, 1000002, 10000003)

        return arrayOf(
                arrayOf(JsonCommunication(db_src = "list"), communication_status_type.DRAFT, "Источник - Списки абонентов",
                        JsonCommunicationTemplate(), arrayListOf<Long>()),
                arrayOf(JsonCommunication(db_src = "manual"), communication_status_type.DRAFT, "Источник - Вручную",
                        JsonCommunicationTemplate(), msisnds),
                arrayOf(JsonCommunication(db_src = "api"), communication_status_type.DRAFT, "Источник - Api",
                        JsonCommunicationTemplate(), arrayListOf<Long>()))
    }

    @DataProvider(name = "goodTalks")
    @JvmStatic
    fun goodTalks(): Array<Array<Any>> {
        val trace = arrayOf<Int>(1)

        return arrayOf(
                arrayOf<Any>(trace, "4105", init_base_type_type.NORMAL, msg_type_type.sms,
                        talk_status.READY_FOR_START, false)
        )
    }

    @DataProvider(name = "startCommunicationWithNoDraftStatus")
    @JvmStatic
    fun startCommunicationWithNoDraftStatus(): Array<Array<Any>> {

        return arrayOf(
                arrayOf<Any>(communication_status_type.ACTIVE),
                arrayOf<Any>(communication_status_type.FINISHED),
                arrayOf<Any>(communication_status_type.DELETED),
                arrayOf<Any>(communication_status_type.STOPPED)
        )
    }

    @DataProvider(name = "stopCommunicationWithDifferentTalksStatus")
    @JvmStatic
    fun stopCommunicationWithDifferentTalksStatus(): Array<Array<Any>> {

        return arrayOf(
                arrayOf<Any>(talk_status.READY_FOR_START, talk_status.STOPPED),
                arrayOf<Any>(talk_status.PROCESSING, talk_status.STOPPED),
                arrayOf<Any>(talk_status.FINISHED, talk_status.FINISHED),
                arrayOf<Any>(talk_status.STOPPED, talk_status.STOPPED),
                arrayOf<Any>(talk_status.FINISHED_WRONG_ANSWER, talk_status.FINISHED_WRONG_ANSWER),
                arrayOf<Any>(talk_status.FINISHED_TIMEOUT, talk_status.FINISHED_TIMEOUT),
                arrayOf<Any>(talk_status.FINISHED_TALK_TIMEOUT, talk_status.FINISHED_TALK_TIMEOUT),
                arrayOf<Any>(talk_status.FINISHED_COMMUNICATION_TIMEOUT, talk_status.FINISHED_COMMUNICATION_TIMEOUT),
                arrayOf<Any>(talk_status.FINISHED_INVALID_MSISDN, talk_status.FINISHED_INVALID_MSISDN)
        )
    }

    @DataProvider(name = "stopCommunicationSeveralTalks")
    @JvmStatic
    fun stopCommunicationSeveralTalks(): Array<Array<Any>> {

        return arrayOf(
                arrayOf<Any>("NameCommunication 1", "NameCommunication 2", arrayListOf<Long>(1000001, 1000002))
        )
    }

    @DataProvider(name = "stopCommunicationWithDifferentCommunicationStatus")
    @JvmStatic
    fun stopCommunicationWithDifferentCommunicationStatus(): Array<Array<Any>> {

        return arrayOf(
                arrayOf<Any>(communication_status_type.STOPPED),
                arrayOf<Any>(communication_status_type.DELETED),
                arrayOf<Any>(communication_status_type.FINISHED),
                arrayOf<Any>(communication_status_type.DRAFT)
        )
    }

    @DataProvider(name = "copyCommunicationWithDifferentCommunicationStatus")
    @JvmStatic
    fun copyCommunicationWithDifferentCommunicationStatus(): Array<Array<Any>> {

        return arrayOf(
                arrayOf<Any>(communication_status_type.STOPPED),
                arrayOf<Any>(communication_status_type.DELETED),
                arrayOf<Any>(communication_status_type.FINISHED),
                arrayOf<Any>(communication_status_type.DRAFT),
                arrayOf<Any>(communication_status_type.ACTIVE)
        )
    }

    @DataProvider(name = "copyCommunicationWithDifferentCommunicationDate")
    @JvmStatic
    fun copyCommunicationWithDifferentCommunicationDate(): Array<Array<Any?>> {

        return arrayOf(
                arrayOf<Any?>(JsonCommunication(date_from = "2017-01-01 10:00:00", date_to = "2018-01-01 10:00:00"), null),
                arrayOf<Any?>(JsonCommunication(date_from = "2018-01-01 10:00:00", date_to = "2019-01-01 10:00:00"), null),
                arrayOf<Any?>(JsonCommunication(date_from = "2019-01-01 10:00:00", date_to = "2020-01-01 10:00:00"),
                        JsonCommunication(date_from = "2019-01-01 10:00:00", date_to = "2020-01-01 10:00:00"))
        )
    }
    @DataProvider(name = "deleteCommunicationWithNoDraftStatus")
    @JvmStatic
    fun deleteCommunicationWithNoDraftStatus(): Array<Array<Any>> {

        return arrayOf(
                arrayOf<Any>(communication_status_type.STOPPED),
                arrayOf<Any>(communication_status_type.DELETED),
                arrayOf<Any>(communication_status_type.FINISHED),
                arrayOf<Any>(communication_status_type.ACTIVE)
        )
    }



}