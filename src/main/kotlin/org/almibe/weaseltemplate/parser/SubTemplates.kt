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

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import org.almibe.weaseltemplate.WeaselTemplateEngine

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
            if (element != null && element.isJsonObject) {
                current = element as JsonObject
            } else if (element != null && element.isJsonPrimitive && names.last() == it) {
                val content = element.asString
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;")
                        .replace("'", "&apos;")
                        .replace("\"", "&quot;")

                stringBuilder.append(content)
            } else {
                throw RuntimeException("Unexpected value $selector")
            }
        }
    }
}

data class IfElseBlockSubTemplate(val conditionTemplates: List<IfElseSubTemplate>, val elseTemplate: ElseSubTemplate?): SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        conditionTemplates.forEach { conditionTemplate ->
            if (conditionTemplate.testCondition(data)) {
                conditionTemplate.apply(data, stringBuilder)
                return
            }
        }
        elseTemplate?.apply(data, stringBuilder)
    }
}

interface ConditionalSubTemplate: SubTemplate

data class IfElseSubTemplate(val conditionSelector: String, val content: List<SubTemplate>): ConditionalSubTemplate {
    fun testCondition(data: JsonObject): Boolean {
        val names = conditionSelector.split(".")
        var current: JsonObject = data
        val itr = names.iterator()
        while (itr.hasNext()) {
            val currentName = itr.next()
            val element = current.get(currentName) ?: return false
            if (itr.hasNext() && element.isJsonObject) {
                current = element as JsonObject
            } else {
                return when {
                    element.isJsonObject -> true
                    element.isJsonArray -> true
                    element.isJsonNull -> false
                    element.isJsonPrimitive -> checkPrimitive(element as JsonPrimitive)
                    else -> throw RuntimeException("Unexpected value")
                }
            }
        }
        return false
    }
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        content.forEach { it.apply(data, stringBuilder) }
    }
    private fun checkPrimitive(value: JsonPrimitive): Boolean {
        return if (value.isBoolean) {
            value.asBoolean
        } else {
            true
        }
    }
}

data class ElseSubTemplate(val content: List<SubTemplate>) : ConditionalSubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        content.forEach { it.apply(data, stringBuilder) }
    }
}

data class EachSubTemplate(val listSelector: String, val iteratorName: String, val content: List<SubTemplate>): SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        val list = selectList(data)
        val dataCopy = data.deepCopy()
        list?.forEach { item ->
            dataCopy.add(iteratorName, item)
            content.forEach { it.apply(dataCopy, stringBuilder) }
        }
    }

    private fun selectList(data: JsonObject): JsonArray? {
        val names = listSelector.split(".")
        var current: JsonObject = data
        val itr = names.iterator()
        while (itr.hasNext()) {
            val currentName = itr.next()
            val element = current.get(currentName) ?: return null
            if (itr.hasNext() && element.isJsonObject) {
                current = element as JsonObject
            } else if (!itr.hasNext() && element is JsonArray) {
                return element
            } else {
                return null
            }
        }
        return null
    }
}

data class IncludeSubTemplate(val engine: WeaselTemplateEngine, val fileName: String, val argumentSelector: String? = null): SubTemplate {
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {
        val argument = selectArgument(data)
        if (argument != null) {
            stringBuilder.append(engine.processTemplate(fileName, argument))
        } else {
            stringBuilder.append(engine.processTemplate(fileName, JsonObject()))
        }
    }

    private fun selectArgument(data: JsonObject): JsonObject? {
        if (argumentSelector == null) {
            return null
        } else {
            val names = argumentSelector.split(".")
            var current: JsonObject = data
            val itr = names.iterator()
            while (itr.hasNext()) {
                val currentName = itr.next()
                val element = current.get(currentName) ?: return null
                if (itr.hasNext() && element.isJsonObject) {
                    current = element as JsonObject
                } else if (!itr.hasNext() && element is JsonObject) {
                    return element
                } else {
                    return null
                }
            }
            return null
        }
    }
}
