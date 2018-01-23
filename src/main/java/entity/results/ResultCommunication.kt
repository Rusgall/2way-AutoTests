package entity.results

import entity.entities.result_code_type

class ResultCommunication {

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
        val o = other as? ResultCommunication ?: return false

        return o.result == result && o.result_code == result_code
    }

    override fun toString(): String {
        return "[result: $result, result_code: $result_code]"
    }
}