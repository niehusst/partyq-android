package com.niehusst.partyq.services

import android.content.Context
import com.niehusst.partyq.SharedPrefNames.PARTY_CODE
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import kotlin.random.Random

object CommunicationService {

    private var code: String? = null

    fun createPartyCode(context: Context) {
        val r = Random(System.nanoTime())
        code = "${randDigit(r)}${randDigit(r)}${randDigit(r)}${randDigit(r)}" //TODO this should actually be based off of the network connection code from connection api

        // save the code into shared prefs
        val sharedPrefs = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()
        editor.putString(PARTY_CODE, code)
        editor.apply()
    }

    fun randDigit(r: Random) = r.nextInt(10)

    fun getPartyCode(context: Context): String? { //TODO: should be nullable?
        if (code == null) {
            val sharedPrefs = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
            code = sharedPrefs.getString(PARTY_CODE, null)
        }
        return code
    }

    fun connectToParty() {
        // TODO: start/connect to party net
    }

    fun sendSearchRequest(query: String) {
        // TODO: send/get search req
    }

    fun updateQueue() {
        // TODO: update queue
    }
}
