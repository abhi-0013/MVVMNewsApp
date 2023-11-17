package com.androiddevs.mvvmnewsapp.api

import com.androiddevs.mvvmnewsapp.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class RetrofitInstance {
    companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val Client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .client(Client)
                .build()
        }


        // below code initializes the above instance to dynamically create implementation for the
        // News API retrofit interface using retrofit create function
        val api by lazy {
            retrofit.create(NewsAPI::class.java)
        }
    }
}