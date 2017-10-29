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

import spock.lang.Specification

class WeaselTemplateSpec extends Specification {
    def templateEngine = new WeaselTemplateEngine()

    def simple = ['', 'test', 'multi word test', '$5 = $5']
    def html = ['<hr>', 'This is a <test>.', '$', '$$$']
    def scalar = ['<$test>', '<$data.test>', '$<$data.test1.test2>']
    def ifs = []
    def nestedIfs = []
    def fullIfElse = []
    def iterateList = []
    def iterateMap = []

    //TODO define all test data here inline as a list so .stream can be called
    //TODO delete all test files

    def "handle empty files"() {
        given:
        def lines = simple.stream()
        when:
        String result = templateEngine.applyTemplate("01-emptyFile.wtf", lines, null)
        then:
        result.readLines().stream()
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
