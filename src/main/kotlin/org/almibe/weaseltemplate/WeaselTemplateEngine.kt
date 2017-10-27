package org.almibe.weaseltemplate

import com.google.gson.JsonObject
import java.io.File

class WeaselTemplateEngine(private val classLoader: ClassLoader) {
    fun processTemplate(templateName: String, data: JsonObject): String {
        val templateUrl = classLoader.getResource(templateName)
        val templateFile = File(templateUrl.toURI())

        return ""
    }
}
