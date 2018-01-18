package entity.logic

import entity.entities.result_code_type

//class ResultStartCommunication(value : String){
//    val result = value.get(1) == 't'
//    val result_code = result_code_type.valueOf(value.substring(3, value.length-1))
//
//}

class ResultStartCommunication {

    var result = false
    var result_code = result_code_type.STUB
    constructor(value : String){
        result = value.get(1) == 't'
        result_code = result_code_type.valueOf(value.substring(3, value.length-1))
    }

    constructor(_result : Boolean, _result_code: result_code_type){
        result = _result
        result_code = _result_code
    }

    constructor()

    override fun equals(other: Any?): Boolean {
        val o = other as? ResultStartCommunication ?: return false

        return o.result == result && o.result_code == result_code
    }
}