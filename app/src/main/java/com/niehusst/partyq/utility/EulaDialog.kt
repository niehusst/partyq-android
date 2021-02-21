package com.niehusst.partyq.utility

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.niehusst.partyq.R
import com.niehusst.partyq.SharedPrefNames

class EulaDialog(
    private val activity: AppCompatActivity
) : AlertDialog(activity) {

    /**
     * Show the user the EULA. If they reject it, they are not allowed to use the app.
     */
    fun showDialog() {
        val editor = activity.getSharedPreferences(
            SharedPrefNames.PREFS_FILE_NAME, Context.MODE_PRIVATE).edit()
        val builder = Builder(activity)

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
