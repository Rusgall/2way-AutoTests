package entity.external

import com.google.gson.annotations.SerializedName
import entity.JsonEntity

data class ResendTimeout(var default: Int = 600, var modification: Boolean = true)

data class RecieveParams(var short_numbers: List<String>, var resend_timeout: ResendTimeout? = ResendTimeout())

data class SendParams(var short_numbers: List<String>, var resend_timeout: ResendTimeout? = ResendTimeout())

data class Viber(var send_params: SendParams = SendParams(arrayListOf("DigBrand")),
                 var receive_params: RecieveParams = RecieveParams(arrayListOf("DigBrand")),
                 var init_src_node_id: Int = 31052, var interactive_src_node_id: Int = 31052)

data class SMS(var send_params: SendParams = SendParams(arrayListOf("4105")),
               var receive_params: RecieveParams= RecieveParams(arrayListOf("4105", "2424")),
               var init_src_node_id: Int = 8875, var interactive_src_node_id: Int = 8876)

data class MsgTypes(var sms: SMS = SMS(), var viber: Viber = Viber())

data class SendingParams(var msg_types: MsgTypes = MsgTypes())

data class Brands(@SerializedName("95") var _95: String = "BEELINE", @SerializedName("120") var _120: String = "Megafon",
                  @SerializedName("125") var _125: String = "MTS", @SerializedName("158") var _158: String = "TELE2")

data class JsonClient(var brands: Brands = Brands(), var sending_params: SendingParams = SendingParams(),
                      var communication_type: List<String> = arrayListOf("active", "passive"),
                      var source_by_api_enabled: Boolean = true) : JsonEntity()

class JsonClientData(var name : String = "Client Name Default", var params:JsonClient = JsonClient()) : JsonEntity()

