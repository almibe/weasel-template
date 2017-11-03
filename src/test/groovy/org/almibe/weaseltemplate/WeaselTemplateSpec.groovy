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
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class WeaselTemplateSpec extends Specification {
    @Shared def templateEngine = new WeaselTemplateEngine(this.class.classLoader)
    @Shared JsonObject data = new JsonObject()

    def setup() {
        data.addProperty("test", "Test")
    }

    def "test basic tokenizing"() {
        given:
        WeaselTemplateLexer lexer = new WeaselTemplateLexer()
        when:
        List<PartialTemplate> tokens = lexer.tokenize(Files.lines(Paths.get("src/test/resources/01-text.result")))
        then:
        tokens.size() == 1
        tokens.first() instanceof TextTemplate
    }

    def "handle plain files"() {
        given:
        String expectedResult = new File("src/test/resources/01-text.result").text
        when:
        String result = templateEngine.processTemplate("01-text.test", data)
        then:
        expectedResult == result
    }

    def "handle empty templates"() {

    }

    def "handle template data with empty templates"() {

    }

    def "handle empty html files"() {

    }

//    def "support basic variables"() {
//    }
//
//    def "supported nested variables"() {
//    }
//
//    def "expect an exception when you pass list or map data to a singular reference"() {
//    }
//
//    def "test passing list data to a list references"() {
//    }
//
//    def "expect an exception when you pass map or singular values to list references"() {
//
//    }
//
//    def "test passing map data to a map references"() {
//    }
//
//    def "expect an exception when you pass list or singular values to map references"() {
//    }
//
//    def "support simple if conditions"() {
//    }
//
//    def "support full if else conditions"() {
//    }
//
//    def "support nested conditions"() {
//    }
//
//    def "support includes"() {
//    }
}
