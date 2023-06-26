package com.maouni92.messengerapp.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.repository.AuthenticationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthenticationRepository(application.applicationContext)
    var user = authRepository.userLiveData


    fun login (email:String, password:String, AuthenticationListener: FirebaseListener){
        authRepository.login(email, password, AuthenticationListener)
    }

    fun register(name:String, email:String, password:String, authenticationListener: FirebaseListener ){
        authRepository.register(name, email,password, authenticationListener)
    }


}