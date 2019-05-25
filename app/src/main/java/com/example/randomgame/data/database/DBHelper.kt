package com.example.randomgame.data.database

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.randomgame.data.model.Result
import com.example.randomgame.data.model.User
import com.example.randomgame.data.model.RankingEntry


class DBHelper(context: Context?): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VER
) {

    init {
        helper = this
        Log.d("DATABASE", "Helper initialized")
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createRankingTableQuery = "CREATE TABLE ${Ranking.TABLE_NAME} (" +
                                            "${Ranking.COL_NR} INTEGER PRIMARY KEY, " +
                                            "${Ranking.COL_NAME} VARCHAR(30), " +
                                            "${Ranking.COL_RESULT} INTEGER)"
        val createUsersTableQuery = "CREATE TABLE ${Users.TABLE_NAME} (" +
                                            "${Users.COL_ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                            "${Users.COL_LOGIN} VARCHAR(30), " +
                                            "${Users.COL_PASSWORD} VARCHAR(30), " +
                                            "${Users.COL_RESULT} INTEGER)"
        val createLoggedUserTableQuery = "CREATE TABLE ${LoggedUser.TABLE_NAME} (" +
                                            "${LoggedUser.COL_ID} INTEGER)"

        db.execSQL(createRankingTableQuery)
        db.execSQL(createUsersTableQuery)
        db.execSQL(createLoggedUserTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS ${Ranking.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${Users.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${LoggedUser.TABLE_NAME}")
        onCreate(db)
    }

    val ranking: ArrayList<RankingEntry>
        get() {
            val rankingList = ArrayList<RankingEntry>()
            val selectQuery = "SELECT * FROM ${Ranking.TABLE_NAME} ORDER BY ${Ranking.COL_NR} ASC"
            val db = this.readableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do {
                    val entry = RankingEntry(
                        cursor.getInt(cursor.getColumnIndex(Ranking.COL_NR)),
                        cursor.getString(cursor.getColumnIndex(Ranking.COL_NAME)),
                        cursor.getInt(cursor.getColumnIndex(Ranking.COL_RESULT))
                    )
                    rankingList.add(entry)
                } while (cursor.moveToNext())
            }
            cursor.close()
            db.close()
            return rankingList
        }

    fun updateResults(newRanking: ArrayList<RankingEntry>) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${Ranking.TABLE_NAME}")

        for (entry in newRanking) {
            val values = ContentValues().apply {
                put(Ranking.COL_NR, entry.nr)
                put(Ranking.COL_NAME, entry.index)
                put(Ranking.COL_RESULT, entry.result)
            }
            db.insert(Ranking.TABLE_NAME, null, values)
        }

        db.close()
    }

    fun updateMyResult(name: String, newResult: Int) {
        val db = this.writableDatabase

        db.update(
            Ranking.TABLE_NAME,
            ContentValues().apply { put(Ranking.COL_RESULT, newResult) },
            "${Ranking.COL_NAME} LIKE ?",
            arrayOf(name))

        db.update(
            Users.TABLE_NAME,
            ContentValues().apply { put(Users.COL_RESULT, newResult) },
            "${Users.COL_LOGIN} LIKE ?",
            arrayOf(name))

        db.close()
    }


    fun addUser(name: String, password: String) : Result<String> {
        val db = this.writableDatabase
        val count = DatabaseUtils.queryNumEntries(db,
            Users.TABLE_NAME, "${Users.COL_LOGIN} = ?", arrayOf(name))
        Log.d("DATABASE", "Liczba użytkowników o takim loginie: $count")

        return if ( count == 0L ) {
            val values = ContentValues().apply {
                put(Users.COL_LOGIN, name)
                put(Users.COL_PASSWORD, password)
                put(Users.COL_RESULT, 0)
            }
            db.insert(Users.TABLE_NAME, null, values)
            db.close()
            Result.Success("Pomyślnie stworzono nowego użytkownika!")
        } else {
            Result.Failure("Podany login już istnieje!")
        }
    }

    fun modifyUser(login: String, result: Int) {
        val db = this.writableDatabase

        db.update(
            Users.TABLE_NAME,
            ContentValues().apply { put(Users.COL_RESULT, result) },
            "${Users.COL_LOGIN} LIKE ?",
            arrayOf(login))

        db.close()

    }

    fun getLoggedUser() : User? {
        var user : User? = null

        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${Users.TABLE_NAME}, ${LoggedUser.TABLE_NAME} WHERE ${LoggedUser.COL_ID} = ${Users.COL_ID}", null)

        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndex(Users.COL_ID)),
                cursor.getString(cursor.getColumnIndex(Users.COL_LOGIN)),
                cursor.getInt(cursor.getColumnIndex((Users.COL_RESULT)))
            )
        }
        cursor.close()
        db.close()

        Log.d("DATABASE", "${user?.login} ${user?.result}")
        return user
    }

    fun updateLoggedUser(user: User?) {
        val db = this.writableDatabase
        Log.d("DATABASE", "Update logged user request ${user?.login} ${user?.id}")

        db.delete(LoggedUser.TABLE_NAME, null, null)
        db.insert(LoggedUser.TABLE_NAME, null, ContentValues().apply { put(
            LoggedUser.COL_ID, user?.id) })

        val cursor = db.rawQuery("SELECT * FROM ${LoggedUser.TABLE_NAME}", null)

        if (cursor.moveToFirst()) {
            do {
                Log.d("DATABASE", "${cursor.getInt(cursor.getColumnIndex(LoggedUser.COL_ID))}")
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
    }

    fun checkUser(name: String, password: String) : User? {

        var user : User? = null

        val db = this.readableDatabase

        val selection = "${Users.COL_LOGIN} = ? AND ${Users.COL_PASSWORD} = ?"
        val selectionArgs = arrayOf(name, password)

        val cursor = db.query(Users.TABLE_NAME, null, selection, selectionArgs, null, null, null)

        if (cursor.moveToFirst()) {
            user = User(
                cursor.getInt(cursor.getColumnIndex(Users.COL_ID)),
                cursor.getString(cursor.getColumnIndex(Users.COL_LOGIN)),
                cursor.getInt(cursor.getColumnIndex((Users.COL_RESULT)))
            )
        }
        cursor.close()
        db.close()

        return user
    }

    companion object {

        lateinit var helper: DBHelper

        private var DATABASE_VER = 7
        private const val DATABASE_NAME = "RandomGame.db"

        object Ranking : BaseColumns{
            const val TABLE_NAME = "RANKING"
            const val COL_NR = "nr"
            const val COL_NAME = "name"
            const val COL_RESULT = "result"
        }

        object Users : BaseColumns {
            const val TABLE_NAME = "USERS"
            const val COL_ID = "id"
            const val COL_LOGIN = "login"
            const val COL_PASSWORD = "password"
            const val COL_RESULT = "result"
        }

        object LoggedUser : BaseColumns {
            const val TABLE_NAME = "LOGGED_USER"
            const val COL_ID = "userId"
        }
    }
}