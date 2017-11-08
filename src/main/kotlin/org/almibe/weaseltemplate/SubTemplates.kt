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

interface SubTemplate {
    fun apply(data: JsonObject, stringBuilder: StringBuilder)
}

data class TextSubTemplate(val content: String): SubTemplate { // TextSubTemplate("hello")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        stringBuilder.append(content)
    }
}
data class ScalarSubTemplate(val name: String): SubTemplate { // ScalarSubTemplate("user.address.city")
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
data class IfSubTemplate(val conditionTemplates: List<ConditionalSubTemplate>, val elseTemplate: ElseSubTemplate?): SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        conditionTemplates.forEach { conditionTemplate ->
            if (conditionTemplate.testCondition(data)) {
                conditionTemplate.apply(data, stringBuilder)
                return
            } else {
                elseTemplate?.apply(data, stringBuilder)
            }
        }
    }
}
data class ConditionalSubTemplate(val condition: String): SubTemplate { // ConditionalSubTemplate("user.isAdmin")
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
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
class ElseSubTemplate : SubTemplate { // ElseSubTemplate()
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class EachSubTemplate(val list: String, val iteratorName: String): SubTemplate { // EachSubTemplate("users", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
data class IncludeSubTemplate(val name: String, val argument: String? = null): SubTemplate { // IncludeSubTemplate("adminTemplate.wt", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
