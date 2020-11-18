/*
 * Copyright 2020 Liam Niehus-Staab
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
