package com.krmnserv321.jsonparser

internal class JsonList : ArrayList<Any?>() {
    internal fun containsKey(key: String): Boolean {
        forEach {
            when (it) {
                is JsonMap -> {
                    if (it.deepContainsKey(key)) {
                        return true
                    }
                }
                is JsonList -> {
                    if (it.containsKey(key)) {
                        return true
                    }
                }
            }
        }

        return false
    }

    internal fun toString(count: Int): String {
        if (isEmpty()) {
            return "[]"
        }

        val result = StringBuilder()
        result.append("[$separator")

        val iterator = iterator()
        while (true) {
            val next = iterator.next()
            result.append(SPACE.repeat(count))
                .append(toString(next, count))
            if (iterator.hasNext()) {
                result.append(",")
                    .append(separator)
            } else {
                break
            }
        }

        result.append(separator)
            .append(SPACE.repeat(count - 1))
            .append("]")

        return result.toString()
    }

    private companion object {
        fun toString(any: Any?, count: Int): String {
            var c = count
            return when (any) {
                is JsonMap -> any.toString(++c)
                is JsonList -> any.toString(++c)
                is String -> "\"${any}\""
                else -> any.toString()
            }
        }
    }
}