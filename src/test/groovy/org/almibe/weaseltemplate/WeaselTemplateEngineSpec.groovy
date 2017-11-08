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

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Paths

class WeaselTemplateEngineSpec extends Specification {
    @Shared def templateEngine = new WeaselTemplateEngine(this.class.classLoader)
    @Shared JsonObject data = new JsonObject()
    @Shared Helper helper = new Helper()

    def setup() {
        data.addProperty("name", "Alex")
        data.addProperty("age", 32)
        def address = new JsonObject()
        address.addProperty("city", "Bloomington")
        data.add("address", address)
    }

    def "test basic tokenizing"() {
        given:
        WeaselTemplateParser parser = new WeaselTemplateParser()
        when:
        List<SubTemplate> tokens = parser.parse(Files.lines(Paths.get("src/test/resources/01-text.result")))
        then:
        tokens.size() == 1
        tokens.first() instanceof TextSubTemplate
    }

    def "handle plain files"() {
        given:
        String expectedResult = new File("src/test/resources/01-text.result").text
        when:
        String result = templateEngine.processTemplate("01-text.test", data)
        then:
        helper.contentCompare(expectedResult, result)
    }

    def "support basic variables"() {
        given:
        String expectedResult = new File("src/test/resources/02-scalar.result").text
        when:
        String result = templateEngine.processTemplate("02-scalar.test", data)
        then:
        helper.contentCompare(expectedResult, result)
    }

    def "expect an exception when you pass list or map data to a singular reference"() {
        given:
        def copy = data.deepCopy()
        when:
        copy.remove("name")
        copy.add("name", new JsonArray())
        templateEngine.processTemplate("02-scalar.test", copy)
        then:
        thrown(RuntimeException)
    }

    def "support conditionals"() {
        given:
        String expectedResult = new File("src/test/resources/03-conditional.result").text
        when:
        String result = templateEngine.processTemplate("03-conditional.test", data)
        then:
        helper.contentCompare(expectedResult, result)
    }
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
//    def "support includes"() {
//    }
}
