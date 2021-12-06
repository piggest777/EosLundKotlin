package se.eoslund.piggest.Services

import android.content.Context
import android.content.SharedPreferences
import io.grpc.InternalConfigSelector.KEY
import java.security.Key
import java.util.*

class Prefs(context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("prefs",Context.MODE_PRIVATE)

    private val TEAM_UPDATE_DATE_LONG = "teamUpdateDate"


    var teamUpdateDate: Long
        get() = preferences.getLong(TEAM_UPDATE_DATE_LONG, -1)
        set(value) = preferences.edit().putLong(TEAM_UPDATE_DATE_LONG, value).apply()
}