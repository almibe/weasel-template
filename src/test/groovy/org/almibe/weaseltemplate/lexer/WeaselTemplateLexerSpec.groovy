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

import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

class WeaselTemplateLexerSpec extends Specification {
    @Shared def lexer = new WeaselTemplateLexer()

    def "tokenize simple scalar variables"() {
        given:
        Stream<String> statement = ["This is a <?test>."].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 3
    }

    def "tokenize simple if tags"() {
        given:
        Stream<String> statement = ["<?if user.isAdmin>Hey<?else>Hi<?end if>"].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 6
    }

    def "tokenize nested if tags"() {
        given:
        Stream<String> statement = [
                "<?if user.isLoggedIn>",
                "  <?if user.isAdmin><?include 'admin.wt'>",
                "  <?elseif user.isMod><?include 'mod.wt'>",
                "  <?else>Hello<?end if>",
                "<?else>",
                "  <?include 'login.wt'>",
                "<?end if>"
        ].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 18
    }

    def "tokenize inline if"() {
        given:
        Stream<String> statement = ["Hello<?if user> <?user.name><?end if>!"].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 6
    }

    def "tokenize simple each tag"() {
        given:
        Stream<String> statement = ["<?each items as item><?item><?end each>"].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 4
    }

    def  "tokenize nested each tags"() {
        given:
        Stream<String> statement = ["<?each items as item><?item><?each stats as stat> <?stat><?end each><?end each>"].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 8
    }

    def "tokenize complex conditional + each"() {
        given:
        Stream<String> statement = ["<?each items as item><?if item.equipped> * <?end if><?item><?each stats as stat> <?stat><?end each><?end each>"].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 11
    }

    def "tokenize include tags"() {
        given:
        Stream<String> statement = ["<?include 'header.wt'><?if user.admin><?include 'admin.wt'><?end if>"].stream()
        when:
        List<Token> result = lexer.tokenize(statement)
        then:
        result.size() == 5
    }
}
