class WeaselTemplateLexer() {
  val cache: Map<String: List<Token>> = mutableMapOf()

  //Lexer class fields
  private val consumed = stringbuilder()
  private val input = ...
  private val tokens = mutableListOf<Token>
  private val lineNumber = 0

  fun lex(val fileName: String): List<Token> {
    return if (cache.contains(fileName) {
      cache.get(fileName)
    } else {
      handleLexing(fileName)
    }
  }
  
  fun handleLexing(val fileName: String): List<Token> {
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
  
  fun checkTag() {
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

  fun addTextToken() {
    val tokenValue = consumed.toString()
    consumed.clear()
    tokens.add(new TextToken(tokenValue))
  }

  fun readWeaselTemplateTag() {
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
  fun readTagTokens(): List<String> {
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

  fun createIfToken() {
    TODO("finish")
  }

  fun createElseIfToken() {
    TODO("finish")
  }

  fun createElseToken() {
    TODO("finish")
  }

  fun createIncludeToken() {
    TODO("finish")
  }

  fun createEachToken() {
    TODO("finish")
  }

  fun createEndToken() {
    TODO("finish")
  }

  fun createValueToken() {
    TODO("finish")
  }
}
