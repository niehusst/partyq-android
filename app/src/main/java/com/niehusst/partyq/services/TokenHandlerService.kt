package com.niehusst.partyq.services

import android.content.Context
import android.content.SharedPreferences
import com.niehusst.partyq.SharedPrefNames.ACCESS_TOKEN
import com.niehusst.partyq.SharedPrefNames.EXPIRES_AT
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import java.util.concurrent.TimeUnit

object TokenHandlerService {

    private var token: String? = null
    private var expiresAt: Long = 0L

    private fun getSharedPreferences(appContext: Context): SharedPreferences {
        return appContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun getToken(context: Context): String {
        if (token == null) {
            val appContext = context.applicationContext
            val sharedPref = getSharedPreferences(appContext)

            token = sharedPref.getString(ACCESS_TOKEN, null)
            expiresAt = sharedPref.getLong(EXPIRES_AT, 0L)

            if (token == null || expiresAt < System.currentTimeMillis()) {
                throw Exception("Token is expired or doesn't exist")
            }
        }

        return token!!
    }

    fun setToken(context: Context, token: String, expiresIn: Int, unit: TimeUnit) {
        val appContext = context.applicationContext

        val now = System.currentTimeMillis()
        expiresAt = now + unit.toMillis(expiresIn.toLong())
        this.token = token

        val sharedPref = getSharedPreferences(appContext)
        val editor = sharedPref.edit()
        editor.putString(ACCESS_TOKEN, token)
        editor.putLong(EXPIRES_AT, expiresAt)
        editor.apply()
    }
}
