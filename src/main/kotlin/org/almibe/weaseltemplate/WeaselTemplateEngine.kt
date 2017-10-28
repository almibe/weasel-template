package org.almibe.weaseltemplate

import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

enum class TokenType {
    STRING,
    OPEN_CURLY,
    ESCAPED_OPEN_CURLY,
    CLOSE_CURLY,
    ESCAPED_CLOSE_CURLY,
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
    TEMPLATE_NAME,
    EOL
}

data class Token(val value: String, val tokenType: TokenType)

class WeaselTemplateEngine(private val classLoader: ClassLoader) {

    private val templateCache: MutableMap<String, Template> = mutableMapOf()

    fun processTemplate(templateName: String, data: JsonObject): String {
        return if (templateCache.containsKey(templateName)) {
            templateCache[templateName]?.applyTemplate(data) ?: throw RuntimeException("Error applying template $templateName")
        } else {
            val templateUrl = classLoader.getResource(templateName)
            val tokenStream = pathToStream(Paths.get(templateUrl.toURI()))
            val cache: List<Cache> = mutableListOf()
            tokenStream.forEach {
                //TODO fill cache list
            }
            val template = Template(templateName, cache)
            templateCache[templateName] = template
            template.applyTemplate(data)
        }
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

    private fun pathToStream(path: Path): Stream<Token> = Files.lines(path).flatMap { line: String ->
        val tokens = mutableListOf<Token>()
        line.forEach { char ->
            when (char) {
                //'{' ->
            }
        }
        tokens.stream()
    }
}
