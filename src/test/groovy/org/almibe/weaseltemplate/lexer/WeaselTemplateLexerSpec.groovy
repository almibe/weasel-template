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

import org.almibe.weaseltemplate.lexer.Token
import org.almibe.weaseltemplate.lexer.WeaselTemplateLexer
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

class WeaselTemplateLexerSpec extends Specification {
    @Shared def lexer = new WeaselTemplateLexer()

    def "test lexing simple scalar variables"() {
        given:
        Stream<String> statement = ["This is a <?test>."].stream()
        when:
        List<Token> result = lexer.lex(statement)
        then:
        result.size() == 3
    }

    def "test lexing simple if statements"() {
        given:
        Stream<String> statement = ["<?if user.isAdmin>Hey<?else>Hi<?end if>"].stream()
        when:
        List<Token> result = lexer.lex(statement)
        then:
        result.size() == 6
    }

    def "test lexing nested if statements"() {
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
        List<Token> result = lexer.lex(statement)
        then:
        result.size() == 18
    }

    def "test lexing inline if"() {
        given:
        Stream<String> statement = ["Hello<?if user> <?user.name><?end if>!"].stream()
        when:
        List<Token> result = lexer.lex(statement)
        then:
        result.size() == 6
    }
    //TODO test lexing each
    //TODO test lexing nested each
    //TODO test lexing complex conditional + each
}
