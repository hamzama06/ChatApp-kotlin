package com.maouni92.messengerapp.ui

import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.maouni92.messengerapp.databinding.ActivityLoginBinding
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.viewModel.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel:AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }else {
            window.statusBarColor = Color.WHITE
        }






        loginViewModel.user?.observe(this, Observer { user->
            if (user != null){
                Log.d("Login Activity", "/////////// user not null " )
                navigateTo( MainActivity::class.java)
                finish()
            }
        })

        binding.newAccountButton.setOnClickListener{
            navigateTo(SignupActivity::class.java)
        }

        binding.loginButton.setOnClickListener{
            login()
        }


    }


    private fun navigateTo(target:Class<*>){
        val intent = Intent(this, target)
        startActivity(intent)
    }

    private fun login(){
        val email = binding.emailLoginEt.text.toString()
        val pass = binding.passwordLoginEt.text.toString()
        val progressBar = binding.loginProgressBar

        if (email.isNotEmpty() && pass.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            loginViewModel.login(email, pass, object : FirebaseListener {
                override fun onCompleteListener() {
                    Log.d("Login Activity", "/////////// inside login activity login: login complete " )
                    progressBar.visibility = View.GONE
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                override fun onFailureListener() {
                    Log.d("Login Activity", "/////////// inside login activity login: login failed " )
                    progressBar.visibility = View.GONE
                }
            })
        }

    }
}