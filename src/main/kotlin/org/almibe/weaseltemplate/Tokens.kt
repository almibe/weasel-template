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

interface Token {
    fun apply(data: JsonObject, stringBuilder: StringBuilder)
}
data class TextToken(val content: String): Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        stringBuilder.append(content)
    }
}
/**
 * A ScalarTemplate is passed a namespaced name and uses that name to access a scala value from the data JSONObject.
 */
data class ScalarToken(private val name: String): Token {
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
data class IfToken(val condition: String): Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class ElseIfToken(val condition: String): Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
class ElseToken: Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
class EndIfToken: Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class EachToken(val list: String, val iteratorName: String): Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
class EndEachToken: Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class IncludeToken(val name: String): Token {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
//
//class IfTemplate(private val templates: List<ConditionTemplate>, private val elseTemplate: ElseTemplate?): PartialTemplate {
//    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
//        templates.forEach {
//            if(it.testCondition(data)) {
//                it.appendResult(data, stringBuilder)
//                return
//            }
//        }
//        elseTemplate?.appendResult(data, stringBuilder)
//    }
//}
//
//class ConditionTemplate(private val condition: String, private val content: List<PartialTemplate>): PartialTemplate {
//    fun testCondition(data: JsonObject): Boolean {
//        val names = condition.split(".")
//        var current: JsonObject = data
//        val itr = names.iterator()
//        while (itr.hasNext()) {
//            val currentName = itr.next()
//            val element = current.get(currentName)
//            if (itr.hasNext() && element.isJsonObject) {
//                current = element as JsonObject
//            } else return !element.isJsonObject
//        }
//        return false
//    }
//
//    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
//class ElseTemplate(private val content: List<PartialTemplate>): PartialTemplate {
//    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
//class ListLoop(val reference: List<String>, val variableName: String, val content: List<PartialTemplate>): PartialTemplate {
//    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
//class MapLoop(val reference: List<String>, val keyName: String, val valueName: String, val content: List<PartialTemplate>): PartialTemplate {
//    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
//
//class Include(val reference: List<String>, val content: List<PartialTemplate>): PartialTemplate {
//    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }
//}
