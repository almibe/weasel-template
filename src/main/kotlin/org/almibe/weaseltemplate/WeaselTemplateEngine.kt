/*
 * Copyright 2017 Alex Berry
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.almibe.weaseltemplate

import com.google.gson.JsonObject
import java.nio.file.Files
import java.nio.file.Paths

class WeaselTemplateEngine(private val classLoader: ClassLoader) {
    private val templateCache: MutableMap<String, NamedTemplate> = mutableMapOf()
    private val lexer = WeaselTemplateLexer()

    fun processTemplate(templateName: String, data: JsonObject): String {
        val template = templateCache[templateName]
        return if (template != null) {
            template.apply(data)
        } else {
            val path = Paths.get(classLoader.getResource(templateName).toURI())
            val lines = Files.lines(path)
            val tokens = lexer.tokenize(lines)
            val newTemplate = NamedTemplate(templateName, tokens)
            templateCache[templateName] = newTemplate
            newTemplate.apply(data)
        }
    }
}

data class NamedTemplate(val templateName: String, private val content: List<Token>) {
    fun apply(data: JsonObject): String {
        val stringBuilder = StringBuilder()
        val iterator = content.iterator()
        handleToken(iterator, data, stringBuilder)
        return stringBuilder.toString()
    }

    private fun handleToken(iterator: Iterator<Token>, data: JsonObject, stringBuilder: StringBuilder) {
        while(iterator.hasNext()) {
            val token = iterator.next()
            when (token) {
                is TextToken -> handleTextToken(token, stringBuilder)
                is ScalarToken -> handleScalarToken(token, stringBuilder, data)
                is IfToken -> handleIfToken(token, iterator, stringBuilder, data)
                is EachToken -> handleEachToken(token, iterator, stringBuilder, data)
                is IncludeToken -> handleIncludeToken(token, stringBuilder, data)
                else -> throw RuntimeException("Unexpected condition")
            }
        }
    }

    private fun handleTextToken(token: TextToken, stringBuilder: StringBuilder) {
        stringBuilder.append(token.content)
    }

    private fun handleScalarToken(token: ScalarToken, stringBuilder: StringBuilder, data: JsonObject) {
        val names = token.name.split(".")
        var current: JsonObject = data
        names.forEach {
            val element = current.get(it)
            if (element.isJsonObject) {
                current = element as JsonObject
            } else if (element.isJsonPrimitive && names.last() == it) {
                stringBuilder.append(element.asString)
            } else {
                throw RuntimeException("Unexpected value")
            }
        }
    }

    private fun handleIfToken(token: IfToken, iterator: Iterator<Token>, stringBuilder: StringBuilder, data: JsonObject) {
        if (testCondition(token.condition, data)) {
            while (iterator.hasNext()) {
                val nextToken = iterator.next()
                when (nextToken) {
                    is TextToken -> handleTextToken(nextToken, stringBuilder)
                    is ScalarToken -> handleScalarToken(nextToken, stringBuilder, data)
                    is IfToken -> handleIfToken(token, iterator, stringBuilder, data)
                    is EachToken -> handleEachToken(nextToken, iterator, stringBuilder, data)
                    is IncludeToken -> handleIncludeToken(nextToken, stringBuilder, data)
                    is ElseIfToken -> return readToEndIf(iterator)
                    is ElseToken -> return readToEndIf(iterator)
                    is EndIfToken -> return
                    else -> throw RuntimeException("Unexpected condition")
                }
            }
        } else {
            //TODO handle elseif cases
            //TODO handle else
            TODO()
        }
    }

    private fun readToEndIf(iterator: Iterator<Token>) {
        while (iterator.hasNext()) {
            val token = iterator.next()
            if (token is EndIfToken) {
                return
            }
        }
    }

    private fun testCondition(condition: String, data: JsonObject): Boolean {
        val names = condition.split(".")
        var current: JsonObject = data
        val itr = names.iterator()
        while (itr.hasNext()) {
            val currentName = itr.next()
            val element = current.get(currentName)
            if (itr.hasNext() && element.isJsonObject) {
                current = element as JsonObject
            } else return !element.isJsonObject
        }
        return false
    }

    private fun handleEachToken(token: EachToken, iterator: Iterator<Token>, stringBuilder: StringBuilder, data: JsonObject) {
        TODO()
    }

    private fun handleIncludeToken(token: IncludeToken, stringBuilder: StringBuilder, data: JsonObject) {
        TODO()
    }
}
