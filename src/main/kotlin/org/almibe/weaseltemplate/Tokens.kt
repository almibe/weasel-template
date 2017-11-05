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

interface Token

data class TextToken(val content: String): Token
/**
 * A ScalarTemplate is passed a namespaced name and uses that name to access a scala value from the data JSONObject.
 */
data class ScalarToken(private val name: String): Token
data class IfToken(val condition: String): Token
data class ElseIfToken(val condition: String): Token
class ElseToken: Token
class EndIfToken: Token
data class EachToken(val list: String, val iteratorName: String): Token
class EndEachToken: Token
data class IncludeToken(val name: String): Token
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
