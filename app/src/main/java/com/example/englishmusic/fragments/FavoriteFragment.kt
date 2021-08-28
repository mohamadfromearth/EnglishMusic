package com.example.englishmusic.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.englishmusic.MainActivity
import com.example.englishmusic.R
import com.example.englishmusic.adapters.SongAdapter
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.FragmentFavoriteBinding
import com.example.englishmusic.databinding.LoginRegisterBottomSheetBinding
import com.example.englishmusic.model.Username
import com.example.englishmusic.other.Resource
import com.example.englishmusic.other.Status
import com.example.englishmusic.viewmodel.MainViewModel
import com.example.englishmusic.viewmodel.MusicInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

class FavoriteFragment:Fragment(R.layout.fragment_favorite){


    private lateinit var musicInfoViewModel: MusicInfoViewModel

    private lateinit var mainViewModel: MainViewModel

    private lateinit var songAdapter: SongAdapter


    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var editor: SharedPreferences.Editor




var binding:FragmentFavoriteBinding? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavoriteBinding.bind(view)
        musicInfoViewModel = ViewModelProvider(requireActivity()).get(MusicInfoViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        sharedPreferences = (activity as MainActivity).applicationContext.getSharedPreferences("login", Context.MODE_PRIVATE)

        val isLogin =sharedPreferences.getBoolean("isLogin",false)
        mainViewModel.mediaItem.postValue(Resource.clean())

       // if (!isLogin) return showLoginRegisterDialog()

        addCustomAction()
        setUpRecyclerView()
        subscribeToObservers()
        songAdapter.setOnItemClick {
            mainViewModel.playOrToggleSong(it)
            CoroutineScope(Dispatchers.Main).launch {
                delay(50)
                findNavController().navigate(R.id.action_favoriteFragment_to_songPlayingFragment)
            }
        }





    }



private fun showLoginRegisterDialog(){
    val loginRegisterBottomSheet = BottomSheetDialog(requireContext())
    val binding:LoginRegisterBottomSheetBinding = LoginRegisterBottomSheetBinding.inflate(layoutInflater)
    loginRegisterBottomSheet.setContentView(binding.root)

    binding.signUpTxt.setOnClickListener {
        binding.usernameLogin.visibility = View.GONE
        binding.passwordLogin.visibility = View.GONE
        binding.login.visibility = View.GONE
        binding.signUpTxt.visibility = View.GONE

        binding.usernameSignUp.visibility = View.VISIBLE
        binding.passwordSignUp.visibility = View.VISIBLE
        binding.passwordConfSignUp.visibility = View.VISIBLE
        binding.signUp.visibility = View.VISIBLE
        binding.loginTxt.visibility = View.VISIBLE
    }

    binding.loginTxt.setOnClickListener {
        binding.usernameSignUp.visibility = View.GONE
        binding.passwordSignUp.visibility = View.GONE
        binding.passwordConfSignUp.visibility = View.GONE
        binding.signUp.visibility = View.GONE
        binding.loginTxt.visibility = View.GONE

        binding.usernameLogin.visibility = View.VISIBLE
        binding.passwordLogin.visibility = View.VISIBLE
        binding.login.visibility = View.VISIBLE
        binding.signUpTxt.visibility = View.VISIBLE
    }

    binding.login.setOnClickListener {
        val user = Username(binding.usernameLogin.text.toString(),binding.passwordLogin.text.toString())
      if (user.name.isEmpty() || user.password.isEmpty()) {
        return@setOnClickListener  Toast.makeText(requireContext(),"Fill the inputes",Toast.LENGTH_SHORT).show()
      }
          CoroutineScope(Dispatchers.Main).launch {
              try {
                  val response = RetrofitInstance.api.loginUser(user)
                  if (response.isSuccessful) {
                      Toast.makeText(requireContext(), response.body()?.message, Toast.LENGTH_SHORT)
                          .show()
                      if (response.body()?.message != "A user with this name or password does not exists") {
                          editor = sharedPreferences.edit()
                          editor.putString("username", user.name)
                          editor.putString("token", response.headers()["x-auth-token"])
                          editor.putBoolean("isLogin", true)
                          editor.apply()
                          loginRegisterBottomSheet.dismiss()
                      }

                  }

              } catch (t: Throwable) {
                  when (t) {
                      is IOException -> Toast.makeText(
                          requireContext(),
                          "Network failure",
                          Toast.LENGTH_SHORT
                      ).show()
                  }
              }

          }




    }

    binding.signUp.setOnClickListener {
        val user = Username(binding.usernameSignUp.text.toString(),binding.passwordSignUp.text.toString())

        if (user.name.isEmpty()   || user.password.isEmpty() ){
            return@setOnClickListener Toast.makeText(requireContext(),"Fill the inputs",Toast.LENGTH_SHORT).show()
        }

        if (binding.passwordSignUp.text.toString() != binding.passwordConfSignUp.text.toString()){
            return@setOnClickListener Toast.makeText(requireContext(),"Confirm password is incorrect",Toast.LENGTH_SHORT).show()
        }
        try {
            CoroutineScope(Dispatchers.Main).launch {
                val response = RetrofitInstance.api.registerUser(user)

                if (response.isSuccessful){
                    if (response.body()?.message != "A user with this name already exists"){
                        editor = sharedPreferences.edit()
                        editor.putString("username", user.name)
                        editor.putString("token", response.headers()["x-auth-token"])
                        editor.putBoolean("isLogin", true)
                        editor.apply()
                        loginRegisterBottomSheet.dismiss()
                    }
                }


            }


        }catch (t:Throwable){
            when(t){
                is IOException -> Toast.makeText(requireContext(),"Network failure",Toast.LENGTH_SHORT).show()
            }
        }







    }





    loginRegisterBottomSheet.show()

}


  private fun setUpRecyclerView(){
      songAdapter = SongAdapter()
      binding!!.favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext())
      binding!!.favoriteRecyclerView.adapter = songAdapter
  }


 private fun addCustomAction(){
     val token = sharedPreferences.getString("token","token").toString()
     val bundle = Bundle()
     bundle.putString("token",token)
     mainViewModel.addCustomAction(bundle,"favorite")
 }

 private fun subscribeToObservers(){






     mainViewModel.mediaItem.observe(viewLifecycleOwner, Observer { result ->


         when(result.status){
             Status.LOADING ->{

             }
             Status.SUCCESS ->{

                 result.data?.let {
                     Toast.makeText(requireContext(),it[0].name,Toast.LENGTH_SHORT).show()
                     songAdapter.differ.submitList(it)


                 }

             }

           Status.ERROR ->{

           }


         }



     })
 }




}
