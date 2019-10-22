package com.krmnserv321.jsonparser

internal enum class TokenType {
    Illegal,
    EOF,
    Colon,
    LBrace,
    RBrace,
    LBracket,
    RBracket,
    Dot,
    Comma,
    String,
    Long,
    Double,
    True,
    False,
    Null,
}