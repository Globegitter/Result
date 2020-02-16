package com.github.kittinunf.result.coroutines

suspend fun <V : Any?> SuspendableResult<V, *>.success(f: suspend (V) -> Unit) = fold(f, {})

suspend fun <E : Exception> SuspendableResult<*, E>.failure(f: suspend (E) -> Unit) = fold({}, f)

infix fun <V : Any?, E : Exception> SuspendableResult<V, E>.or(fallback: V) = when (this) {
    is Success -> this
    else -> Success(fallback)
}

suspend fun <V : Any?, U : Any?, E : Exception> SuspendableResult<V, E>.map(transform: suspend (V) -> U): SuspendableResult<U, E> = try {
    when (this) {
        is Success -> Success(transform(value))
        is Failure -> Failure(error)
    }
} catch (ex: Exception) {
    Failure(ex as E)
}

suspend fun <V : Any?, U : Any?, E : Exception> SuspendableResult<V, E>.flatMap(transform: suspend (V) -> SuspendableResult<U, E>): SuspendableResult<U, E> = try {
    when (this) {
        is Success -> transform(value)
        is Failure -> Failure(error)
    }
} catch (ex: Exception) {
    Failure(ex as E)
}

suspend fun <V : Any?, E : Exception, E2 : Exception> SuspendableResult<V, E>.mapError(transform: suspend (E) -> E2) = when (this) {
    is Success -> Success(value)
    is Failure -> Failure(transform(error))
}

suspend fun <V : Any?, E : Exception, E2 : Exception> SuspendableResult<V, E>.flatMapError(transform: suspend (E) -> SuspendableResult<V, E2>) = when (this) {
    is Success -> Success(value)
    is Failure -> transform(error)
}

suspend fun <V : Any?, U: Any> SuspendableResult<V, *>.fanout(other: suspend () -> SuspendableResult<U, *>): SuspendableResult<Pair<V, U>, *> =
    flatMap { outer -> other().map { outer to it } }


suspend fun <V : Any?, E : Exception> List<SuspendableResult<V, E>>.lift(): SuspendableResult<List<V>, E> = fold(Success(mutableListOf<V>()) as SuspendableResult<MutableList<V>, E>) { acc, result ->
    acc.flatMap { combine ->
        result.map { combine.apply { add(it) } }
    }
}