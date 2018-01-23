package dataprovider

import db.DBUtil
import db.query.EntitiesQuery
import dbsteps.entities.CommunicationSteps
import entity.entities.*
import entity.logic.init_base_type_type
import entity.logic.msg_type_type
import entity.logic.talk_status
import org.testng.annotations.DataProvider

object EntitiesProvider {

    @DataProvider(name = "goodCommunications")
    @JvmStatic
    fun goodCommunications(): Array<Array<Any?>> {
        val msisnds = arrayListOf<Long>(1000001, 1000002, 10000003)

        return arrayOf(
                arrayOf<Any?>(JsonCommunication(db_src = "list"), communication_status_type.DRAFT, "Источник - Списки абонентов",
                        JsonCommunicationTemplate(), null),
                arrayOf<Any?>(JsonCommunication(db_src = "manual"), communication_status_type.DRAFT, "Источник - Вручную",
                        JsonCommunicationTemplate(), msisnds),
                arrayOf<Any?>(JsonCommunication(db_src = "api"), communication_status_type.DRAFT, "Источник - Api",
                        JsonCommunicationTemplate(), null))
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

    @DataProvider(name = "saveCommunications")
    @JvmStatic
    fun saveCommunications(): Array<Array<Any>> {
        val msgType = MsgType("viber", "2105", "1105", 600)
        val jsonCommunication = JsonCommunication(date_from = "2019-11-11 11:11:11", date_to = "2020-02-02 11:20:20",
                start_hour = "11:11:11", end_hour = "22:22:22", db_src = "list", msg_types = arrayListOf(msgType),
                abonents_settings = AbonentsSettings(arrayListOf("test")), speed_per_minute = 300)

        val WrongAnswer = "Новый Текст при превышении лимита неверных ответов"
        val InitalQuestion = "Новый Текст вопроса"
        val AnswerMessage = "Новый Текст при неверном ответе"
        val FinalMessage = "Новый Текст финального сообщения"
        val Question = "Новый Текст вопроса"
        val Answer1 = "Новый Ответ1"
        val Answer2 = "Новый Ответ2"

        val FinalNextMessage = Message(sms = Sms(FinalMessage), viber = Viber(text = FinalMessage))
        val QuestionNextMessage = Message(sms = Sms(Question), viber = Viber(text = Question))

        val ChildNextFinal = Child(next = Next("final", FinalNextMessage, null, null),
                check_text = CheckText(answer_text_array = arrayListOf(Answer1)))
        val ChildNextQuestion = Child(next = Next("asnwer", QuestionNextMessage, QuestionParams(), arrayListOf(ChildNextFinal)),
                check_text = CheckText(answer_text_array = arrayListOf(Answer2)))

        val jsonCommunicationTemplate = JsonCommunicationTemplate(params = Params(
                init_question = InitQuestion(Sms(InitalQuestion), viber = Viber(text = InitalQuestion)),
                question_params = QuestionParams(AnyAnswerMessage(Sms(AnswerMessage), Viber(text = AnswerMessage)),
                        wrong_message = WrongMessage(sms = Sms(WrongAnswer), viber = Viber(text = "WrongAnswer"))),
                answer_params = AnswerParams(answer_time = 2, answer_period = "min")), children = arrayListOf(
                ChildNextFinal, ChildNextQuestion))

        return arrayOf(
                arrayOf<Any>(jsonCommunication, jsonCommunicationTemplate, "Save Communication 1")
        )
    }

    @DataProvider(name = "saveCommunicationsFromList")
    @JvmStatic
    fun saveCommunicationsFromList(): Array<Array<Any?>> {
        return arrayOf(
                arrayOf<Any?>("manual", arrayListOf<Long>(1000001, 1000002, 1000003)),
                arrayOf<Any?>("api", null)
        )
    }
}
