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

import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

class WeaselTemplateLexer {
    private val cache: Map<String, List<Token>> = mutableMapOf() //only shared variable

    private data class TokenizingInstanceValues(
        val input: Stream<String>,
        val consumed: StringBuilder = StringBuilder(),
        val tokens: MutableList<Token> = mutableListOf(),
        val lineNumber: Int = 0
    )

    fun tokenize(fileName: String): List<Token> {
        val cacheValue = cache[fileName]
        return if (cacheValue != null) {
            cacheValue
        } else {
            handleTokenizing(fileName)
        }
    }

    private fun handleTokenizing(fileName: String): List<Token> {
        val path = Paths.get(fileName)
        val input = Files.lines(path)
        val instanceValues = WeaselTemplateLexer.TokenizingInstanceValues(input)
        input.forEach { line: String ->
            val iterator = line.toCharArray().iterator()
            while (iterator.hasNext()) {
                val nextCharacter = iterator.nextChar()
                if (nextCharacter == '<') {
                    checkTag(iterator, instanceValues)
                } else {
                    instanceValues.consumed.append(nextCharacter)
                }
            }
        }
        return instanceValues.tokens
    }
  
    private fun checkTag(iterator: CharIterator, instanceValues: TokenizingInstanceValues) {
        if (iterator.hasNext()) {
            val nextCharacter = iterator.nextChar()
            if (nextCharacter == '$') {
                if (!instanceValues.consumed.isEmpty()) { //if there is already a text token being build up add it
                    addTextToken(instanceValues)
                }
                readWeaselTemplateTag(iterator, instanceValues)
            } else {
                instanceValues.consumed.append("<$nextCharacter")
            }
        } else {
            throw RuntimeException("Invalid starting tag at end of file.")
        }
    }

    private fun addTextToken(instanceValues: TokenizingInstanceValues) {
        val tokenValue = instanceValues.consumed.toString()
        instanceValues.consumed.setLength(0) //clear
        instanceValues.tokens.add(Token(TokenType.TEXT, tokenValue))
    }

    private fun readWeaselTemplateTag(iterator: CharIterator, instanceValues: TokenizingInstanceValues) {
        val tagTokens: List<String> = readTagTokens(iterator, instanceValues)
        when (tagTokens.first()) {
            "if" -> createIfToken(tagTokens, instanceValues)
            "elseif" -> createElseIfToken(tagTokens, instanceValues)
            "else" -> createElseToken(tagTokens, instanceValues)
            "include" -> createIncludeToken(tagTokens, instanceValues)
            "each" -> createEachToken(tagTokens, instanceValues)
            "end" -> createEndToken(tagTokens, instanceValues)
            else -> createValueToken(tagTokens, instanceValues)
        }
    }

    /**
    * Returns a list of tokens that are in the current tag.
    */
    private fun readTagTokens(iterator: CharIterator, instanceValues: TokenizingInstanceValues): List<String> {
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

    private fun createIfToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }

    private fun createElseIfToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }

    private fun createElseToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }

    private fun createIncludeToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }

    private fun createEachToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }

    private fun createEndToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }

    private fun createValueToken(tagTokens: List<String>, instanceValues: TokenizingInstanceValues) {
        TODO("finish")
    }
}
