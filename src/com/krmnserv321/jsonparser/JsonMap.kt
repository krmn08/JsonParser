package com.krmnserv321.jsonparser

class JsonMap : LinkedHashMap<String, Any?>() {
    fun deepContainsKey(key: String): Boolean {
        if (containsKey(key)) {
            return true
        }

        values.forEach {
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
            return "{}"
        }

        val result = StringBuilder()
        result.append("{$separator")

        val iterator = iterator()
        while (true) {
            val next = iterator.next()
            result.append(SPACE.repeat(count))
                .append("\"${next.key}\": ${toString(next.value, count)}")
            if (iterator.hasNext()) {
                result.append(",")
                    .append(separator)
            } else {
                break
            }
        }

        val repeat = if (count == 0) {
            0
        } else {
            count - 1
        }

        result.append(separator)
            .append(SPACE.repeat(repeat))
            .append("}")

        return result.toString()
    }

    override fun toString(): String {
        return toString(1)
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