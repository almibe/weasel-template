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

interface Template {
    fun apply(data: JsonObject, stringBuilder: StringBuilder)
}

data class TextTemplate(val content: String): Template { // TextTemplate("hello")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
data class ScalarTemplate(val name: String): Template { // ScalarTemplate("user.address.city")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
data class IfTemplate(val condition: String): Template { // IfTemplate("user.isAdmin")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
data class ElseIfTemplate(val condition: String): Template { // ElseIfTemplate("user.isAdmin")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
class ElseTemplate : Template { // ElseTemplate()
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
data class EachTemplate(val list: String, val iteratorName: String): Template { // EachTemplate("users", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
data class IncludeTemplate(val name: String, val argument: String? = null): Template { // IncludeTemplate("adminTemplate.wt", "user")
    override fun apply(data: JsonObject, stringBuilder: StringBuilder) {}
}
