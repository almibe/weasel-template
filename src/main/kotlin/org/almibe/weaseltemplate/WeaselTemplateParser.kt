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

import org.almibe.weaseltemplate.lexer.*
import java.util.*

class WeaselTemplateParser {
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

    fun parse(tokens: List<Token>): List<SubTemplate> {
        val instanceValues = WeaselTemplateParser.ParserInstanceValues()
        val it = tokens.iterator()
        while(it.hasNext()) {
            val token = it.next()
            when(token) {
                is TextToken -> handleTextToken(token, instanceValues)
                is ScalarToken -> handleScalarToken(token, instanceValues)
                is IfToken -> handleIfToken(token, it, instanceValues)
                is EachToken -> handleEachToken(token, it, instanceValues)
                is IncludeToken -> handleIncludeToken(token, instanceValues)
                else -> throw RuntimeException("Unexpected token $token")
            }
        }
        return instanceValues.subTemplates
    }

    private fun handleTextToken(token: TextToken, instanceValues: ParserInstanceValues) {
        val tokenValue = instanceValues.consumed.toString()
        instanceValues.consumed.setLength(0) //clear
        if (tokenValue.isNotBlank()) {
            instanceValues.subTemplates.add(TextSubTemplate(tokenValue))
        }
    }

    private fun handleScalarToken(token: ScalarToken, instanceValues: ParserInstanceValues) {
        instanceValues.subTemplates.add(ScalarSubTemplate(token.selector))
    }

    private fun handleIfToken(token: IfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        TODO()
        //instanceValues.subTemplates.add(ConditionalSubTemplate(tagTokens.component2(), listOf()))
    }

    private fun handleEachToken(token: EachToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        TODO()
        //instanceValues.subTemplates.add(EachSubTemplate(tagTokens.component2(), tagTokens.component4()))
    }

    private fun handleIncludeToken(token: IncludeToken, instanceValues: ParserInstanceValues) {
        TODO()
    }
}
