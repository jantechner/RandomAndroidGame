package com.example.randomgame.viewmodels

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.randomgame.MainActivity

class MainViewModelFactory(
    private val login: String,
    private val score: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(login, score) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}