package com.example.fay.core.common.result

/**
 * Wrapper class representing the result of the fetch
 * of a resource from a data source, for example a network request.
 * Consumers can apply situational logic, depending on the subclass received.
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val code: Int, val message: String?) : Resource<Nothing>()
    data class Exception(val exception: Throwable) : Resource<Nothing>()
}