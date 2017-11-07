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

interface Template {
    fun apply(data: JsonObject, stringBuilder: StringBuilder)
}

data class TextTemplate(val content: String): Template { // TextTemplate("hello")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        stringBuilder.append(content)
    }
}
data class ScalarTemplate(val name: String): Template { // ScalarTemplate("user.address.city")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
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
data class IfTemplate(val condition: String): Template { // IfTemplate("user.isAdmin")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class ElseIfTemplate(val condition: String): Template { // ElseIfTemplate("user.isAdmin")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
class ElseTemplate : Template { // ElseTemplate()
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class EachTemplate(val list: String, val iteratorName: String): Template { // EachTemplate("users", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class IncludeTemplate(val name: String, val argument: String? = null): Template { // IncludeTemplate("adminTemplate.wt", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}

/*
    private fun handleIfToken(token: IfTemplate, iterator: Iterator<Template>, stringBuilder: StringBuilder, data: JsonObject) {
//        if (testCondition(token.condition, data)) {
//            while (iterator.hasNext()) {
//                val nextToken = iterator.next()
//                when (nextToken) {
//                    is TextTemplate -> handleTextToken(nextToken, stringBuilder)
//                    is ScalarTemplate -> handleScalarToken(nextToken, stringBuilder, data)
//                    is IfTemplate -> handleIfToken(token, iterator, stringBuilder, data)
//                    is EachTemplate -> handleEachToken(nextToken, iterator, stringBuilder, data)
//                    is IncludeTemplate -> handleIncludeToken(nextToken, stringBuilder, data)
//                    is ElseIfTemplate -> return readToEndIf(iterator)
//                    is ElseTemplate -> return readToEndIf(iterator)
//                    is EndIfTemplate -> return
//                    else -> throw RuntimeException("Unexpected condition")
//                }
//            }
//        } else {
//            skip@ while (iterator.hasNext()) {
//                val nextToken = iterator.next()
//                when (nextToken) {
//                    is TextTemplate -> continue@skip
//                    is ScalarTemplate -> continue@skip
//                    is IfTemplate -> continue@skip
//                    is EachTemplate -> continue@skip
//                    is IncludeTemplate -> continue@skip
//                    is ElseIfTemplate -> handleElseIfToken()
//                    is ElseTemplate -> handleElseToken()
//                    else -> throw RuntimeException("Unexpected condition")
//                }
//            }
//        }
    }

    private fun readToEndIf(iterator: Iterator<Template>) {
//        while (iterator.hasNext()) {
//            val token = iterator.next()
//            if (token is EndIfTemplate) {
//                return
//            }
//        }
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

    private fun handleEachToken(token: EachTemplate, iterator: Iterator<Template>, stringBuilder: StringBuilder, data: JsonObject) {
        TODO()
    }

    private fun handleIncludeToken(token: IncludeTemplate, stringBuilder: StringBuilder, data: JsonObject) {
        TODO()
    }
*/
