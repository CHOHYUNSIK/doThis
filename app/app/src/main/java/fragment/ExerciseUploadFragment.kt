package fragment

import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import com.example.healthcare_exercise.MainPageActivity
import com.example.healthcare_exercise.R
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.fragment_exercise_upload.view.*
import kotlinx.android.synthetic.main.fragment_exercise_upload.view.progress_bar
import kotlinx.android.synthetic.main.fragment_exercise_upload.view.tx_progress
import retrofit.Data
import retrofit.Request
import java.text.SimpleDateFormat
import java.util.*
import retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Response

class ExerciseUploadFragment : Fragment() {

    private var viewProfile : View?=null
    var fbStorage : FirebaseStorage?=null
    var uri : Uri? = null
    lateinit var name: String
    lateinit var email: String
    lateinit var str_uri : String
    var method = "unselected"
    lateinit var path: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewProfile = inflater.inflate(R.layout.fragment_exercise_upload, container, false)

        fbStorage = FirebaseStorage.getInstance()

        //사용자 정보 text set
        infoSet()

        //스피너 구현
        var methods = resources.getStringArray(R.array.methods)
        var adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, methods)
        this.viewProfile!!.spinner.adapter = adapter

        this.viewProfile!!.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                method = methods.get(position)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //storage에 동영상 upload 및 분석 요청
        this.viewProfile!!.btn_analyse.setOnClickListener(View.OnClickListener {
            // firebase
            funImageUpload(viewProfile!!)
//            requestResponse(path)
        })
        return viewProfile
    }

    private fun infoSet() {
        name = arguments?.getString("name").toString()
        email = arguments?.getString("email").toString()
        str_uri = arguments?.getString("uri").toString()
        uri = Uri.parse(str_uri)
        var timeStamp = SimpleDateFormat("yyMMdd_HH:mm").format(Date())
        this.viewProfile!!.video_view.setVideoURI(uri)
        this.viewProfile!!.video_view.setMediaController((MediaController(context)))
        this.viewProfile!!.video_view.start()
        this.viewProfile!!.tx_userName.text = name
        this.viewProfile!!.tx_date.text = timeStamp
    }

    private fun funImageUpload(view:View){
        //프로그레스바 loading 시작
        this.viewProfile!!.progress_bar.visibility = View.VISIBLE
        this.viewProfile!!.tx_progress.visibility = View.VISIBLE

        //layout 반투명화
        val paint = Paint()
        paint.alpha = 80
        this.viewProfile!!.exercise_layout.setBackgroundColor(paint.getColor())
        this.viewProfile!!.video_view.setBackgroundColor(paint.getColor())

        //upload
        var timeStamp = SimpleDateFormat("yyMMddHHmm").format(Date())
        var date = SimpleDateFormat("yyMMdd").format(Date())
//        var imgFileName = "VIDEO_"+timeStamp+"_.mp4"
        if(method.equals("업로드할 운동을 선택해주세요")) method = "unselected"
//        var storageRef = fbStorage?.reference?.child(email)?.child("exercise")?.child(method)?.child(date)?.child(imgFileName)
//        path = email+"/exercise/"+method+"/"+date
        path = email+"_exercise_"+method+"_"+timeStamp
        var imgFileName = path+".mp4"
        var storageRef = fbStorage?.reference?.child("temp/exercise/")?.child(imgFileName)

//        thread(start = true){
//            Thread.sleep(500)
//            this.viewProfile!!.progress_bar.incrementProgressBy(15)
//            Thread.sleep(500)
//            this.viewProfile!!.progress_bar.incrementProgressBy(15)
//            Thread.sleep(500)
//            this.viewProfile!!.progress_bar.incrementProgressBy(15)
//        }

        //업로드, 업로드 확인
        storageRef?.putFile(uri!!)?.addOnSuccessListener{
            //loading 끝냄
//            this.viewProfile!!.progress_bar.progress = 100
            requestResponse(path)
        }
    }

    //fragment  전환
    private fun changeFragment(){
        val activity = activity as MainPageActivity
        activity.setDataAtFragment(ExerciseAnalyseFragment(), str_uri, method, path)
    }

    //
    fun requestResponse(path: String){
        val call = RetrofitClient.service.loadResponse("temp/exercise/"+path)

        call.enqueue(object: retrofit2.Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if(response.isSuccessful){
                    var r = response.body().toString()
                    // 정상 response
                    Log.d("응답","complete: "+r)
                    if(r.equals("Result")){
                        Log.d("응답2","된다!!")
                    }
                    changeFragment()
                }
                else{
                    // mCallback.~~
                    Log.d("응답","response fail: "+response.body().toString())
                    changeFragment()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                //mCallback.~~()
                Log.d("응답","server connect fail: "+t.toString())
                changeFragment()
            }
        })
    }
}