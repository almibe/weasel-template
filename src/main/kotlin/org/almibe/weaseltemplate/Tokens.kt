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

interface Token

data class TextToken(val content: String): Token // TextToken("hello")
data class ScalarToken(val name: String): Token // ScalarToken("user.address.city")
data class IfToken(val condition: String): Token // IfToken("user.isAdmin")
data class ElseIfToken(val condition: String): Token // ElseIfToken("user.isAdmin")
class ElseToken: Token // ElseToken()
class EndIfToken: Token // EndIfToken()
data class EachToken(val list: String, val iteratorName: String): Token // EachToken("users", "user")
class EndEachToken: Token // EndEachToken()
data class IncludeToken(val name: String, val argument: String? = null): Token // IncludeToken("adminTemplate.wt", "user")
