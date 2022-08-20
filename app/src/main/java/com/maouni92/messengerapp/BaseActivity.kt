package com.maouni92.messengerapp

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.FirebaseInstances
import com.maouni92.messengerapp.ui.profile.ProfileViewModel

open class BaseActivity : AppCompatActivity() {

    val viewModel : ProfileViewModel by viewModels()
    override fun onPause() {
        super.onPause()

      viewModel.updateUserAvailability(false)
      }

    override fun onResume() {
        super.onResume()
      viewModel.updateUserAvailability(true)
    }
}