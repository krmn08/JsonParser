package com.krmnserv321.jsonparser

internal data class Token(val type: TokenType, val literal: String, val lineNumber: Int = 0)