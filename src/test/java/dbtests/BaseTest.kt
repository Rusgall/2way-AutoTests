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

    lateinit var adminClient: Clients
    lateinit var adminRole: Roles
    lateinit var adminUser: Users

    @BeforeMethod(description = "Подготовка БД")
    fun beforeBaseTests() {
        DBUtil.clearDB()
        DBUtil.insertInitialData()

        initAdmin()
    }

    private fun initAdmin() {
        ExternalQuery.getClient(adminClientName).let {
            adminClient = it!!
        }

        ExternalQuery.getRole(adminRoleName).let {
            adminRole = it!!
        }
        ExternalQuery.getUser(adminLogin).let {
            adminUser = it!!
        }
    }

}