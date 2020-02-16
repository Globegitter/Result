package com.github.kittinunf.result.coroutines.conversion

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.Failure
import com.github.kittinunf.result.coroutines.Success
import com.github.kittinunf.result.coroutines.SuspendableResult

fun <V: Any, E: Exception> Result<V, E>.toSuspendableResult() = when (this) {
    is com.github.kittinunf.result.Success -> Success(value)
    is com.github.kittinunf.result.Failure -> Failure(error)
}

fun <V: Any, E: Exception> SuspendableResult<V, E>.toResult() = when (this) {
    is Success -> com.github.kittinunf.result.Success(value)
    is Failure -> com.github.kittinunf.result.Failure(error)
}


