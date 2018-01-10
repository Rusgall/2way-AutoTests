package dataprovider

import org.testng.annotations.DataProvider
import utils.*

object ExternalProvider {

    @DataProvider(name = "failAuth")
    @JvmStatic
    fun failAuth() : Array<Array<Any>>{
        val notFoundUser : Array<Any> = arrayOf(adminIncorrectLogin, adminPass, "No such user: login=$adminIncorrectLogin")
        val userBlocked : Array<Any> = arrayOf(blockedLogin, blockedPass, "User is blocked: login=$blockedLogin")
        val incorrectPassword : Array<Any> = arrayOf(adminLogin, adminIncorrectPass, "Incorrect password")

        return arrayOf(notFoundUser, userBlocked, incorrectPassword)
    }

}