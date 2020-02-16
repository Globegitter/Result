package com.github.kittinunf.result

inline fun <V : Any?, U : Any?, E : Exception> Result<V, E>.map(transform: (V) -> U): Result<U, E> = try {
    when (this) {
        is Success -> Success(transform(value))
        is Failure -> Failure(error)
    }
} catch (ex: Exception) {
    Failure(ex as E)
}

inline fun <V : Any?, U : Any?, E : Exception> Result<V, E>.flatMap(transform: (V) -> Result<U, E>): Result<U, E> = try {
    when (this) {
        is Success -> transform(value)
        is Failure -> Failure(error)
    }
} catch (ex: Exception) {
    Failure(ex as E)
}

fun <V : Any?, E : Exception, E2 : Exception> Result<V, E>.mapError(transform: (E) -> E2) = when (this) {
    is Success -> Success(value)
    is Failure -> Failure(transform(error))
}

fun <V : Any?, E : Exception, E2 : Exception> Result<V, E>.flatMapError(transform: (E) -> Result<V, E2>) = when (this) {
    is Success -> Success(value)
    is Failure -> transform(error)
}

infix fun <V : Any?, E : Exception> Result<V, E>.or(fallback: V) = when (this) {
    is Success -> this
    else -> Success(fallback)
}

inline fun <V: Any?, E: Exception, X> Result<V, E>.fold(success: (V) -> X, failure: (E) -> X): X = when (this) {
    is Success -> success(this.value)
    is Failure -> failure(this.error)
}

fun <V : Any?> Result<V, *>.success(f: (V) -> Unit) = fold(f, {})

fun <E : Exception> Result<*, E>.failure(f: (E) -> Unit) = fold({}, f)

fun <V : Any?, U : Any?> Result<V, *>.fanout(other: () -> Result<U, *>): Result<Pair<V, U>, *> =
    flatMap { outer -> other().map { outer to it } }

fun <V : Any?, E : Exception> List<Result<V, E>>.lift(): Result<List<V>, E> = fold(Success(mutableListOf<V>()) as Result<MutableList<V>, E>) { acc, result ->
    acc.flatMap { combine ->
        result.map { combine.apply { add(it) } }
    }
}
