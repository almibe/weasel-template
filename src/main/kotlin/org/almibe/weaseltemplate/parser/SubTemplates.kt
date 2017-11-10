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

package org.almibe.weaseltemplate.parser

import com.google.gson.JsonObject

interface SubTemplate {
    fun apply(data: JsonObject, stringBuilder: StringBuilder)
}

data class TextSubTemplate(val content: String): SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        stringBuilder.append(content)
    }
}

data class ScalarSubTemplate(val selector: String): SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        val names = selector.split(".")
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

data class ConditionalSubTemplate(val conditionSelector: String, val content: List<SubTemplate>): SubTemplate {
    fun testCondition(data: JsonObject): Boolean {
        val names = conditionSelector.split(".")
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
        content.forEach { it.apply(data, stringBuilder) }
    }
}

data class ElseSubTemplate(val content: List<SubTemplate>) : SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        content.forEach { it.apply(data, stringBuilder) }
    }
}

data class EachSubTemplate(val listSelector: String, val iteratorName: String, val content: List<SubTemplate>): SubTemplate { // EachSubTemplate("users", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}

data class IncludeSubTemplate(val fileName: String, val argumentSelector: String? = null): SubTemplate { // IncludeSubTemplate("adminTemplate.wt", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        TODO()
    }
}
