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

import org.almibe.weaseltemplate.lexer.*
import java.util.*

class WeaselTemplateParser {

    private data class IfElseBlockSubTemplateBuilder(val ifElseSubTemplates: MutableList<IfElseSubTemplate> = mutableListOf(),
                                                     var elseSubTemplate: ElseSubTemplate? = null) {
        fun createSubTemplate(): IfElseBlockSubTemplate = IfElseBlockSubTemplate(ifElseSubTemplates, elseSubTemplate)
    }

    private interface ConditionalSubTemplateBuilder {
        fun createSubTemplate(): ConditionalSubTemplate
    }

    private data class IfElseSubTemplateBuilder(val conditionSelector: String,
                                                val content: MutableList<SubTemplate> = mutableListOf()): ConditionalSubTemplateBuilder {
        override fun createSubTemplate(): IfElseSubTemplate = IfElseSubTemplate(conditionSelector, content)
    }

    private data class ElseSubTemplateBuilder(val content: MutableList<SubTemplate> = mutableListOf()): ConditionalSubTemplateBuilder {
        override fun createSubTemplate(): ElseSubTemplate = ElseSubTemplate(content)
    }

    private data class EachSubTemplateBuilder(val listSelector: String,
                                              val itemName: String,
                                              val content: MutableList<SubTemplate> = mutableListOf()) {
        fun createSubTemplate(): EachSubTemplate = EachSubTemplate(listSelector, itemName, content)
    }

    private data class ParserInstanceValues(
            val subTemplates: MutableList<SubTemplate> = mutableListOf(),
            val lineNumber: Int = 0,
            val ifElseBlockSubTemplateBuilders: Deque<IfElseBlockSubTemplateBuilder> = ArrayDeque(),
            val conditionalSubTemplateBuilders: Deque<ConditionalSubTemplateBuilder> = ArrayDeque(),
            val eachSubTemplateBuilders: Deque<EachSubTemplateBuilder> = ArrayDeque()
    )

    fun parse(tokens: List<Token>): List<SubTemplate> {
        val instanceValues = ParserInstanceValues()
        val it = tokens.iterator()
        while(it.hasNext()) {
            val token = it.next()
            when(token) {
                is TextToken -> handleTextToken(token, instanceValues)
                is ScalarToken -> handleScalarToken(token, instanceValues)
                is IfToken -> handleIfToken(token, it, instanceValues)
                is ElseIfToken -> handleElseIfToken(token, it, instanceValues)
                is ElseToken -> handleElseToken(token, it, instanceValues)
                is EndIfToken -> handleEndIfToken(token, it, instanceValues)
                is EachToken -> handleEachToken(token, it, instanceValues)
                is EndEachToken -> handleEndEachToken(token, it, instanceValues)
                is IncludeToken -> handleIncludeToken(token, instanceValues)
                else -> throw RuntimeException("Unexpected token $token")
            }
        }
        return instanceValues.subTemplates
    }

    private fun handleTextToken(token: TextToken, instanceValues: ParserInstanceValues) {
        instanceValues.subTemplates.add(TextSubTemplate(token.content))
    }

    private fun handleScalarToken(token: ScalarToken, instanceValues: ParserInstanceValues) {
        instanceValues.subTemplates.add(ScalarSubTemplate(token.selector))
    }

    private fun handleIfToken(token: IfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val ifSubTemplateBuilder = IfElseBlockSubTemplateBuilder()
        instanceValues.ifElseBlockSubTemplateBuilders.push(ifSubTemplateBuilder)

        val conditionSubTemplateBuilder = IfElseSubTemplateBuilder(token.condition)
        instanceValues.conditionalSubTemplateBuilders.push(conditionSubTemplateBuilder)
    }

    private fun handleElseIfToken(token: ElseIfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousBuilder = instanceValues.conditionalSubTemplateBuilders.pop()
        val ifElseBlock = instanceValues.ifElseBlockSubTemplateBuilders.peekFirst()
        if(previousBuilder is IfElseSubTemplateBuilder) {
            ifElseBlock.ifElseSubTemplates.add(previousBuilder.createSubTemplate())
        } else {
            throw RuntimeException("Unexpected value")
        }

        val conditionSubTemplateBuilder = IfElseSubTemplateBuilder(token.condition)
        instanceValues.conditionalSubTemplateBuilders.push(conditionSubTemplateBuilder)
    }

    private fun handleElseToken(token: ElseToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousBuilder = instanceValues.conditionalSubTemplateBuilders.pop()
        val ifElseBlock = instanceValues.ifElseBlockSubTemplateBuilders.peekFirst()
        if(previousBuilder is IfElseSubTemplateBuilder) {
            ifElseBlock.ifElseSubTemplates.add(previousBuilder.createSubTemplate())
        } else {
            throw RuntimeException("Unexpected value")
        }

        val elseSubTemplateBuilder = ElseSubTemplateBuilder()
        instanceValues.conditionalSubTemplateBuilders.push(elseSubTemplateBuilder)
    }

    private fun handleEndIfToken(token: EndIfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousBuilder = instanceValues.conditionalSubTemplateBuilders.pop()
        val ifElseBlock = instanceValues.ifElseBlockSubTemplateBuilders.peekFirst()
        if(previousBuilder is IfElseSubTemplateBuilder) {
            ifElseBlock.ifElseSubTemplates.add(previousBuilder.createSubTemplate())
        } else {
            throw RuntimeException("Unexpected value")
        }

        val previousIfBuilder = instanceValues.ifElseBlockSubTemplateBuilders.pop()
        assert(previousIfBuilder is IfElseBlockSubTemplateBuilder)
        instanceValues.subTemplates.add(previousIfBuilder.createSubTemplate())
    }

    private fun handleEachToken(token: EachToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        //create and push each sub template builder
        TODO()
        //instanceValues.subTemplates.add(EachSubTemplate(tagTokens.component2(), tagTokens.component4()))
    }

    private fun handleEndEachToken(token: EndEachToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        //pop build and append previous each sub template builder
        TODO()
    }

    private fun handleIncludeToken(token: IncludeToken, instanceValues: ParserInstanceValues) {
        TODO()
    }
}
