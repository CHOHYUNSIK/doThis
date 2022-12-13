package com.example.healthcare_exercise

import android.util.Log
import com.google.gson.JsonElement
import retrofit2.Call
import retrofit2.Response
import com.example.healthcare_exercise.Retrofitlmpl

class RetrofitManager {
    companion object {
        val instance = RetrofitManager()
        private const val URL = "http://58.127.238.25:5000/"
    }

    // get retrofit interface
    private val retrofitService : RetrofitService? = Retrofitlmpl.getClient(URL)?.create(RetrofitService::class.java)

    // api 호출
    fun analyseFin(path: String?, completion:(String)->Unit){

        // 만약 path: String? 이 비어있으면, "": String 으로 대체해라
        val term: String = path ?: ""
//        val term: String = path.let{
//            it
//        }?: ""

        // call
        val call = Retrofitlmpl.service.analyseFin(term)
//        val call = retrofitService?.analyseFin(searchTerm = term) ?: return
//        val call = retrofitService?.analyseFin(searchTerm = term).let{
//            it
//        }?: return

        // 호출
        call.enqueue(object: retrofit2.Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("retrofit", "response success ${response.raw()}")
                // 완료 되었을 때, response 를 String 으로 가져옴
                // 그냥 completion 만 하면, 알려주기만 하고 data 를 가져오지는 않는다
                completion(response.body().toString())
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("retrofit", "response failure")
                completion(t.toString())
            }
        })
    }
}