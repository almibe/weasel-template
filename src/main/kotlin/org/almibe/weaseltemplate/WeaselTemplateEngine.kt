package org.almibe.weaseltemplate

import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
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
        val template = templateCache[templateName]
        return if (template != null) {
            template.applyTemplate(data)
        } else {
            val templateUrl = classLoader.getResource(templateName)
            val tokenStream = pathToStream(Paths.get(templateUrl.toURI()))
            val cache: List<Cache> = mutableListOf()
            val iterator = tokenStream.iterator()
            while(iterator.hasNext()) {
                val token = iterator.next()
                when(token) {
                    //TODO fill cache list
                }
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

    val lookAhead: Queue<Char> = LinkedList<Char>()

    private fun CharIterator.consume(): Char? {
        //TODO handle checking look ahead
        return if(this.hasNext()) {
            nextChar()
        } else {
            null
        }
    }

    private fun CharIterator.ignoreWS(): Char? {
        TODO("Get next non white space character or return null")
    }

    private fun CharIterator.consume(testString: String): Boolean {
        TODO("check if testString is the next string available")
    }

    private fun pathToStream(path: Path): Stream<Token> = Files.lines(path).flatMap { line: String ->
        val tokens = mutableListOf<Token>()
        val iterator = line.toCharArray().iterator()
        while(iterator.hasNext()) {
            val char = iterator.nextChar()
            tokens.addAll(when (char) {
                '{' -> handleOpenCurly(iterator)
                '}' -> handleCloseCurly(iterator)
                else -> handleText(iterator)
            })
        }
        tokens.stream()
    }

    private fun handleOpenCurly(iterator: CharIterator): List<Token> {
        if (iterator.hasNext()) {
            val char = iterator.ignoreWS()
            when (char) {
                '{' -> return listOf(Token("{", TokenType.ESCAPED_OPEN_CURLY))
                'i' -> return handleIfOrReference(iterator)
                'e' -> return handleEachOrReference(iterator)
                else -> return handleReference(iterator)
            }
        } else {
            throw RuntimeException("Error parsing template")
        }
    }

    private fun handleCloseCurly(iterator: CharIterator): List<Token> {
        TODO()
    }

    private fun handleText(iterator: CharIterator): List<Token> {
        TODO()
    }

    private fun handleIfOrReference(iterator: CharIterator): List<Token> {
        TODO()
    }

    private fun handleEachOrReference(iterator: CharIterator): List<Token> {
        TODO()
    }

    private fun handleReference(iterator: CharIterator): List<Token> {
        TODO()
    }
}
