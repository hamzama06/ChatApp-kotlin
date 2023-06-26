package com.maouni92.messengerapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.databinding.ActivitySignupBinding
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.viewModel.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    private val signupViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else {
            window.statusBarColor = Color.WHITE
        }

        binding.backSignupButton.setOnClickListener{
            finish()
        }

        binding.signupButton.setOnClickListener{
            checkInputs()
        }


    }

    private fun createNewUser(name:String, email:String, password:String){

        val progressBar = binding.signupProgressBar

        progressBar.visibility = View.VISIBLE

        signupViewModel.register(name,email,password, object : FirebaseListener{
            override fun onCompleteListener() {
                progressBar.visibility = View.GONE
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("is_first_access", true )
                startActivity(intent)
                finish()
            }

            override fun onFailureListener() {
                progressBar.visibility = View.GONE
            }
        })

    }

    // Check inputs if valid before create new user
    private fun checkInputs(){
        val name = binding.nameSignupEt.text.toString()
        val email = binding.emailSignupEt.text.toString()
        val password = binding.passwordSignupEt.text.toString()
        val passConfirm = binding.confirmPassEt.text.toString()

        if (name.isEmpty()) {
            binding.nameSignupEt.error = getString(R.string.name_required_error)
            binding.nameSignupEt.requestFocus()
            return
        }

        if (email.isEmpty()  || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailSignupEt.error = getString(R.string.valid_email_error)
            binding.emailSignupEt.requestFocus()
            return
        }

        if(password.length < 6 ){
            binding.passwordSignupEt.error = getString(R.string.password_length_error)
            binding.passwordSignupEt.requestFocus()
            return
        }

        if (password != passConfirm){
            binding.confirmPassEt.error = getString(R.string.confirm_password_error)
            binding.confirmPassEt.requestFocus()
            return
        }

        createNewUser(name, email, password)

    }
}