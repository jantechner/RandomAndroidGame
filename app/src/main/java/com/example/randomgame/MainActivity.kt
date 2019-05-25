package com.example.randomgame

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.example.randomgame.data.model.Result
import com.example.randomgame.utils.showToast
import com.example.randomgame.viewmodels.MainViewModel
import com.example.randomgame.viewmodels.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var model: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val login = intent.getStringExtra("USER_LOGIN")
        val score = intent.getIntExtra("USER_RESULT", 0)

        model = ViewModelProviders.of(this, MainViewModelFactory(login, score)).get(MainViewModel::class.java)
        model.totalScore.observe(this, Observer { totalScoreLabel.text = "Twój wynik: $it" })
        model.hint.observe(this, Observer { hintLabel.text = it })
        model.currentScore.observe(this, Observer { scoreLabel.text = if (it != 0) "Ilość prób: $it" else "" })

        model.toastNotification.observe(this, Observer {
            it?.getContentIfNotHandled()?.let { showToast(this@MainActivity, it) }
        })
        model.gameResult.observe(this, Observer {
            it?.getContentIfNotHandled()?.let {
                when (it) {
                    is Result.Failure -> showDialog(
                        "Przegrałeś!",
                        "Przekroczyłeś dopuszczoną liczbę strzałów!",
                        "OK"
                    )
                    is Result.Success -> showDialog(
                        "Wygrałeś!",
                        "Udało Ci się za ${it.data.score} razem!\nOtrzymujesz ${it.data.points} punktów!",
                        "Super"
                    )
                }
            }
        })

        rankingButton.setOnClickListener { startActivity(Intent(this, RankingActivity::class.java)) }
        newGameButton.setOnClickListener { model.setUpNewGame() }
        checkNumberButton.setOnClickListener { model.checkNumber(number.text.toString()); number.setText("") }
        logoutButton.setOnClickListener { showLogoutDialog() }
    }

    override fun onBackPressed() {
        showLogoutDialog()
    }

    private fun showDialog(title: String, message: String, buttonLabel: String) {
        AlertDialog.Builder(this@MainActivity)
            .setTitle(title).setMessage(message)
            .setPositiveButton(buttonLabel) { _, _ -> model.setUpNewGame() }
            .create().show()
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this@MainActivity).apply {
            setTitle("Czy na pewno chcesz się wylogować?")
            setPositiveButton("Tak") { _, _ -> logout() }
            setNegativeButton("Nie") { _, _ -> }
        }.create().show()
    }

    private fun logout() {
        model.logout()
        setResult(Activity.RESULT_OK)
        finish()
    }



}
