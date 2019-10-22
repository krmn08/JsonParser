package com.krmnserv321.jsonparser

class JsonParser(input: String) {
    private val lexer: Lexer = Lexer(input)

    private var curToken = lexer.nextToken()
    private var peekToken = lexer.nextToken()

    @Suppress("MemberVisibilityCanBePrivate")
    val errors = mutableListOf<String>()

    fun parse(): JsonMap {
        if (curToken.type != TokenType.LBrace) {
            addError("expected next token to be LBrace, got ${curToken.type} instead")
        }

        return parseObject()
    }

    private fun parseObject(): JsonMap {
        val map = JsonMap()
        if (peekToken.type == TokenType.RBrace) {
            nextToken()
            return map
        }

        while (true) {
            if (!expectPeek(TokenType.String)) {
                nextToken()
                return map
            }

            val key = curToken.literal

            if (!expectPeek(TokenType.Colon)) {
                return map
            }

            val value = parseValue()

            map[key] = value

            if (peekToken.type == TokenType.RBrace) {
                nextToken()
                return map
            }

            if (!expectPeek(TokenType.Comma)) {
                return map
            }
        }
    }

    private fun parseArray(): JsonList {
        val list = JsonList()
        if (peekToken.type == TokenType.RBracket) {
            nextToken()
            return list
        }

        while (true) {
            val value = parseValue()
            list.add(value)

            if (peekToken.type == TokenType.RBracket) {
                nextToken()
                return list
            }

            if (!expectPeek(TokenType.Comma)) {
                return list
            }
        }
    }

    private fun parseValue(): Any? {
        nextToken()
        return when (curToken.type) {
            TokenType.String -> curToken.literal
            TokenType.Long -> curToken.literal.toLong()
            TokenType.Double -> curToken.literal.toDouble()
            TokenType.True -> true
            TokenType.False -> false
            TokenType.Null -> null
            TokenType.LBrace -> parseObject()
            TokenType.LBracket -> parseArray()
            else -> addError("not a value: $curToken")
        }
    }

    private fun nextToken() {
        curToken = peekToken
        peekToken = lexer.nextToken()
    }

    private fun expectPeek(type: TokenType): Boolean {
        if (peekToken.type === type) {
            nextToken()
            return true
        }
        addError("expected next token to be $type, got ${peekToken.type} instead")
        return false
    }

    private fun addError(message: String) {
        errors.add("line:${curToken.lineNumber} $message")
    }
}