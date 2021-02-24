package com.niehusst.partyq.utility

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.niehusst.partyq.R
import com.niehusst.partyq.SharedPrefNames

object EulaDialog {

    /**
     * Show the user the EULA. If they reject it, they are not allowed to use the app.
     */
    fun showDialog(activity: AppCompatActivity) {
        val editor = activity.getSharedPreferences(
            SharedPrefNames.PREFS_FILE_NAME, Context.MODE_PRIVATE).edit()
        val builder = AlertDialog.Builder(activity, R.style.Theme_AppCompat_Light_Dialog_Alert_EULA)

        builder.apply {
            setTitle(R.string.license_agreement)
            setMessage(R.string.eula)

            setPositiveButton(R.string.agree) { dialog, _ ->
                // mark that this user has agreed
                editor.putBoolean(SharedPrefNames.AGREED_TO_EULA, true).apply()
                // continue app usage
                dialog.dismiss()
            }
            setNegativeButton(R.string.disagree) { _, _ ->
                // users are not allowed to use the app if they don't accept the EULA
                activity.finish()
            }

            // prevent dialog from being dismissed w/o accepting
            setCancelable(false)
        }

        builder.create().show()
    }
}
