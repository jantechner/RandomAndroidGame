package com.example.randomgame.data

import com.example.randomgame.data.database.DBHelper
import com.example.randomgame.data.model.Result
import com.example.randomgame.data.model.User

class LoginDataSource {

    fun login(username: String, password: String): Result<User> {
        return try {
            val user = DBHelper.helper.checkUser(username, password) ?: throw Throwable()
            Result.Success(user)
        } catch (e: Throwable) {
            Result.Failure(null)
        }
    }

    fun logout() {
        updateLoggedUser(null)
    }

    fun updateLoggedUser(user: User?) {
        DBHelper.helper.updateLoggedUser(user)
    }

    fun getLoggedUser(): User? = DBHelper.helper.getLoggedUser()

}

