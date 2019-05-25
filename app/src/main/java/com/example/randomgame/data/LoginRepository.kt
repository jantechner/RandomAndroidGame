package com.example.randomgame.data

import com.example.randomgame.data.model.Result
import com.example.randomgame.data.model.User

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    var user: User? = dataSource.getLoggedUser()
        private set

    val isLoggedIn get() = user != null

    fun login(username: String, password: String): Result<User> {
        val result = dataSource.login(username, password)
        if (result is Result.Success) {
            user = result.data
            dataSource.updateLoggedUser(user)
        }
        return result
    }

}
