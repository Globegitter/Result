package com.github.kittinunf.result.coroutines

sealed class SuspendableResult<out V : Any?, out E : Exception> {

    open operator fun component1(): V? = null
    open operator fun component2(): E? = null

    suspend fun <X> fold(success: suspend (V) -> X, failure: suspend (E) -> X): X {
      return when (this) {
        is Success -> success(this.value)
        is Failure -> failure(this.error)
      }
    }

    abstract fun get(): V

    companion object {
        // Factory methods
        fun <V : Any?> of(value: V?, fail: (() -> Exception) = { Exception() }): SuspendableResult<V, Exception> {
            return value?.let { Success(it) } ?: Failure(fail())
        }

        suspend fun <V : Any?, E: Exception> of(f: suspend () -> V): SuspendableResult<V, E> = try {
            Success(f())
        } catch(ex: Exception) {
            Failure(ex as E)
        }
    }
    
}

class Success<out V : Any?>(val value: V) : SuspendableResult<V, Nothing>() {
    override fun component1(): V? = value

    override fun get(): V = value

    override fun toString() = "[Success: $value]"

    override fun hashCode(): Int = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Success<*> && value == other.value
    }
}

class Failure<out E : Exception>(val error: E) : SuspendableResult<Nothing, E>() {
    override fun component2(): E? = error

    override fun get() = throw error

    fun getException(): E = error

    override fun toString() = "[Failure: $error]"

    override fun hashCode(): Int = error.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is Failure<*> && error == other.error
    }
}
