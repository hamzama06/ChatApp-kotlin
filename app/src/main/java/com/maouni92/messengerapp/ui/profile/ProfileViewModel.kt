package com.maouni92.messengerapp.ui.profile

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.repository.AuthenticationRepository
import com.maouni92.messengerapp.repository.UsersRepository

class ProfileViewModel(application: Application) : AndroidViewModel(application) {


 private val authRepository = AuthenticationRepository(application.applicationContext)
 private val usersRepository = UsersRepository(application.applicationContext)

 var currentUser = usersRepository.currentUserInfo

    fun getCurrentUser() = usersRepository.getCurrentUser()

    fun changeUserName(name:String) = usersRepository.changeUserName(name)

    fun uploadProfileImage(uri:Uri) = usersRepository.uploadProfileImage(uri)

    fun changePassword(currentPassword:String, newPassword:String, listener: FirebaseListener) {
        usersRepository.changePassword(currentPassword, newPassword, listener)
    }

    fun updateUserAvailability(isAvailable:Boolean){
        usersRepository.updateUserAvailability(isAvailable)
    }

    fun signOut() = authRepository.signOut()





}



