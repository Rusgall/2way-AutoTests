package entity.entities

import entity.JsonEntity
import utils.adminLogin

data class MsgType(var type: String, var sender_sn: String, var receive_sn: String, var resend_timeout: Int?)

data class AbonentsSettings(var substitutions: List<String> = ArrayList<String>())

data class JsonCommunication(var date_to: String = "2018-01-01 00:00:00", var date_from: String = "2019-01-01 00:00:00",
                             var start_hour: String = "00:00:00", var end_hour: String = "00:00:00",
                             var db_src: String = "list", var msg_types: List<MsgType> = arrayListOf(SmsMsgType, ViberMsgType),
                             var init_base_type: String = "normal", var speed_per_minute: Int = 100,
                             var abonents_settings: AbonentsSettings = AbonentsSettings(),
                             var created_user_name: String = adminLogin) : JsonEntity() {
    override fun toString() = super.toString()
}

class JsonCommunicationData(var status: Any? = null, var name: String = NameCommunication,
                            var abonents_list_id: List<Int>, var communication_template_id: Any? = null,
                            var params: JsonCommunication = JsonCommunication(), var client_id: Any? = null,
                            var abonents_lists: List<Any> = arrayListOf(), var id: Any? = null,
                            var msisdns: List<Long> = arrayListOf(),
                            var schema: JsonCommunicationTemplate = JsonCommunicationTemplate()) : JsonEntity()

val NameCommunication = "Name Communication"
val SmsMsgType = MsgType("sms", "4105","4105", null)
val ViberMsgType = MsgType("viber", "DigBrand","DigBrand", 600)