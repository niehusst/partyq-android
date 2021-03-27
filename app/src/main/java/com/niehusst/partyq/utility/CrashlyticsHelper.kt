/*
 * Copyright 2021 Liam Niehus-Staab
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

package com.niehusst.partyq.utility

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

object CrashlyticsHelper {
    var instance: FirebaseCrashlytics? = null

    fun recordException(throwable: Throwable) {
        instance?.recordException(throwable)
    }

    fun log(message: String) {
        instance?.log(message)
    }

    fun setCustomKey(key: FirebaseKeys, value: Any) {
        instance?.setCustomKey(key.key, value.toString())
    }

    fun getCrashReportingTree(): CrashReportingTree {
        return CrashReportingTree()
    }

    class CrashReportingTree : Timber.Tree() {
        /**
         * Write a log message to Firebase Crashlytics (if instance has been initialized).
         * Called for all level-specific methods by default.
         *
         * @param priority Log level. See [Log] for constants.
         * @param tag Explicit or inferred tag. May be `null`.
         * @param message Formatted log message. May be `null`, but then `t` will not be.
         * @param t Accompanying exceptions. May be `null`, but then `message` will not be.
         */
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            if (priority != Log.DEBUG && priority != Log.VERBOSE) {
                log(message)
                t?.let {
                    recordException(t)
                }
            }
        }

    }

    enum class FirebaseKeys(val key: String) {
        IS_HOST("isHost")
    }
}