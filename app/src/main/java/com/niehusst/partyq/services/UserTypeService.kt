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

    fun setSelfAsGuest(context: Context) {
        clearHostData(context)

        userIsHost = false
    }

    fun clearHostData(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .remove(IS_HOST)
            .apply()

        userIsHost = null
    }

    /**
     * Determine if the user is the host of the current party. Use shared prefs to persist
     * across process death.
     */
    fun isHost(context: Context): Boolean {
        if (userIsHost == null) {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)
            val hostCode = prefs.getString(IS_HOST, null)
            userIsHost = hostCode == PartyCodeHandler.getPartyCode(context)
        }

        return userIsHost!!
    }
}
