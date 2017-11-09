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

import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Stream

class WeaselTemplateLexerSpec extends Specification {
    @Shared def templateParser = new WeaselTemplateParser()
    @Shared def helper = new Helper()

    def setup() {
    }

    def "test parsing simple scalar variables"() {
        given:
        Stream<String> statement = ["This is a <?test>."].stream()
        when:
        List<SubTemplate> result = templateParser.parse(statement)
        then:
        result.size() == 3
    }

    def "test parsing simple if statements"() {
        given:
        Stream<String> statement = ["<?if user.isAdmin>Hey<?else>Hi<?endif>"].stream()
        when:
        List<SubTemplate> result = templateParser.parse(statement)
        then:
        result.size() == 1
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
        when:
        List<SubTemplate> result = templateParser.parse(statement)
        then:
        result.size() == 1
    }

    def "test parsing inline if"() {
        given:
        Stream<String> statement = ["Hello<?if user>&nbsp;<?user.name><?end if>!"].stream()
        when:
        List<SubTemplate> result = templateParser.parse(statement)
        then:
        result.size() == 3
    }
    //TODO test parsing each
    //TODO test parsing nested each
    //TODO test parsing complex conditional + each
}
