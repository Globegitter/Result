package com.github.kittinunf.result.coroutines

inline fun <reified X> SuspendableResult<*, *>.getAs() = when (this) {
    is Success -> value as? X
    is Failure -> error as? X
}

inline infix fun <V: Any?, E: Exception> SuspendableResult<V, E>.getOrElse(fallback: (E) -> V): V {
    return when (this) {
        is Success -> value
        is Failure -> fallback(error)
    }
}

fun <V: Any?, E: Exception> SuspendableResult<V, E>.getOrNull(): V? {
    return when (this) {
        is Success -> value
        is Failure -> null
    }
}

suspend fun <V : Any?, E : Exception> SuspendableResult<V, E>.any(predicate: suspend (V) -> Boolean): Boolean = try {
    when (this) {
        is Success -> predicate(value)
        is Failure -> false
    }
} catch (ex: Exception) {
    false
}