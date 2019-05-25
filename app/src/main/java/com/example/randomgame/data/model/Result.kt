package com.example.randomgame.data.model

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure<out T : Any>(val data: T?) : Result<T>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "$data"
            is Failure -> "$data"
        }
    }
}
