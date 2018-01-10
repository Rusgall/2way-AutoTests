package utils

import java.io.FileInputStream
import java.util.*

object ResUtils {
    private var prop: Properties? = null

    init{
        prop = Properties()
        prop?.load(FileInputStream("${srcResourcesPath}config.properties"))
    }

    fun getProperties(properties:String):String{
        return prop?.getProperty(properties).toString()
    }
}