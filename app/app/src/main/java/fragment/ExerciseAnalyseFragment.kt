package fragment

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.healthcare_exercise.R
import com.example.healthcare_exercise.databinding.FragmentExerciseAnalyseBinding
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_exercise_analyse.*
import kotlinx.android.synthetic.main.fragment_exercise_upload.*
import kotlinx.android.synthetic.main.fragment_exercise_upload.tx_userName
import kotlinx.android.synthetic.main.fragment_exercise_upload.view.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import retrofit.Data
import retrofit2.http.Url
import java.net.URL

class ExerciseAnalyseFragment : Fragment() {

    var uri : Uri? = null
    lateinit var binding:FragmentExerciseAnalyseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseAnalyseBinding.inflate(inflater, container, false)

        Toast.makeText(context, "분석 완료!",Toast.LENGTH_SHORT).show()

        var email = arguments?.getString("email").toString()
        var userName = arguments?.getString("name").toString()
        var method = arguments?.getString("method").toString()
        var str_uri = arguments?.getString("uri").toString()
        var path = arguments?.getString("path").toString()

        uri = Uri.parse(str_uri)

        binding.txUserName.text = userName
        binding.txMethod.text = method
        binding.videoView.setVideoURI(uri)
        binding.videoView.setMediaController((MediaController(context)))
        binding.videoView.start()

        var storage = FirebaseStorage.getInstance()
        var storageRef = storage.getReference()

//        storageRef.child(email+"/"+path+"_result/"+path+".json").downloadUrl.addOnSuccessListener {
//            var result: Data
//            result = getUserInfoFromFile(it)!!
//            Log.d("결과",result.toString())
//        }

        storageRef.child(email+"/"+path+"_result/"+path+"_graph.png").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(binding.imgGraph)
            Log.d("이미지", "graph 띄움")
        }
        storageRef.child(email+"/"+path+"_result/"+path+"_worst1.png").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(binding.imgWorstPose1)
            Log.d("이미지", "worst1 띄움")
        }
        storageRef.child(email+"/"+path+"_result/"+path+"_worst2.png").downloadUrl.addOnSuccessListener {
            Glide.with(this)
                .load(it)
                .into(binding.imgWorstPose2)
            Log.d("이미지", "worst2 띄움")
        }
        return binding.root
    }
}

//"temp/result/"+path+"_graph.png"
//"temp/result/"+path+"_good.png"

private fun getUserInfoFromFile(url: Uri): Data? {
    return Gson().fromJson(url.toString(), Data::class.java)
}