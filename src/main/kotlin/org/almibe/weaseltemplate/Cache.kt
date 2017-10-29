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

interface Cache
interface Condition: Cache

data class StringContent(val data: String): Cache
data class Interpretation(val reference: List<String>): Cache
data class Conditional(val conditions: List<Condition>): Cache
data class If(val reference: List<String>, val content: List<Cache>): Condition
data class ElseIf(val reference: List<String>, val content: List<Cache>): Condition
data class Else(val content: List<Cache>): Condition
data class ListLoop(val reference: List<String>, val variableName: String, val content: List<Cache>): Cache
data class MapLoop(val reference: List<String>, val keyName: String, val valueName: String, val content: List<Cache>): Cache
data class Include(val reference: List<String>, val content: List<Cache>)

data class Template(val templateName: String, private val content: List<Cache>) {
    fun applyTemplate(data: JsonObject): String {
        TODO("implement")
    }
}
