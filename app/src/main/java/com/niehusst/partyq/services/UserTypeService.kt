package com.niehusst.partyq.services

import android.content.Context
import com.niehusst.partyq.SharedPrefNames.IS_HOST
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME

object UserTypeService {

    private var userIsHost: Boolean? = null

    fun setSelfAsHost(context: Context, code: String) {
        val prefs = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(IS_HOST, code)
        editor.apply()

        userIsHost = true
    }

    /**
     * Determine if the user is the host of the current party. Use shared prefs to persist
     * across app closure.
     */
    fun isHost(context: Context): Boolean {
        if (userIsHost == null) {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
            val hostCode = prefs.getString(IS_HOST, null)
            userIsHost = hostCode == TokenHandlerService.getToken(context)
        }

        return userIsHost!!
    }
}
