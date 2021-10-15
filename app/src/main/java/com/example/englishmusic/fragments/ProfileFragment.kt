package com.example.englishmusic.fragments

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.R
import com.example.englishmusic.adapters.ProfileAdapter
import com.example.englishmusic.databinding.FragmentProfileBinding
import com.example.englishmusic.dialog.LogoutDialog
import com.example.englishmusic.model.ProfileList
import com.example.englishmusic.viewmodel.DownloadViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment:Fragment(R.layout.fragment_profile){
    private lateinit var profileAdapter: ProfileAdapter
    @Inject
    lateinit var sharePref:SharedPreferences
    private lateinit var downloadViewModel:DownloadViewModel
    var binding: FragmentProfileBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)
        animateView()
       // setUpRecyclerView()
        downloadViewModel = ViewModelProvider(requireActivity()).get(DownloadViewModel::class.java)
        downloadViewModel.getDownloadSong()
        binding!!.myArtistImg.setOnClickListener {
            navigateToMyArtists()
        }
        binding!!.myMusicImg.setOnClickListener {
            navigateToMySong()
        }
        binding!!.myAlbumImg.setOnClickListener {
            navigateToMyAlbums()
        }
        binding!!.myDownloadImg.setOnClickListener {
            navigateToDownloads()
        }
        binding!!.logOutImg.setOnClickListener {
            showLogOutDialog()
        }
        /*profileAdapter.setOnClickListener {
            when(it){
                "Log out" ->{
                    logOut()
                }
                "My songs" ->{
                    navigateToMySong()
                }
                "My artist"->{
                    navigateToMyArtists()
                }
                "Downloads"->{
                    navigateToDownloads()
                }
                "My albums"->{
                    navigateToMyAlbums()
                }
            }
        }*/
    }
//    private fun setUpRecyclerView(){
//        val data = ArrayList<ProfileList>()
//        data.add(ProfileList("My artist",R.drawable.ic_microphone))
//        data.add(ProfileList("My songs",R.drawable.ic_romantic_music__1_))
//        data.add(ProfileList("My albums",R.drawable.ic_music_album))
//        data.add(ProfileList("Downloads",R.drawable.ic_download))
//       // data.add(ProfileList("Log out",R.drawable.ic_logout))
//        profileAdapter = ProfileAdapter(data)
//        binding!!.profileRecyclerView.layoutManager = GridLayoutManager(requireContext(),2)
//        binding!!.profileRecyclerView.adapter = profileAdapter
//    }
    private fun logOut(){

        showLogOutDialog()




    }
    private fun navigateToMySong(){
        findNavController().navigate(R.id.action_profileFragment_to_mySongFragment)
    }
    private fun navigateToMyArtists(){
        findNavController().navigate(R.id.action_profileFragment_to_myArtistsFragment)
    }
    private fun navigateToDownloads(){
        findNavController().navigate(R.id.action_profileFragment_to_downloadFragment)
    }
    private fun navigateToMyAlbums(){
        findNavController().navigate(R.id.action_profileFragment_to_myAlbumsFragment)
    }

    private fun showLogOutDialog(){
        val logoutDialog = LogoutDialog()
        logoutDialog.show(childFragmentManager,"Logout")

    }


    private fun animateView(){



        ObjectAnimator.ofFloat(binding!!.myArtistImg,"translationX",1500f).apply {
            duration = 1000
            start()
        }
        ObjectAnimator.ofFloat(binding!!.myAlbumImg,"translationX",1500f).apply {
            duration = 1000
            start()
        }

        ObjectAnimator.ofFloat(binding!!.myMusicImg,"translationX",-1500f).apply {
            duration = 1000
            start()
        }

        ObjectAnimator.ofFloat(binding!!.myDownloadImg,"translationX",-1500f).apply {
            duration = 1000
            start()
        }
        ObjectAnimator.ofFloat(binding!!.logOutImg,"translationX",1500f).apply {
            duration = 1000
            start()
        }
    }






}
