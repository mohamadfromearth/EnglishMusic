package com.example.englishmusic

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import com.example.englishmusic.api.RetrofitInstance
import com.example.englishmusic.databinding.ActivityLoginBinding
import com.example.englishmusic.model.Constance
import com.example.englishmusic.model.Username
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    @Inject
    lateinit var sharePref:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToMainActivity()
        animateInputs()
        binding.button.setOnClickListener {
            val name = binding.loginUsername.text.toString()
            val password = binding.passwordLogin.text.toString()
            if (name.isEmpty()) return@setOnClickListener Toast.makeText(this,"username is empty",
                Toast.LENGTH_SHORT).show()
            if (password.isEmpty()) return@setOnClickListener Toast.makeText(this,"password is empty",
                Toast.LENGTH_SHORT).show()

            CoroutineScope(Dispatchers.Main).launch {

                try {
                    binding.progressBar.visibility = View.VISIBLE
                    val response = RetrofitInstance.api.loginUser(Username(name,password))
                    if (response.isSuccessful){
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity,response.body()?.message, Toast.LENGTH_SHORT).show()

                        if (response.body()?.message != "A user with this name or password does not exists"){
                            val editor = sharePref.edit()
                            editor.putString("token",response.headers()["x-auth-token"])
                            editor.putString(Constance.USERNAME_KEY,name)
                            editor.putBoolean(Constance.IS_LOGIN,true)
                            editor.putBoolean(Constance.IS_ARTIST_SELECTED,true)
                            editor.apply()
                            val intent = Intent(this@LoginActivity,MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }

                    }

                }catch (t:Throwable){
                    binding.progressBar.visibility = View.GONE
                    when(t){
                        is IOException -> Toast.makeText(this@LoginActivity,"Network failure", Toast.LENGTH_SHORT).show()
                    }
                }


            }
        }
        binding.buttonRegister.setOnClickListener {
            val name = binding.registerUsername.text.toString()
            val password = binding.registerPassword.text.toString()
            val confirmPassword = binding.registerConfirmPassword.text.toString()
            if (name.isEmpty()) return@setOnClickListener Toast.makeText(this,"Username is empty",Toast.LENGTH_SHORT).show()
            if (password.isEmpty()) return@setOnClickListener Toast.makeText(this,"Password is empty",Toast.LENGTH_SHORT).show()
            if (password != confirmPassword) return@setOnClickListener Toast.makeText(this,"Confirm password is incorrect",Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    binding.progressBar.visibility = View.VISIBLE
                    val response = RetrofitInstance.api.registerUser(Username(name,password))
                    if (response.isSuccessful){
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(this@LoginActivity,response.body()?.message,Toast.LENGTH_SHORT).show()
                        if (response.body()?.message != "A user with this name already exists"){
                            val editor = sharePref.edit()
                            editor.putString("token",response.headers()["x-auth-token"])
                            editor.putBoolean(Constance.IS_LOGIN,true)
                            editor.apply()
                            startActivity(Intent(this@LoginActivity,SelectArtistActivity::class.java))
                            finish()
                }


                    }
                }catch (t:Throwable){
                    binding.progressBar.visibility = View.GONE
                    when(t){
                        is IOException -> Toast.makeText(this@LoginActivity,"Network failure",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }
        binding.signUpTxt.setOnClickListener {
            binding.loginUsername.visibility = View.GONE
            binding.passwordLogin.visibility = View.GONE
            binding.button.visibility = View.GONE
            binding.signUpTxt.visibility = View.GONE
            binding.registerUsername.visibility = View.VISIBLE
            binding.registerPassword.visibility = View.VISIBLE
            binding.registerConfirmPassword.visibility = View.VISIBLE
            binding.buttonRegister.visibility = View.VISIBLE
            binding.signInTxt.visibility = View.VISIBLE

        }
        binding.signInTxt.setOnClickListener {
            binding.loginUsername.visibility = View.VISIBLE
            binding.passwordLogin.visibility = View.VISIBLE
            binding.button.visibility = View.VISIBLE
            binding.signUpTxt.visibility = View.VISIBLE
            binding.registerUsername.visibility = View.GONE
            binding.registerPassword.visibility = View.GONE
            binding.registerConfirmPassword.visibility = View.GONE
            binding.buttonRegister.visibility = View.GONE
            binding.signInTxt.visibility = View.GONE
        }
    }
    private fun navigateToMainActivity(){
        val mainActivityIntent = Intent(this@LoginActivity,MainActivity::class.java)
        val isLogin = sharePref.getBoolean(Constance.IS_LOGIN,false)
        val isArtistSelected = sharePref.getBoolean(Constance.IS_ARTIST_SELECTED,false)
        if (isLogin && isArtistSelected){
          startActivity(mainActivityIntent)
            finish()
        }else if(isLogin){
            val selectArtistIntent = Intent(this,SelectArtistActivity::class.java)
            startActivity(selectArtistIntent)
            finish()
        }




    }
    private fun animateInputs() {
     ObjectAnimator.ofFloat(binding.loginUsername,"translationX",2000f).apply {
         duration = 1000
         start()
     }
        ObjectAnimator.ofFloat(binding.passwordLogin,"translationX",2000f).apply {
            duration = 1000
            start()
        }
        ObjectAnimator.ofFloat(binding.signUpTxt,"translationX",2000f).apply {
            duration = 1000
            start()
        }

        ObjectAnimator.ofFloat(binding.button,"translationX",2000f).apply {
            duration = 1000
            start()
        }


    }
    }

