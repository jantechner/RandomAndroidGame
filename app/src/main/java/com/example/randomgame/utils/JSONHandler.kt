package com.example.randomgame.utils

import com.example.randomgame.data.model.RankingEntry
import org.json.JSONArray

class JSONHandler {
    companion object {
        fun convertToRankingEntryList(jsonString: String) : ArrayList<RankingEntry> {
            val jsonArray = JSONArray(jsonString)
            val resultList = ArrayList<RankingEntry>()
            for (i in 0 until jsonArray.length()) {
                val item = JSONArray(jsonArray[i].toString())
                resultList.add(
                    RankingEntry(
                        i + 1,
                        item[1].toString(),
                        item[2].toString().toInt()
                    )
                )
            }
            return resultList
        }
    }
}