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

class WeaselTemplateEngineSpec extends Specification {
    @Shared def templateEngine = new WeaselTemplateEngine(this.class.classLoader)
    @Shared JsonObject data = new JsonObject()
    @Shared Helper helper = new Helper()

    def setup() {
        def user = new JsonObject()
        user.addProperty("name", "Alex")
        data.add("user", user)
        data.addProperty("name", "Alex")
        data.addProperty("age", 32)
        data.addProperty("adminUser", true)
        def address = new JsonObject()
        address.addProperty("city", "Bloomington")
        data.add("address", address)
        def nested = new JsonObject()
        nested.addProperty("boolean", true)
        data.add("nested", nested)
        data.addProperty("true", true)
        def values = new JsonArray()
        values.add(1)
        values.add(2)
        values.add(true)
        values.add("false")
        data.add("values", values)
        data.addProperty("html", "Test this < & > ' \"")
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

    def "support list iteration"() {
        given:
        String expectedResult = new File("src/test/resources/04-list.result").text
        when:
        String result = templateEngine.processTemplate("04-list.test", data)
        then:
        helper.contentCompare(expectedResult, result)
    }

    def "support includes"() {
        given:
        String expectedResult = new File("src/test/resources/05-includes.result").text
        when:
        String result = templateEngine.processTemplate("05-includes.test", data)
        then:
        helper.contentCompare(expectedResult, result)
    }

    def "support escapes"() {
        given:
        String expectedResult = new File("src/test/resources/06-escapes.result").text
        when:
        String result = templateEngine.processTemplate("06-escapes.test", data)
        then:
        helper.contentCompare(expectedResult, result)
    }
}
