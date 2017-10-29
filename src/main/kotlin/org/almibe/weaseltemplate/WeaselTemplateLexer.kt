class WeaselTemplateLexer() {
  val cache: Map<String: List<Token>> = mutableMapOf() //only shared variable

  data class TokenizingInstanceValues(
    val consumed: StringBuilder,
    val input: ...
    val tokens: mutableListOf<Token>
    val lineNumber: 0
  }

  fun lex(val fileName: String): List<Token> {
    val instanceValues = TokenizingInstanceValues()
    return if (cache.contains(fileName) {
      cache.get(fileName)
    } else {
      handleTokenizing(fileName, instanceValues)
    }
  }
  
  fun handleTokenize(val fileName: String, val instanceValues: TokenizingInstanceValues): List<Token> {
    val index = readFile(fileName)
    while input has next {
      val nextCharacter = input.read()
      if (nextCharacter == '<' {
        '<' -> checkTag()
      } else {
        consumed.add(nextCharacter)
      }
    }
  }
  
  fun checkTag(val instanceValues: TokenizingInstanceValues) {
    if (input.hasNext()) { 
      val nextCharacter = input.read()
      if (nextCharacter == '$') {
        if (!consumed.isEmpty) {
          addTextToken()
        }
        readWeaselTemplateTag(()
      } else {
        consumed.add("<${nextCharacter}")
      }
    } else {
      throw RuntimeException("Invalid starting tag at end of file.")
    }
  }

  fun addTextToken(val instanceValues: TokenizingInstanceValues) {
    val tokenValue = consumed.toString()
    consumed.clear()
    tokens.add(new TextToken(tokenValue))
  }

  fun readWeaselTemplateTag(val instanceValues: TokenizingInstanceValues) {
    while (input.hasNext()) {
      val tagTokens: List<String> = readTagTokens()
      when (tagTokens.first) {
        'if' -> createIfToken(tagTokens)
        'elseif' -> createElseIfToken(tagTokens)
        'else' -> createElseToken(tagTokens)
        'include' -> createIncludeToken(tagTokens)
        'each' -> createEachToken(tagTokens)
        'end' -> createEndToken(tagTokens)
        else -> createValueToken(tagTokens)
      }
    }
  }

  /**
  * Returns a list of tokens that are in the current tag.
  */
  fun readTagTokens(val instanceValues: TokenizingInstanceValues): List<String> {
    while (input.hasNext()) {
      val nextToken = input.read()
      if (nextToken != '>') {
        consumed.add(input.read())
      } else {
        val result = consumed.toString()
        consumed.clear()
        val resultList = result.split(' ')
        if (resultList.isEmpty) {
          throw RuntimeException("Empty tag on line $lineNumber")
        } else {
          return resultList
        }
      }
    }
  }

  fun createIfToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }

  fun createElseIfToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }

  fun createElseToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }

  fun createIncludeToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }

  fun createEachToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }

  fun createEndToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }

  fun createValueToken(val instanceValues: TokenizingInstanceValues) {
    TODO("finish")
  }
}
