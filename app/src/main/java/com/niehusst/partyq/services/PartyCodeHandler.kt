package com.niehusst.partyq.services

import android.content.Context
import com.niehusst.partyq.SharedPrefNames.PARTY_CODE
import com.niehusst.partyq.SharedPrefNames.PARTY_FIRST_START
import com.niehusst.partyq.SharedPrefNames.PREFS_FILE_NAME
import kotlin.random.Random

object PartyCodeHandler {

    private var code: String? = null

    fun createPartyCode(context: Context) {
        val r = Random(System.nanoTime())
        code = "${randDigit(r)}${randDigit(r)}${randDigit(r)}${randDigit(r)}"

        // save the code into shared prefs
        setPartyCode(code!!, context)
    }

    private fun randDigit(r: Random) = r.nextInt(10)

    fun setPartyCode(partyCode: String, context: Context) {
        code = partyCode

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
}
