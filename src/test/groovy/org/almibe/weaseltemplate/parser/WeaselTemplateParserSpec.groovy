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
import spock.lang.Shared
import spock.lang.Specification
import java.util.stream.Stream

class WeaselTemplateParserSpec extends Specification {
    @Shared def templateParser = new WeaselTemplateParser()
    @Shared def templateLexer = new WeaselTemplateLexer()

    def "test parsing simple text token"() {
        given:
        List<Token> tokens = [
                new TextToken("Test")
        ]
        when:
        List<SubTemplate> result = templateParser.parse(tokens)
        then:
        result.size() == 1
    }

    def "test parsing simple scalar variables"() {
        given:
        List<Token> tokens = [
                new TextToken("This is a "),
                new ScalarToken("test"),
                new TextToken(".\n")
        ]
        when:
        List<SubTemplate> result = templateParser.parse(tokens)
        then:
        result.size() == 3
    }

    def "test parsing simple if statements"() {
        given:
        List<Token> tokens = [
                new IfToken("user.isAdmin"),
                new TextToken("Hey"),
                new ElseToken(),
                new TextToken("Hi"),
                new EndIfToken(),
                new TextToken("\n")
        ]
        when:
        List<SubTemplate> result = templateParser.parse(tokens)
        then:
        result.size() == 2
    }

    def "test parsing nested if statements"() {
        given:
        Stream<String> statement = [
                "<?if user.isLoggedIn>",
                "  <?if user.isAdmin><?include 'admin.wt'>",
                "  <?elseif user.isMod><?include 'mod.wt'>",
                "  <?else>Hello<?endif>",
                "<?else>",
                "  <?include 'login.wt'>",
                "<?endif>"
        ].stream()
        List<Token> tokens = templateLexer.tokenize(statement)
        when:
        List<SubTemplate> result = templateParser.parse(tokens)
        then:
        result.size() == 1
    }

    def "test parsing inline if"() {
        given:
        List<Token> tokens = [
                new TextToken("Hello"),
                new IfToken("user"),
                new TextToken(" "),
                new ScalarToken("user.name"),
                new EndIfToken(),
                new TextToken("!\n")
        ]
        when:
        List<SubTemplate> result = templateParser.parse(tokens)
        then:
        result.size() == 3
    }
    //TODO test parsing each
    //TODO test parsing nested each
    //TODO test parsing complex conditional + each
    //TODO test parsing includes with and without arguments
}