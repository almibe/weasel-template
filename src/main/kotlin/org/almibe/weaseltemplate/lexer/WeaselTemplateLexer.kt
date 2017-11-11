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

package org.almibe.weaseltemplate.lexer

import java.util.stream.Stream

class WeaselTemplateLexer {
    private val specialCharacter = '?'

    private data class LexerInstanceValues(
            val consumed: StringBuilder = StringBuilder(),
            val tokens: MutableList<Token> = mutableListOf(),
            val lineNumber: Int = 0
    )

    fun tokenize(lines: Stream<String>): List<Token> {
        val instanceValues = LexerInstanceValues()
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
        if (instanceValues.consumed.isNotEmpty()) {
            createTextToken(instanceValues) //create a text token with remaining value
        }
        return instanceValues.tokens
    }

    private fun checkTag(iterator: CharIterator, instanceValues: LexerInstanceValues) {
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

    private fun readWeaselTemplateTag(iterator: CharIterator, instanceValues: LexerInstanceValues) {
        val tagTokens: List<String> = splitTagText(iterator, instanceValues)
        when (tagTokens.first()) {
            "if" -> createIfToken(tagTokens, instanceValues)
            "elseif" -> createElseIfToken(tagTokens, instanceValues)
            "else" -> createElseToken(tagTokens, instanceValues)
            "include" -> createIncludeToken(tagTokens, instanceValues)
            "each" -> createEachToken(tagTokens, instanceValues)
            "end" -> createEndToken(tagTokens, instanceValues)
            else -> createScalarToken(tagTokens, instanceValues)
        }
    }

    /**
    * Returns a list of strings that represent current tag.
    */
    private fun splitTagText(iterator: CharIterator, instanceValues: LexerInstanceValues): List<String> {
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

    private fun createTextToken(instanceValues: LexerInstanceValues) {
        val tokenValue = instanceValues.consumed.toString()
        instanceValues.consumed.setLength(0) //clear
        if (tokenValue.isNotEmpty()) {
            instanceValues.tokens.add(TextToken(tokenValue))
        }
    }

    private fun createScalarToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.size == 1)
        instanceValues.tokens.add(ScalarToken(tagTokens.first()))
    }

    private fun createIfToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.first() == "if")
        assert(tagTokens.size == 2)
        instanceValues.tokens.add(IfToken(tagTokens.component2()))
    }

    private fun createElseIfToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.first() == "elseif")
        assert(tagTokens.size == 2)
        instanceValues.tokens.add(ElseIfToken(tagTokens.component2()))
    }

    private fun createElseToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.first() == "else")
        assert(tagTokens.size == 1)
        instanceValues.tokens.add(ElseToken())
    }

    private fun createIncludeToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.first() == "include")
        assert(tagTokens.size == 2 || tagTokens.size == 3)
        when (tagTokens.size) {
            2 -> instanceValues.tokens.add(IncludeToken(tagTokens.component2()))
            3 -> instanceValues.tokens.add(IncludeToken(tagTokens.component2(), tagTokens.component3()))
            else -> RuntimeException("Unexpected value")
        }
    }

    private fun createEachToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.first() == "each")
        assert(tagTokens.component3() == "as")
        assert(tagTokens.size == 4)
        instanceValues.tokens.add(EachToken(tagTokens.component2(), tagTokens.component4()))
    }

    private fun createEndToken(tagTokens: List<String>, instanceValues: LexerInstanceValues) {
        assert(tagTokens.first() == "end")
        assert(tagTokens.component2() == "if" || tagTokens.component2() == "each")
        assert(tagTokens.size == 2)
        when (tagTokens.component2()) {
            "if" -> instanceValues.tokens.add(EndIfToken())
            "each" -> instanceValues.tokens.add(EndEachToken())
            else -> RuntimeException("Unexpected value")
        }
    }
}
