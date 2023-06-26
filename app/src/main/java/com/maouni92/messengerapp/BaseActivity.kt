package com.maouni92.messengerapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.helper.Constants

open class BaseActivity : AppCompatActivity() {

    private val db : FirebaseFirestore by lazy {
        Firebase.firestore
    }

    private val usersRef : CollectionReference
        get() = db.collection("users")

    private val auth: FirebaseAuth by lazy{
        Firebase.auth
    }


    override fun onPause() {
        super.onPause()
        if (auth.currentUser != null){
            usersRef.document(auth.currentUser!!.uid).update(Constants.FRIEND_AVAILABILITY_KEY, false)
        }


    }

    override fun onResume() {
        super.onResume()
        usersRef.document(auth.currentUser!!.uid).update(Constants.FRIEND_AVAILABILITY_KEY, true)
    }
}