package com.example.randomgame.viewmodels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.example.randomgame.data.LoginDataSource
import com.example.randomgame.data.api.SendNewRecord
import com.example.randomgame.data.database.DBHelper
import com.example.randomgame.data.model.Game
import com.example.randomgame.data.model.Result
import com.example.randomgame.utils.Event

class MainViewModel(val login: String, initScore: Int) : AndroidViewModel(Application()) {

    private val _totalScore = MutableLiveData<Int>().apply { value = initScore }
    private val _hint = MutableLiveData<String>().apply { value = "" }
    private val _currentScore = MutableLiveData<Int>().apply { value = 0 }
    private val _toastNotification = MutableLiveData<Event<String>>()
    private val _gameResult = MutableLiveData<Event<Result<Game>>>()

    init {
        _toastNotification.value = Event("Witaj, $login!")
        val user = DBHelper.helper.getLoggedUser()
        Log.d("LOGGED", "${user?.login}")
    }

    val totalScore: LiveData<Int> = _totalScore
    val hint: LiveData<String> = _hint
    val currentScore: LiveData<Int> = _currentScore
    val toastNotification: LiveData<Event<String>> = _toastNotification
    val gameResult: LiveData<Event<Result<Game>>> = _gameResult

    private var correctNumber = (0..20).random()
    private val points
        get() = when (_currentScore.value) {
            1 -> 5; in 2..4 -> 3; in 5..6 -> 2; in 7..10 -> 1; else -> 0
        }

    private fun updateTotalScore(points: Int) {
        _totalScore.value = _totalScore.value!!.plus(points)
        DBHelper.helper.modifyUser(login, _totalScore.value!!)
        DBHelper.helper.updateMyResult(login, _totalScore.value!!)
        updateResultInAPI()
        //setTotalScore()
    }

    fun setUpNewGame() {
        correctNumber = (0..20).random()
        _currentScore.value = 0
        _hint.value = ""
        _toastNotification.value = Event("Wylosowano nową liczbę!")
    }

    fun checkNumber(input: String) {
        when {
            input.isEmpty() -> _toastNotification.value = Event("Nie podano liczby!")
            input.toInt() < 0 || input.toInt() > 20 -> _toastNotification.value =
                Event("Podaj liczbę z przedziału (0, 20)!")
            else -> {
                _currentScore.value = _currentScore.value!!.inc()
                when {
                    _currentScore.value!! > 10 -> _gameResult.value = Event(Result.Failure(Game()))
                    input.toInt() > correctNumber -> _hint.value = "PODAJ MNIEJSZĄ!"
                    input.toInt() < correctNumber -> _hint.value = "PODAJ WIĘKSZĄ!"
                    else -> {
                        updateTotalScore(points)
                        _gameResult.value = Event(Result.Success(Game(points = points, score = _currentScore.value!!)))
                    }
                }
            }
        }
    }

    private fun updateResultInAPI() {
        val result = SendNewRecord(login, totalScore.value!!).execute().get()
        _toastNotification.value = when (result) {
            "OK" -> Event("Pomyślnie wysłano nowy wynik!")
            else -> Event("Nowy rekord nie został wysłany!")
        }
    }

    fun logout() {
        LoginDataSource().logout()
        DBHelper.helper.modifyUser(login, _totalScore.value!!)
    }


//    private fun setTotalScore(score: Int) {
//        val loginShared = getContext().getSharedPreferences("com.example.randomgame.prefs", 0)
//        val editor = loginShared.edit()
//        editor.putInt("record/Value", score)
//        editor.apply()
//    }
//
//    private fun getTotalScore() : Int {
//        val loginShared = getContext().getSharedPreferences("com.example.randomgame.prefs", 0)
//        return loginShared.getInt("record/Value", 0)
//    }
//
//    private fun getContext() = getApplication<Application>().applicationContext


}