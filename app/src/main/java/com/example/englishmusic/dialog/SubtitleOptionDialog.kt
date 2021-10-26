package com.example.englishmusic.dialog

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SubtitleOptionAdapter
import com.example.englishmusic.databinding.DialogSubtitleOptionBinding
import com.example.englishmusic.model.Constance.Companion.SHOW_EN_SUBTITLE
import com.example.englishmusic.model.Constance.Companion.SHOW_PER_SUBTITLE
import com.example.englishmusic.model.lyric.SubtitleOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SubtitleOptionDialog(private val callback:()->Unit) : BottomSheetDialogFragment() {


    @Inject
    lateinit var sharePref:SharedPreferences


    private val data = ArrayList<SubtitleOption>()


    private lateinit var subtitleOptionAdapter:SubtitleOptionAdapter

    private lateinit var binding:DialogSubtitleOptionBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_subtitle_option,container,false)
        binding = DialogSubtitleOptionBinding.bind(view)
        submitListToArraylist()
        setUpRecyclerView()
        subtitleOptionAdapter.setOnCheckBoxClickListener { it,isChecked ->
            val editor = sharePref.edit()
            when(it.name){
                "English sub" -> editor.putBoolean(SHOW_EN_SUBTITLE,isChecked)
                "Persian sub" -> editor.putBoolean(SHOW_PER_SUBTITLE,isChecked)

            }
            editor.apply()
            callback()
            dismiss()
        }

         return binding.root
    }


    private fun setUpRecyclerView(){
        subtitleOptionAdapter = SubtitleOptionAdapter(data)
        binding.subtitleOptionsRecyclerView.adapter = subtitleOptionAdapter
        binding.subtitleOptionsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun submitListToArraylist(){
        val showENSub = sharePref.getBoolean(SHOW_EN_SUBTITLE,false)
        val showPerSub =sharePref.getBoolean(SHOW_PER_SUBTITLE,false)
        data.add(SubtitleOption("English sub",showENSub))
        data.add(SubtitleOption("Persian sub",showPerSub))
    }



}