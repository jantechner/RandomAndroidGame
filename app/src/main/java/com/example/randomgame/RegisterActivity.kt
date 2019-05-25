package com.example.randomgame

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.randomgame.data.database.DBHelper
import com.example.randomgame.data.model.Result
import com.example.randomgame.utils.afterTextChanged
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    val registerButtonIsEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        login.afterTextChanged {

        }

        registerButton.setOnClickListener {

            val result = DBHelper.helper.addUser(login.text.toString(), password.text.toString())

            if (result is Result.Success) {
                Toast.makeText(this@RegisterActivity, result.toString(), Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else if (result is Result.Failure){
                Toast.makeText(this@RegisterActivity, result.toString(), Toast.LENGTH_SHORT).show()
            }

        }
    }
}
