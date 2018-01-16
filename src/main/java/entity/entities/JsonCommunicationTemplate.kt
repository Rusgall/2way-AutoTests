package entity.entities

import entity.JsonEntity

data class Viber(var content_type: String = "text", var text: String)

data class Sms(var text: String)

data class WrongMessage(var sms: Sms = Sms(WrongAnswer), var viber: Viber = Viber(text = WrongAnswer))

data class AnyAnswerMessage(var sms: Sms = Sms(AnswerMessage), var viber: Viber = Viber(text = AnswerMessage))

data class QuestionParams(var any_answer_message: AnyAnswerMessage = AnyAnswerMessage(), var max_any_count: Int = 1,
                          var wrong_message: WrongMessage = WrongMessage())

data class InitQuestion(var sms: Sms = Sms(InitalQuestion), var viber: Viber = Viber(text = InitalQuestion))

data class AnswerParams(var answer_time: Int = 1, var answer_period: String = "hour")

data class Params(var init_question: InitQuestion = InitQuestion(), var question_params: QuestionParams = QuestionParams(),
                  var answer_params: AnswerParams = AnswerParams())

data class Message(var sms: Sms, var viber: Viber)

data class CheckText(var type: String = "array", var contain_type: String = "contain",
                     var answer_text_array: List<String> = arrayListOf(Answer1))

data class Child(var type: String = "answer", var check_text: CheckText = CheckText(), var next: Next)

data class Next(var type: String, var message: Message, var question_params : QuestionParams?,
                var children: List<Child>?)

data class JsonCommunicationTemplate(var type: String = "active", var params: Params = Params(),
                                     var children: List<Child> = arrayListOf(ChildNextFinal, ChildNextQuestion)) : JsonEntity() {
    override fun toString(): String = super.toString()
}

//По умолчанию создается схема c двумя ветками
//Первая ветка: ответ-> финальное сообщение
//Вторая ветка: ответ-> вопрос -> ответ
val WrongAnswer = "Текст при превышении лимита неверных ответов"
val InitalQuestion = "Текст вопроса"
val AnswerMessage = "Текст при неверном ответе"
val FinalMessage = "Текст финального сообщения"
val Question = "Текст вопроса"
val Answer1 = "Ответ1"
val Answer2 = "Ответ2"

val FinalNextMessage = Message(sms = Sms(FinalMessage), viber = Viber(text = FinalMessage))
val QuestionNextMessage = Message(sms = Sms(Question), viber = Viber(text = Question))

val ChildNextFinal = Child(next = Next("final", FinalNextMessage, null, null))
val ChildNextQuestion = Child(next = Next("asnwer", QuestionNextMessage, QuestionParams(), arrayListOf(ChildNextFinal)),
        check_text = CheckText(answer_text_array = arrayListOf(Answer2)))
