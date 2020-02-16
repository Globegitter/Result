package com.github.kittinunf.result.coroutines.conversion

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.Failure
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert
import org.junit.Test

class ResultTest {

    @Test
    fun testToSuspendableResult() {
        val r = Result.of(1).toSuspendableResult()
        val r2 = Result.of<String>(null).toSuspendableResult()

        MatcherAssert.assertThat("Result is successfully converted", r is SuspendableResult, equalTo(true))
        MatcherAssert.assertThat("Result is successfully converted", r.get(), equalTo(1))

        val result = try {
            r2.get()
            false
        } catch (e: Exception) {
            true
        }

        MatcherAssert.assertThat("Result is successfully converted", r2 is SuspendableResult, equalTo(true))
        MatcherAssert.assertThat("Result is successfully converted", r2 is Failure, equalTo(true))
        MatcherAssert.assertThat("e is Failure type", result, equalTo(true))
    }

    @Test
    fun testToResult() {
        val r = runBlocking { SuspendableResult.of(1) }.toResult()
        val r2 = runBlocking { SuspendableResult.of<String>(null) }.toResult()

        MatcherAssert.assertThat("Result is successfully converted", r is Result, equalTo(true))
        MatcherAssert.assertThat("Result is successfully converted", r.get(), equalTo(1))

        val result = try {
            r2.get()
            false
        } catch (e: Exception) {
            true
        }

        MatcherAssert.assertThat("Result is successfully converted", r2 is Result, equalTo(true))
        MatcherAssert.assertThat("Result is successfully converted",
            r2 is com.github.kittinunf.result.Failure, equalTo(true))
        MatcherAssert.assertThat("e is Failure type", result, equalTo(true))
    }
}