package retrofit

import fragment.ExerciseUploadFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Request {
    fun requestResponse(path: String, mCallback:ExerciseUploadFragment){
        val call = RetrofitClient.service.loadResponse(path.toString())

        call.enqueue(object: Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.isSuccessful){
                    // ExerciseUploadFragment 안에 정상 response 시 함수 만들기
                }
                else{
                    // mCallback.~~
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //mCallback.~~()
            }
        })
    }
}