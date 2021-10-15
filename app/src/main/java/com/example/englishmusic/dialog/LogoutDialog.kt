package com.example.englishmusic.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.englishmusic.LoginActivity
import com.example.englishmusic.R
import com.example.englishmusic.model.Constance.Companion.LOGIN_SHARE_PREF
import java.lang.IllegalStateException

class LogoutDialog:DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val sharePref = requireContext().getSharedPreferences(LOGIN_SHARE_PREF,MODE_PRIVATE)
            val builder = AlertDialog.Builder(it,R.style.CustomAlertDialog)
            builder.setMessage("Do you wanna logout")
                .setPositiveButton("Yes",
                    DialogInterface.OnClickListener{id,dialog ->
                        val editor = sharePref.edit()
                        editor.clear()
                        editor.apply()
                        startActivity(Intent(activity,LoginActivity::class.java))
                        requireActivity().finish()

                    })
                .setNegativeButton("No",
                DialogInterface.OnClickListener{id,dialog ->
                    dismiss()
                })

            builder.create()
        }?: throw IllegalStateException("Activity cannot be null")
    }
}