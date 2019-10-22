package com.krmnserv321.jsonparser

import java.lang.NumberFormatException
import java.math.BigDecimal

internal class Lexer(private var input: String) {
    private var pos = 0
    private var readPos = 0
    private var ch = ZERO
    private var lineNumber = 1

    init {
        input = input.trim()
        readChar()
    }

    private val tokens = arrayOf(
        COLON,
        L_BRACE,
        R_BRACE,
        L_BRACKET,
        R_BRACKET,
        DOT,
        COMMA
    )

    fun nextToken(): Token {
        var token: Token? = null

        if (ch == ZERO) {
            return EOF.copy(lineNumber = lineNumber)
        }

        if (Character.isWhitespace(ch)) {
            if (ch == '\n') {
                lineNumber++
            }

            readChar()
            return nextToken()
        }

        for (t in tokens) {
            if (t.literal[0] == ch) {
                token = t
                break
            }
        }

        if (token == null) {
            token = when (ch) {
                '"' -> {
                    val str = readString()
                    if (str == null) {
                        Token(TokenType.Illegal, ch.toString())
                    } else {
                        Token(TokenType.String, str)
                    }
                }
                in '0'..'9', '-' -> {
                    val num = readNumber()
                    return try {
                        val big = BigDecimal(num)
                        if ('.' in num) {
                            Token(TokenType.Double, big.toDouble().toString())
                        } else {
                            Token(TokenType.Long, big.toInt().toString())
                        }
                    } catch (e: NumberFormatException) {
                        Token(TokenType.Illegal, num)
                    }
                }
                else -> when (readKeyword()) {
                    "true" -> TRUE
                    "false" -> FALSE
                    "null" -> NULL
                    else -> Token(TokenType.Illegal, ch.toString())
                }
            }
        }

        readChar()
        return token.copy(lineNumber = lineNumber)
    }

    private fun readChar() {
        if (readPos < input.length) {
            ch = input[readPos]
            pos = readPos
            readPos++
        } else {
            ch = ZERO
        }
    }

    private fun peekChar(): Char {
        return if (readPos < input.length) {
            input[readPos]
        } else {
            ZERO
        }
    }

    private fun readKeyword(): String {
        val p = pos
        while (peekChar() in 'a'..'z') {
            readChar()
        }

        return input.substring(p..pos)
    }

    private fun readNumber(): String {
        val p = pos
        while (peekChar() in '0'..'9' || peekChar() == 'e' || peekChar() == '.') {
            readChar()
        }

        readChar()

        return input.substring(p until pos)
    }

    private fun readString(): String? {
        val sb = StringBuilder()

        while (peekChar() != ZERO && peekChar() != '"') {
            readChar()

            if (ch == '\\') {
                readChar()
                if (ch == 'u') {
                    sb.append(readCodePoint() ?: return null)
                } else {
                    val escape = escapeMap[ch]
                    sb.append(escape ?: return null)
                }
            } else {
                sb.append(ch)
            }
        }

        readChar()

        return sb.toString()
    }

    private fun readCodePoint(): String? {
        val code = StringBuilder()
        for (i in 0 until 4) {
            readChar()
            if (ch in '0'..'9' || ch in 'A'..'F' || ch in 'a'..'f') {
                code.append(ch)
            } else {
                return null
            }
        }

        return String(Character.toChars(Integer.parseInt(code.toString(), 16)))
    }

    private companion object {
        const val ZERO = 0.toChar()

        val EOF = Token(TokenType.EOF, "")
        val COLON = Token(TokenType.Colon, ":")
        val L_BRACE = Token(TokenType.LBrace, "{")
        val R_BRACE = Token(TokenType.RBrace, "}")
        val L_BRACKET = Token(TokenType.LBracket, "[")
        val R_BRACKET = Token(TokenType.RBracket, "]")
        val DOT = Token(TokenType.Dot, ".")
        val COMMA = Token(TokenType.Comma, ",")
        val TRUE = Token(TokenType.True, "true")
        val FALSE = Token(TokenType.False, "false")
        val NULL = Token(TokenType.Null, "null")

        val escapeMap = hashMapOf(
            '"' to '"',
            '\\' to '\\',
            '/' to '/',
            'b' to '\b',
            'n' to '\n',
            'r' to '\r',
            't' to '\t'
        )
    }
}