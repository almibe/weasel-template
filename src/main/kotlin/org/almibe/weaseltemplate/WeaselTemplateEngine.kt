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
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Stream

class WeaselTemplateEngine(private val classLoader: ClassLoader) {
    private val templateCache: MutableMap<String, Template> = mutableMapOf()

    fun processTemplate(templateName: String, data: JsonObject): String {
        val template = templateCache[templateName]
        return if (template != null) {
            template.applyTemplate(data)
        } else {
            val templateUrl = classLoader.getResource(templateName)
            val tokenStream = pathToStream(Paths.get(templateUrl.toURI()))
            val cache: List<Cache> = mutableListOf()
            val iterator = tokenStream.iterator()
            while(iterator.hasNext()) {
                val token = iterator.next()
                when(token) {
                    //TODO fill cache list
                }
            }
            val template = Template(templateName, cache)
            templateCache[templateName] = template
            template.applyTemplate(data)
        }
    }

    //rules
    fun string() {

    }

    fun conditional() {

    }

    fun listLoop() {

    }

    fun mapLoop() {

    }

    fun include() {

    }

    private fun pathToStream(path: Path): Stream<Token> = Files.lines(path).flatMap { line: String ->
        val tokens = mutableListOf<Token>()
//        val iterator = line.toCharArray().iterator()
//        while(iterator.hasNext()) {
//            val char = iterator.nextChar()
//            tokens.addAll(when (char) {
//                '{' -> handleOpenCurly(iterator)
//                '}' -> handleCloseCurly(iterator)
//                else -> handleText(iterator)
//            })
//        }
        tokens.stream()
    }
}
