package dbsteps.external

import db.query.ExternalQuery
import dbsteps.BaseSteps
import io.qameta.allure.Step

abstract class ClientSteps : BaseSteps() {

    @Step("Удаляем клиента")
    fun deleteClient(id: Int) : Boolean? {
        return ExternalQuery.deleteClient(id)
    }

}