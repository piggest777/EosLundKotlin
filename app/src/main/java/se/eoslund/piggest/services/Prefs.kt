package se.eoslund.piggest.services

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE)

    private val TEAM_UPDATE_DATE_LONG = "teamUpdateDate"


    var teamUpdateDate: Long
        get() = preferences.getLong(TEAM_UPDATE_DATE_LONG, -1)
        set(value) = preferences.edit().putLong(TEAM_UPDATE_DATE_LONG, value).apply()
}