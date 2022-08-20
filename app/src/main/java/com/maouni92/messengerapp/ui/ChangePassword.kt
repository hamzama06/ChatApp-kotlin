package com.maouni92.messengerapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.databinding.ActivityChangePasswordBinding
import com.maouni92.messengerapp.helper.initStatusBar
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.ui.profile.ProfileViewModel

class ChangePassword : AppCompatActivity(), FirebaseListener {
    private lateinit var binding: ActivityChangePasswordBinding
    private val changePasswordViewModel:ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initStatusBar()
        setSupportActionBar(binding.changePassToolbar)
        supportActionBar?.let {
            it.title = getString(R.string.change_password)
            it.setHomeButtonEnabled(true)
            it.setDisplayHomeAsUpEnabled(true)

        }

        binding.changePassButton.setOnClickListener {
            if (isInputsValid()){
                changePassword()
            }
        }
    }

    private fun isInputsValid():Boolean{

        if (binding.currentPassEt.text.isEmpty()){
            binding.currentPassEt.error = getString(R.string.current_password_require_error)
            return false
        }
        if (binding.newPassEt.text.isEmpty()){
            binding.currentPassEt.error = getString(R.string.empty_new_password_field_error)
            return false
        }
        if (binding.confirmPassEt.text.isEmpty() && binding.confirmPassEt.text.toString() != binding.newPassEt.text.toString()){
            binding.currentPassEt.error = getString(R.string.confirm_password_error)
            return false
        }
        return true
    }

    private fun changePassword(){
        val newPassword = binding.newPassEt.text.toString()
        val currentPassword = binding.currentPassEt.text.toString()
        changePasswordViewModel.changePassword(currentPassword, newPassword, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCompleteListener() {
        finish()
    }

    override fun onFailureListener() {

    }
}