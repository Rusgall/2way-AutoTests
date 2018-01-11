package dbtests

import db.DBUtil
import db.entity.external.Clients
import db.entity.external.Roles
import db.entity.external.Users
import db.query.ExternalQuery
import dbsteps.BaseSteps
import entity.external.JsonUser
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import utils.adminClientName
import utils.adminLogin
import utils.adminRoleName

abstract class BaseTest {

    var adminClient : Clients? = null
    var adminRole : Roles? = null
    var adminUser : Users? = null

    @BeforeMethod(description = "Подготовка БД")
    fun beforeBaseTests(){
        DBUtil.clearDB()
        DBUtil.insertInitialData()

        initAdmin()
    }

    private fun initAdmin(){
        adminClient = ExternalQuery.getClient(adminClientName)
        adminRole = ExternalQuery.getRole(adminRoleName)
        adminUser = ExternalQuery.getUser(adminLogin)
    }

}