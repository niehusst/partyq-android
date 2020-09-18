package com.niehusst.partyq.services

import android.content.Context
import com.niehusst.partyq.SharedPrefNames.PARTY_CODE
import com.niehusst.partyq.SharedPrefNames.PARTY_FIRST_START
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import com.niehusst.partyq.network.models.Item
import kotlin.random.Random

object CommunicationService {

    private var code: String? = null

    fun createPartyCode(context: Context) {
        val r = Random(System.nanoTime())
        code = "${randDigit(r)}${randDigit(r)}${randDigit(r)}${randDigit(r)}"

        // save the code into shared prefs
        setPartyCode(code!!, context)
    }

    private fun randDigit(r: Random) = r.nextInt(10)

    fun setPartyCode(partyCode: String, context: Context) {
        val sharedPrefs = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putString(PARTY_CODE, partyCode)
            .putBoolean(PARTY_FIRST_START, true) // mark the party as just started
            .apply()
    }

    fun getPartyCode(context: Context): String? {
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

    fun updateQueue(item: Item) {
        // TODO: update queue
    }

    fun disconnectFromParty() {
        // TODO: send disconnect message to all connections, then disconnect from them
    }
}
