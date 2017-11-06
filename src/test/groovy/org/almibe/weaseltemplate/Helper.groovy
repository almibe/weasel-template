package org.almibe.weaseltemplate

class Helper {
    String removeWhitespace(String input) {
        StringBuilder result = new StringBuilder()
        input.readLines().forEach {
            if (!it.trim().isEmpty()) {
                result.append(it.trim() + "\n")
            }
        }
        return result.toString()
    }

    Boolean contentCompare(String input1, String input2) {
        return removeWhitespace(input1) == removeWhitespace(input2)
    }
}
