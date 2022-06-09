package com.zaky.capstone.ui.gallery

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult
import com.zaky.capstone.R
import com.zaky.capstone.adapter.ChatAdapter
import com.zaky.capstone.data.Chat
import java.util.*
import kotlin.collections.ArrayList

class GalleryViewModel(view: View) : ViewModel() {
    val db = Firebase.firestore
    val view = view

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    private val _chat = MutableLiveData<ArrayList<Chat>>()
    val chat : LiveData<ArrayList<Chat>> = _chat
    private val _lastItem = MutableLiveData<Int>()
    val lastItem : LiveData<Int> = _lastItem

    val conversation = ArrayList<FirebaseTextMessage>()

    init {
        cariData()
        snapShoot()
        buatChatBot()

    }

    private fun cariData () {
        Log.d("AG", "run: Detik ke ...")
        val adah = ArrayList<Chat>()
        val docRef = db.collection("chats")
        docRef.orderBy("id")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    document.forEach {
                        Log.d(GalleryFragment.TAG, "id = ${it.id}, data = ${it.data}")
                        val item = Chat(it.get("id").toString().toInt(), it.get("user").toString(), it.get("message").toString(), it.get("date").toString())
                        Log.d(GalleryFragment.TAG, "onCreateView Model: $item")
                        adah.add(item)
                    }
                    _chat.postValue(adah)
                    _lastItem.postValue(adah.size)
                } else {
                    Log.d(GalleryFragment.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Snackbar.make(view, "Maaf, terdapat kesalahan. Mungkin koneksi anda buruk", Snackbar.LENGTH_LONG).show()
            }


    }
    fun snapShoot () {
        val docRef = db.collection("users")
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                Log.d("TAG", "Current data: ${snapshot}")
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    fun tambahData (chat: Chat) {
        db.collection("chats")
            .add(chat)
            .addOnSuccessListener { documentReference ->
                Log.d(GalleryFragment.TAG, "DocumentSnapshot added with ID: ")
                cariData()
            }
            .addOnFailureListener { e ->
                Log.w(GalleryFragment.TAG, "Error adding document", e)
            }
    }


    companion object {
        val ONE_SECOND = 1000
    }

    fun buatChatBot() {
        conversation.add(FirebaseTextMessage.createForLocalUser(
            "How are you", System.currentTimeMillis()));
        balasChatBot("How are you")
    }

    fun balasChatBot(text : String) {
        val smartReply = FirebaseNaturalLanguage.getInstance().smartReply
        smartReply.suggestReplies(conversation)
            .addOnSuccessListener { result ->
                if (result.getStatus() == SmartReplySuggestionResult.STATUS_NOT_SUPPORTED_LANGUAGE) {
                    // The conversation's language isn't supported, so the
                    // the result doesn't contain any suggestions.
                } else if (result.getStatus() == SmartReplySuggestionResult.STATUS_SUCCESS) {
                    // Task completed successfully
                    // ...
                    for (suggestion in result.suggestions) {
                        val replyText = suggestion.text
                        Log.d("Balasan Chat", "balasChatBot: $replyText")
                    }
                }
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
    }




}