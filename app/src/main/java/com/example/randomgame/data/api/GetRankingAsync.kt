package com.example.randomgame.data.api

import android.os.AsyncTask
import android.util.Log
import com.example.randomgame.RankingActivity
import com.example.randomgame.utils.ranking.RankingList
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class GetRankingAsync(private val activity: RankingActivity?) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String? {
        val url = URL("http://hufiecgniezno.pl/br/record.php?f=get")
        val connection = url.openConnection() as HttpURLConnection
        return try {
            val buffer = connection.inputStream.bufferedReader()
            var result = ""
            do {
                result = buffer.readLine()
                Log.d("DOWNLOAD RANKING", result)
            } while (!result.trimStart().startsWith('['))

            result
        } catch (e: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
//        Log.d("DOWNLOAD RANKING", result)
        when (result) {

            null -> RankingList(activity).getFromLocalDatabase()
            else -> RankingList(activity).setFromRemoteAPIRequest(result)
        }
    }
}