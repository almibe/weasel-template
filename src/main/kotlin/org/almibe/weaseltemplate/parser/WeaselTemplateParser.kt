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

    private interface SubTemplateBuilder {
        fun createSubTemplate(): SubTemplate
    }

    private data class IfElseBlockSubTemplateBuilder(val ifElseSubTemplates: MutableList<IfElseSubTemplate> = mutableListOf(),
                                                     var elseSubTemplate: ElseSubTemplate? = null): SubTemplateBuilder {
        override fun createSubTemplate(): IfElseBlockSubTemplate = IfElseBlockSubTemplate(ifElseSubTemplates, elseSubTemplate)
    }

    private data class IfElseSubTemplateBuilder(val conditionSelector: String,
                                                val content: MutableList<SubTemplate> = mutableListOf()): SubTemplateBuilder {
        override fun createSubTemplate(): IfElseSubTemplate = IfElseSubTemplate(conditionSelector, content)
    }

    private data class ElseSubTemplateBuilder(val content: MutableList<SubTemplate> = mutableListOf()): SubTemplateBuilder {
        override fun createSubTemplate(): ElseSubTemplate = ElseSubTemplate(content)
    }

    private data class EachSubTemplateBuilder(val listSelector: String,
                                              val itemName: String,
                                              val content: MutableList<SubTemplate> = mutableListOf()): SubTemplateBuilder {
        override fun createSubTemplate(): EachSubTemplate = EachSubTemplate(listSelector, itemName, content)
    }

    private data class ParserInstanceValues(
        val subTemplates: MutableList<SubTemplate> = mutableListOf(),
        val lineNumber: Int = 0,
        val subTemplateBuilders: Deque<SubTemplateBuilder> = ArrayDeque(),
        val ifElseBlockSubTemplateBuilders: Deque<IfElseBlockSubTemplateBuilder> = ArrayDeque()
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

    private fun appendSubTemplate(instanceValues: ParserInstanceValues, subTemplate: SubTemplate) {
        val currentState = instanceValues.subTemplateBuilders.peekFirst()
        when (currentState) {
            is IfElseSubTemplateBuilder -> currentState.content.add(subTemplate)
            is ElseSubTemplateBuilder -> currentState.content.add(subTemplate)
            null -> instanceValues.subTemplates.add(subTemplate)
            else -> throw RuntimeException("Unexpected value")
        }
    }

    private fun handleTextToken(token: TextToken, instanceValues: ParserInstanceValues) {
        val textSubTemplate = TextSubTemplate(token.content)
        appendSubTemplate(instanceValues, textSubTemplate)
    }

    private fun handleScalarToken(token: ScalarToken, instanceValues: ParserInstanceValues) {
        val scalarSubTemplate = ScalarSubTemplate(token.selector)
        appendSubTemplate(instanceValues, scalarSubTemplate)
    }

    private fun handleIfToken(token: IfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val ifSubTemplateBuilder = IfElseBlockSubTemplateBuilder()
        instanceValues.subTemplateBuilders.push(ifSubTemplateBuilder)
        instanceValues.ifElseBlockSubTemplateBuilders.push(ifSubTemplateBuilder)

        val conditionSubTemplateBuilder = IfElseSubTemplateBuilder(token.condition)
        instanceValues.subTemplateBuilders.push(conditionSubTemplateBuilder)
    }

    private fun handleElseIfToken(token: ElseIfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousBuilder = instanceValues.subTemplateBuilders.pop()
        val ifElseBlock = instanceValues.ifElseBlockSubTemplateBuilders.peekFirst()
        if(previousBuilder is IfElseSubTemplateBuilder) {
            ifElseBlock.ifElseSubTemplates.add(previousBuilder.createSubTemplate())
        } else {
            throw RuntimeException("Unexpected value")
        }

        val conditionSubTemplateBuilder = IfElseSubTemplateBuilder(token.condition)
        instanceValues.subTemplateBuilders.push(conditionSubTemplateBuilder)
    }

    private fun handleElseToken(token: ElseToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousBuilder = instanceValues.subTemplateBuilders.pop()
        val ifElseBlock = instanceValues.ifElseBlockSubTemplateBuilders.peekFirst()
        if(previousBuilder is IfElseSubTemplateBuilder) {
            ifElseBlock.ifElseSubTemplates.add(previousBuilder.createSubTemplate())
        } else {
            throw RuntimeException("Unexpected value")
        }

        val elseSubTemplateBuilder = ElseSubTemplateBuilder()
        instanceValues.subTemplateBuilders.push(elseSubTemplateBuilder)
    }

    private fun handleEndIfToken(token: EndIfToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousBuilder = instanceValues.subTemplateBuilders.pop()
        val ifElseBlock = instanceValues.ifElseBlockSubTemplateBuilders.peekFirst()
        when (previousBuilder) {
            is IfElseSubTemplateBuilder -> ifElseBlock.ifElseSubTemplates.add(previousBuilder.createSubTemplate())
            is ElseSubTemplateBuilder -> ifElseBlock.elseSubTemplate = previousBuilder.createSubTemplate()
            else -> throw RuntimeException("Unexpected value")
        }
        val previousIfBuilder = instanceValues.ifElseBlockSubTemplateBuilders.pop()
        instanceValues.subTemplateBuilders.pop()
        assert(previousIfBuilder is IfElseBlockSubTemplateBuilder)

        appendSubTemplate(instanceValues, previousBuilder.createSubTemplate())
    }

    private fun handleEachToken(token: EachToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val eachSubTemplateBuilder = EachSubTemplateBuilder(token.listSelector, token.iteratorName)
        instanceValues.subTemplateBuilders.push(eachSubTemplateBuilder)
    }

    private fun handleEndEachToken(token: EndEachToken, tokens: Iterator<Token>, instanceValues: ParserInstanceValues) {
        val previousEachSubTemplateBuilder = instanceValues.subTemplateBuilders.pop()
        if (previousEachSubTemplateBuilder is EachSubTemplateBuilder) {
            appendSubTemplate(instanceValues, previousEachSubTemplateBuilder.createSubTemplate())
        } else {
            throw RuntimeException("Unexpected value")
        }
    }

    private fun handleIncludeToken(token: IncludeToken, instanceValues: ParserInstanceValues) {
        TODO()
    }
}
