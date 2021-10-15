package com.example.englishmusic.api

import com.example.englishmusic.model.Constance.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitInstance {

    companion object{
        private val retrofit by lazy {

            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

           val client = OkHttpClient.Builder().addInterceptor(logging).
               build()


            Retrofit.Builder().baseUrl("https://mohamadhosseinkhalili.iran.liara.run/api/").client(client).
                    addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api: MusicApi by lazy {
            retrofit.create(MusicApi::class.java)
        }
    }



}