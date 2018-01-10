import db.DBUtil
import db.entity.external.Clients
import entity.external.JsonClient
import entity.external.JsonUser
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import utils.JsonUtils

fun main(args: Array<String>){
    var json = JsonUser("1", "2")


}