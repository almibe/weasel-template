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
    private val parser = WeaselTemplateParser()

    fun processTemplate(templateName: String, data: JsonObject): String {
        val template = templateCache[templateName]
        return if (template != null) {
            template.apply(data)
        } else {
            val path = Paths.get(classLoader.getResource(templateName).toURI())
            val lines = Files.lines(path)
            val tokens = parser.parse(lines)
            val newTemplate = NamedTemplate(templateName, tokens)
            templateCache[templateName] = newTemplate
            newTemplate.apply(data)
        }
    }

    fun clearCache() {
        templateCache.clear()
    }
}

private data class NamedTemplate(val templateName: String, private val content: List<SubTemplate>) {
    fun apply(data: JsonObject): String {
        val stringBuilder = StringBuilder()
        val iterator = content.iterator()
        handleTemplates(iterator, data, stringBuilder)
        return stringBuilder.toString()
    }

    private fun handleTemplates(iterator: Iterator<SubTemplate>, data: JsonObject, stringBuilder: StringBuilder) {
        while(iterator.hasNext()) {
            val template = iterator.next()
            template.apply(data, stringBuilder)
        }
    }
}
