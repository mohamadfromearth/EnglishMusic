package com.example.englishmusic.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.englishmusic.R
import com.example.englishmusic.viewmodel.DownloadViewModel
import java.io.File
import java.lang.IllegalStateException

class DeleteDownloadDialog(private val callback:()->Unit):DialogFragment() {

    private lateinit var downloadViewModel:DownloadViewModel
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        downloadViewModel = ViewModelProvider(requireActivity()).get(DownloadViewModel::class.java)

        return activity?.let {
            val builder = AlertDialog.Builder(it, R.style.CustomAlertDialog)
            builder.setMessage("Do you wanna delete this music")
            builder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                File(tag).delete()
                downloadViewModel.getDownloadSong()
                callback()
                dismiss()
            })
            builder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dismiss()
            })

            builder.create()
        }?:throw IllegalStateException("Activity cannot be null")
    }
}