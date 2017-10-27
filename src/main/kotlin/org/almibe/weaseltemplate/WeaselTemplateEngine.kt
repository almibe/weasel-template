package org.almibe.weaseltemplate

import com.google.gson.JsonObject
import java.io.File

enum class TokenType {
    STRING,
    OPEN_CURLY,
    CLOSE_CURLY,
    IF,
    ELSEIF,
    ELSE,
    CLOSE_IF,
    EACH,
    CLOSE_EACH,
    AS,
    REFERENCE,
    LOOP_NAME,
    KEY_NAME,
    VALUE_NAME,
    TEMPLATE_NAME
}

data class Token(val value: String, val tokenType: TokenType)

class WeaselTemplateLexer {

}

class WeaselTemplateParser(lexer: WeaselTemplateLexer) {

}

class WeaselTemplateEngine(private val classLoader: ClassLoader) {
    fun processTemplate(templateName: String, data: JsonObject): String {
        val templateUrl = classLoader.getResource(templateName)
        val templateFile = File(templateUrl.toURI())

        return ""
    }

    //rules
    fun string() {

    }

    fun conditional() {

    }

    fun listLoop() {

    }

    fun mapLoop() {

    }

    fun include() {

    }
}
