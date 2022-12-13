package com.example.healthcare_exercise

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofitlmpl {
//    private var retrofitClient: Retrofit? = null
    private lateinit var retrofitClient: Retrofit

    fun getClient(URL:String): Retrofit? {
//        if (retrofitClient == null){
            retrofitClient = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
//        }
        return retrofitClient
    }

    val service: RetrofitService = retrofitClient.create(RetrofitService::class.java)
//    val service: RetrofitService = retrofitClient!!.create(RetrofitService::class.java)
}