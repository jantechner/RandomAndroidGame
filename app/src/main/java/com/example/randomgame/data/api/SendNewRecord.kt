package com.example.randomgame.data.api

import android.os.AsyncTask
import android.util.Log
import android.widget.Toast
import com.example.randomgame.MainActivity
import com.example.randomgame.R
import com.example.randomgame.viewmodels.MainViewModel
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class SendNewRecord(
    private val login: String,
    private val result: Int
) : AsyncTask<String, String, String>() {

    override fun doInBackground(vararg params: String?): String? {
        val url = URL("http://hufiecgniezno.pl/br/record.php?f=add&id=$login&r=$result")
        val connection = url.openConnection() as HttpURLConnection
        return try {
            val buffer = connection.inputStream.bufferedReader()
            var result = ""
            do {
                result = buffer.readLine()
                Log.d("DOWNLOAD RANKING", result)
            } while (result.trimStart().startsWith('<') || result.trim().isEmpty())
            result
        } catch (e: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}