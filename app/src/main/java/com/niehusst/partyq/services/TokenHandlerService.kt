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
            val sharedPref = getSharedPreferences(context.applicationContext)

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

    fun clearToken(context: Context) {
        val sharedPref = getSharedPreferences(context.applicationContext)
        sharedPref.edit()
            .remove(ACCESS_TOKEN)
            .apply()
    }
}
