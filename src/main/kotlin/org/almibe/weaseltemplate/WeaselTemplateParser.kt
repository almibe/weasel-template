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

import java.util.*
import java.util.stream.Stream

class WeaselTemplateParser {
    private val specialCharacter = '?'

    private data class IfSubTemplateBuilder(val conditionalSubTemplates: MutableList<ConditionalSubTemplate>,
                                            var elseSubTemplate: ElseSubTemplate?) {
        fun createIfTemplate(): IfSubTemplate = IfSubTemplate(conditionalSubTemplates, elseSubTemplate)
    }

    private data class ConditionalSubTemplateBuilder(val conditionSelector: String,
                                                     val content: MutableList<SubTemplate>) {
        fun createConditionalTemplate(): ConditionalSubTemplate = ConditionalSubTemplate(conditionSelector, content)
    }

    private data class ElseSubTemplateBuilder(val content: MutableList<SubTemplate>) {
        fun createElseTemplate(): ElseSubTemplate = ElseSubTemplate(content)
    }

    private data class ParserInstanceValues(
        val consumed: StringBuilder = StringBuilder(),
        val subTemplates: MutableList<SubTemplate> = mutableListOf(),
        val lineNumber: Int = 0,
        val ifTemplateBuilders: Deque<IfSubTemplateBuilder> = ArrayDeque()
    )

    fun parse(lines: Stream<String>): List<SubTemplate> {
        val instanceValues = WeaselTemplateParser.ParserInstanceValues()
        lines.forEach { line: String ->
            val iterator = line.toCharArray().iterator()
            while (iterator.hasNext()) {
                val nextCharacter = iterator.nextChar()
                if (nextCharacter == '<') {
                    checkTag(iterator, instanceValues)
                } else {
                    instanceValues.consumed.append(nextCharacter)
                }
            }
            instanceValues.consumed.append("\n")
        }
        createTextToken(instanceValues) //create a text token with remaining value
        return instanceValues.subTemplates
    }

    private fun checkTag(iterator: CharIterator, instanceValues: ParserInstanceValues) {
        if (iterator.hasNext()) {
            val nextCharacter = iterator.nextChar()
            if (nextCharacter == specialCharacter) {
                if (instanceValues.consumed.isNotEmpty()) { //if there is already a text token being build up add it
                    createTextToken(instanceValues)
                }
                readWeaselTemplateTag(iterator, instanceValues)
            } else {
                instanceValues.consumed.append("<$nextCharacter")
            }
        } else {
            throw RuntimeException("Invalid starting tag at end of file.")
        }
    }

    private fun readWeaselTemplateTag(iterator: CharIterator, instanceValues: ParserInstanceValues) {
        val tagTokens: List<String> = splitTagText(iterator, instanceValues)
        when (tagTokens.first()) {
            "if" -> handleIfToken(tagTokens, instanceValues)
            "elseif" -> handleElseIfToken(tagTokens, instanceValues)
            "else" -> handleElseToken(tagTokens, instanceValues)
            "include" -> handleIncludeToken(tagTokens, instanceValues)
            "each" -> handleEachToken(tagTokens, instanceValues)
            "end" -> handleEndToken(tagTokens, instanceValues)
            else -> handleScalarToken(tagTokens, instanceValues)
        }
    }

    /**
    * Returns a list of strings that represent current tag.
    */
    private fun splitTagText(iterator: CharIterator, instanceValues: ParserInstanceValues): List<String> {
        while (iterator.hasNext()) {
            val nextToken = iterator.nextChar()
            if (nextToken != '>') {
                instanceValues.consumed.append(nextToken)
            } else {
                break
            }
        }
        val tagText = instanceValues.consumed.toString()
        instanceValues.consumed.setLength(0)
        val resultList = tagText.split(' ')
        if (resultList.isEmpty()) {
            throw RuntimeException("Empty tag on line ${instanceValues.lineNumber}")
        } else {
            return resultList
        }
    }

    private fun createTextToken(instanceValues: ParserInstanceValues) {
        val tokenValue = instanceValues.consumed.toString()
        instanceValues.consumed.setLength(0) //clear
        if (tokenValue.isNotBlank()) {
            instanceValues.subTemplates.add(TextSubTemplate(tokenValue))
        }
    }

    private fun handleScalarToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.size == 1)
        instanceValues.subTemplates.add(ScalarSubTemplate(tagTokens.first()))
    }

    private fun handleIfToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.first() == "if")
        assert(tagTokens.size == 2)
        instanceValues.subTemplates.add(ConditionalSubTemplate(tagTokens.component2()))
    }

    private fun handleElseIfToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.first() == "elseif")
        assert(tagTokens.size == 2)
        instanceValues.subTemplates.add(ConditionalSubTemplate(tagTokens.component2()))
    }

    private fun handleElseToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.first() == "else")
        assert(tagTokens.size == 1)
        instanceValues.subTemplates.add(ElseSubTemplate())
    }

    private fun handleIncludeToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.first() == "include")
        assert(tagTokens.size == 2 || tagTokens.size == 3)
        when (tagTokens.size) {
            2 -> instanceValues.subTemplates.add(IncludeSubTemplate(tagTokens.component2()))
            3 -> instanceValues.subTemplates.add(IncludeSubTemplate(tagTokens.component2(), tagTokens.component3()))
            else -> RuntimeException("Unexpected value")
        }
    }

    private fun handleEachToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.first() == "each")
        assert(tagTokens.component3() == "as")
        assert(tagTokens.size == 4)
        instanceValues.subTemplates.add(EachSubTemplate(tagTokens.component2(), tagTokens.component4()))
    }

    private fun handleEndToken(tagTokens: List<String>, instanceValues: ParserInstanceValues) {
        assert(tagTokens.first() == "end")
        assert(tagTokens.component2() == "if" || tagTokens.component2() == "each")
        assert(tagTokens.size == 2)
        when (tagTokens.component2()) {
//            "if" -> instanceValues.subTemplates.add(EndIfTemplate())
//            "each" -> instanceValues.subTemplates.add(EndEachTemplate())
            else -> RuntimeException("Unexpected value")
        }
    }
}
