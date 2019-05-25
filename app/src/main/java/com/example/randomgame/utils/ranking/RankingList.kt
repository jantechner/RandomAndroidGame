package com.example.randomgame.utils.ranking

import android.widget.Toast
import com.example.randomgame.R
import com.example.randomgame.utils.JSONHandler
import com.example.randomgame.data.database.DBHelper
import com.example.randomgame.RankingActivity
import com.example.randomgame.data.model.RankingEntry
import kotlinx.android.synthetic.main.activity_ranking.*

class RankingList(private val activity: RankingActivity?) {

    fun getFromLocalDatabase() {
        Toast.makeText(activity, activity?.getString(R.string.api_download_negative), Toast.LENGTH_SHORT).show()
        setRankingAdapter(DBHelper.helper.ranking)
    }

    fun setFromRemoteAPIRequest(result: String) {
        val rankingEntries = JSONHandler.convertToRankingEntryList(result)
        DBHelper.helper.updateResults(rankingEntries)
        setRankingAdapter(rankingEntries)
    }

    private fun setRankingAdapter(list: ArrayList<RankingEntry>) {
        val rankingListAdapter = RankingListAdapter(activity, list)
        activity?.ranking?.adapter = rankingListAdapter
    }
}