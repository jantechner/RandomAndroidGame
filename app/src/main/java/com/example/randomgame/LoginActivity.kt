package com.example.randomgame

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.randomgame.data.model.Result
import com.example.randomgame.utils.afterTextChanged
import com.example.randomgame.utils.showToast
import com.example.randomgame.viewmodels.LoginViewModel
import com.example.randomgame.viewmodels.LoginViewModelFactory
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {

    private lateinit var model: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        model = ViewModelProviders.of(this, LoginViewModelFactory()).get(LoginViewModel::class.java)

        model.loginFormState.observe(this, Observer {
            it?.let {
                login.isEnabled = it.isDataValid
                it.usernameError?.let { username.error = it }
                it.passwordError?.let { password.error = it }
            }
        })

        model.loginResult.observe(this, Observer {
            it?.let {
                loading.visibility = View.GONE
                when(it) {
                    is Result.Failure -> showToast(this@LoginActivity, it.data!!.error!!)
                    is Result.Success -> startActivity(Intent(this, MainActivity::class.java).apply {
                        putExtra("USER_LOGIN", it.data.user!!.login)
                        putExtra("USER_RESULT", it.data.user.result)
                    }) }
            }
        })

        username.afterTextChanged { model.loginDataChanged(username.text.toString(), password.text.toString()) }

        password.apply {
            afterTextChanged { model.loginDataChanged(username.text.toString(), password.text.toString()) }

            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) model.login(username.text.toString(), password.text.toString())
                false
            }
        }

        login.setOnClickListener {
            loading.visibility = View.VISIBLE
            model.login(username.text.toString(), password.text.toString())
        }

        newAccount.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
    }
}


