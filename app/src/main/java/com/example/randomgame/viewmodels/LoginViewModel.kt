package com.example.randomgame.viewmodels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.example.randomgame.data.LoginRepository
import com.example.randomgame.data.model.Result
import com.example.randomgame.data.database.DBHelper
import com.example.randomgame.data.model.LoginFormState
import com.example.randomgame.data.model.LoginResult

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginForm = MutableLiveData<LoginFormState>()
    private val _loginResult = MutableLiveData<Result<LoginResult>>()

    val loginFormState: LiveData<LoginFormState> = _loginForm
    val loginResult: LiveData<Result<LoginResult>> = _loginResult

    init {
        checkIfAlreadyLoggedIn()
    }

    fun login(username: String, password: String) {
        val result = loginRepository.login(username, password)
        _loginResult.value = when(result) {
            is Result.Success -> Result.Success(LoginResult(user = result.data))
            is Result.Failure -> Result.Failure(LoginResult(error = "Logowanie nieudane!"))
        }
    }

    fun loginDataChanged(username: String, password: String) {
        _loginForm.value = when {
            !isUserNameValid(username) -> LoginFormState(usernameError = "Nie podano nazwy użytkownika!")
            !isPasswordValid(password) -> LoginFormState(passwordError = "Hasło musi mieć więcej niż 5 znaków!")
            else -> LoginFormState(isDataValid = true)
        }
    }

    private fun checkIfAlreadyLoggedIn() {
        Log.d("LOGIN", "CHECK")
        val user = DBHelper.helper.getLoggedUser()
        Log.d("LOGIN", "${user?.login}")
        if (loginRepository.isLoggedIn) {
            Log.d("LOGIN", "CHECK")

            _loginResult.value = Result.Success(LoginResult(user = loginRepository.user))
        }
    }

    // A placeholder username and password validation check
    private fun isUserNameValid(username: String): Boolean { return username.isNotBlank() }
    private fun isPasswordValid(password: String): Boolean { return password.length > 5 }
}
