package dbtests

import db.DBUtil
import db.entity.external.Clients
import db.entity.external.Roles
import db.entity.external.Users
import db.query.ExternalQuery
import org.testng.annotations.BeforeClass
import org.testng.annotations.BeforeMethod
import utils.adminClientName
import utils.adminLogin
import utils.adminRoleName

abstract class BaseTest {

    lateinit var adminClient: Clients
    lateinit var adminRole: Roles
    lateinit var adminUser: Users


    @BeforeClass(description = "Чистим всю БД")
    fun beforeClassBaseTests() {
        DBUtil.clearDB()
    }

    @BeforeMethod(description = "Подготовка БД")
    fun beforeMethodBaseTests() {
        ExternalQuery.clearDB()
        DBUtil.insertInitialUser()

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