package com.zaky.capstone.ui.gallery

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.custom.*
import com.zaky.capstone.R
import com.zaky.capstone.adapter.ChatAdapter
import com.zaky.capstone.data.Chat
import com.zaky.capstone.data.Home
import com.zaky.capstone.databinding.FragmentGalleryBinding
import com.zaky.capstone.helper.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var galleryViewModel: GalleryViewModel
    private var lastItem : Int? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val context = container?.context
        loadingMulai()

        val db = Firebase.firestore
        val layout = LinearLayoutManager(context)
        binding.rvChat.layoutManager = layout
        val activity = getActivity()
        galleryViewModel = activity?.let { obtainViewModel(it, binding.root) }!!
        galleryViewModel.chat.observe(viewLifecycleOwner) {
            Log.d("TAG", "showRv: $it")
            val userAdapter = ChatAdapter()
            userAdapter.setListNotes(it)
            binding.rvChat.adapter = userAdapter
        }
        galleryViewModel.lastItem.observe(viewLifecycleOwner) {
            lastItem = it
            loadingSelesai()
        }



        binding.send.setOnClickListener {
            val message = binding.message.text.toString()
//            val date = "20:00"
            val sdf = SimpleDateFormat("HH:mm")
            val currentDate = sdf.format(Date())
            val id = lastItem?.toInt()
            if(id == null) {
                Snackbar.make(it, "Maaf, terdapat kesalahan. Mungkin koneksi anda buruk", Snackbar.LENGTH_LONG).show()
            } else {
                val chat = Chat(id, "you", message, currentDate)
                galleryViewModel.tambahData(chat as Chat)
                binding.message.text = null
            }


        }

//        val localModel = FirebaseCustomLocalModel.Builder()
//            .setAssetFilePath("model.tflite")
//            .build()
//
//        val options = FirebaseModelInterpreterOptions.Builder(localModel).build()
//        val interpreter = FirebaseModelInterpreter.getInstance(options)
//
//        val rumah = Home(true, true, true, true, true, true, true)

//        val inputOutputOptions = FirebaseModelInputOutputOptions.Builder()
////            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 224, 224, 3))
//            .setInputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 224, 224, 3))
////            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 5))
//            .setOutputFormat(0, FirebaseModelDataType.FLOAT32, intArrayOf(1, 5))
//            .build()

//        var inputan = arrayOf("Halo", "Hai")
//
//
//        val inputs = FirebaseModelInputs.Builder()
//            .add(inputan) // add() as many input arrays as your model requires
//            .build()
//        interpreter?.run(inputs, inputOutputOptions)
//            ?.addOnSuccessListener { result ->
//                // ...
//                Log.d("TAG ML", "onCreateView: $result")
//            }
//            ?.addOnFailureListener { e ->
//                // Task failed with an exception
//                Log.d("TAG ML", "onCreateView: $e")
//                // ...
//            }







        return root
    }

    fun obtainViewModel(activity: FragmentActivity, view: View) : GalleryViewModel {
        val factory = ViewModelFactory.getInstance(view)
        return ViewModelProvider(activity, factory).get(GalleryViewModel::class.java)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        val TAG = "Chat Fragment"
    }

    private fun loadingMulai() {
        binding.progress.visibility = View.VISIBLE
    }
    private fun loadingSelesai() {
        binding.progress.visibility = View.GONE
    }
}