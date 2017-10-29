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

interface PartialTemplate {
    fun appendResult(data: JsonObject, stringBuilder: StringBuilder)
}

interface Condition: PartialTemplate //TODO is this needed?

/**
 * A TextTemplate simply represents an uninterpreted block of text.  The data object isn't used
 * in the appendResult method at all.
 */
class TextTemplate(private val textContent: String): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        stringBuilder.append(textContent)
    }
}

/**
 * A ScalarTemplate is passed a namespaced name and uses that name to access a scala value from the data JSONObject.
 */
class ScalarTemplate(private val name: List<String>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        data.get(name.joinToString(".")) //TODO I don't think this works and I have to loop calls to get
        stringBuilder.append(data)
    }
}

/**
 * A NamedTemplate is template made up of multiple PartialTemplates and given a name for referencing in the cache.
 */
data class NamedTemplate(val templateName: String, private val content: List<PartialTemplate>): PartialTemplate {
    override fun appendResult(data: JsonObject, stringBuilder: StringBuilder) {
        //TODO maybe use collect here
        TODO("not implemented")
    }
}

//data class IfTemplate()...

//class Interpretation(val reference: List<String>): Token
//class Conditional(val conditions: List<Condition>): Token
//class If(val reference: List<String>, val content: List<Token>): Condition
//class ElseIf(val reference: List<String>, val content: List<Token>): Condition
//class Else(val content: List<Token>): Condition
//class ListLoop(val reference: List<String>, val variableName: String, val content: List<Token>): Token
//class MapLoop(val reference: List<String>, val keyName: String, val valueName: String, val content: List<Token>): Token
//class Include(val reference: List<String>, val content: List<Token>)
//
//enum class TokenType {
//    IF,
//    ELSEIF,
//    ELSE,
//    CLOSE_IF,
//    EACH,
//    CLOSE_EACH,
//    AS,
//    REFERENCE,
//    LOOP_NAME,
//    KEY_NAME,
//    VALUE_NAME,
//    TEMPLATE_NAME,
//    EOL
//}
