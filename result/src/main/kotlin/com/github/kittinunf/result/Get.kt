package com.github.kittinunf.result

inline fun <reified X> Result<*, *>.getAs() = when (this) {
    is Success -> value as? X
    is Failure -> error as? X
}

inline infix fun <V: Any?, E: Exception> Result<V, E>.getOrElse(fallback: (E) -> V): V {
    return when (this) {
        is Success -> value
        is Failure -> fallback(error)
    }
}

fun <V: Any?, E: Exception> Result<V, E>.getOrNull(): V? {
    return when (this) {
        is Success -> value
        is Failure -> null
    }
}

fun <V : Any?, E : Exception> Result<V, E>.any(predicate: (V) -> Boolean): Boolean = try {
    when (this) {
        is Success -> predicate(value)
        is Failure -> false
    }
} catch (ex: Exception) {
    false
}
