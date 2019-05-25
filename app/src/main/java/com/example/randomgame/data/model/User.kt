package com.example.randomgame.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val id: Int,
    val login: String,
    val result: Int)
