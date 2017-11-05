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

interface Token

data class TextToken(val content: String): Token
data class ScalaToken(val name: String): Token
data class IfToken(val condition: String): Token
data class ElseIfToken(val condition: String): Token
class ElseToken: Token
class EndIfToken: Token
data class EachListToken(val list: String, val iteratorName: String): Token
data class EachMapToken(val map: String, val keyName: String, val valueName: String): Token
class EndEachToken: Token
data class IncludeToken(val name: String): Token

interface PartialTemplate {
    fun appendResult(data: JsonObject, stringBuilder: StringBuilder)
}

/**
 * A TextTemplate simply represents an uninterpreted block of text.  The data object isn't used
 * in the appendResult method at all.
 */
class TextTemplate(private val textContent: String): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        stringBuilder.append(textContent)
    }
}

/**
 * A ScalarTemplate is passed a namespaced name and uses that name to access a scala value from the data JSONObject.
 */
class ScalarTemplate(private val name: String): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        val names = name.split(".")
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
}

class IfTemplate(private val templates: List<ConditionTemplate>, private val elseTemplate: ElseTemplate?): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        templates.forEach {
            if(it.testCondition(data)) {
                it.appendResult(data, stringBuilder)
                return
            }
        }
        elseTemplate?.appendResult(data, stringBuilder)
    }
}

class ConditionTemplate(private val condition: String, private val content: List<PartialTemplate>): PartialTemplate {
    fun testCondition(data: JsonObject): Boolean {
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

    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ElseTemplate(private val content: List<PartialTemplate>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class ListLoop(val reference: List<String>, val variableName: String, val content: List<PartialTemplate>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class MapLoop(val reference: List<String>, val keyName: String, val valueName: String, val content: List<PartialTemplate>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class Include(val reference: List<String>, val content: List<PartialTemplate>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

/**
 * A NamedTemplate is template made up of multiple PartialTemplates and given a name for referencing in the cache.
 */
data class NamedTemplate(val templateName: String, private val content: List<PartialTemplate>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        content.forEach { currentTemplate ->
            currentTemplate.appendResult(data, stringBuilder)
        }
    }
}
