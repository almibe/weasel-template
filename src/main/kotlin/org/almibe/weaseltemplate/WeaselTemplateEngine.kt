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
import org.almibe.weaseltemplate.lexer.WeaselTemplateLexer
import org.almibe.weaseltemplate.parser.Template
import org.almibe.weaseltemplate.parser.WeaselTemplateParser
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class WeaselTemplateEngine(private val classLoader: ClassLoader) {
    private val templateCache: MutableMap<String, Template> = mutableMapOf()
    private val parser = WeaselTemplateParser(this)
    private val lexer = WeaselTemplateLexer()

    fun processTemplate(templateName: String, data: JsonObject): String {
        val template = templateCache[templateName]
        return if (template != null) {
            template.apply(data)
        } else {
            val stream: InputStream = classLoader.getResourceAsStream(templateName)
            val reader = BufferedReader(InputStreamReader(stream))
            val lines = reader.lines()
            val tokens = lexer.tokenize(lines)
            val templates = parser.parse(tokens)
            val newTemplate = Template(templateName, templates)
            templateCache[templateName] = newTemplate
            newTemplate.apply(data)
        }
    }

    fun clearCache() {
        templateCache.clear()
    }
}
