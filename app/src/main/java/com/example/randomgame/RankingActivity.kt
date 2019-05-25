package com.example.randomgame

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.randomgame.data.api.GetRankingAsync

class RankingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ranking)
        downloadCurrentRanking()
    }

    fun backToGame(view: View) {
        finish()
    }

    private fun downloadCurrentRanking() {
        GetRankingAsync(this@RankingActivity).execute()
    }
}



