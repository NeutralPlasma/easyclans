package eu.virtusdevelops.easyclans.api

sealed class Result<out T> {

    data class Ok<out T>(val result: T): Result<T>()

    data class Error(
        val message: String?,
        val throwable: Throwable
    ): Result<Nothing>()


    fun isSuccess(): Boolean = this is Ok<*>

    fun isError(): Boolean = this is Error
}

typealias Success = Result.Ok<Nothing>