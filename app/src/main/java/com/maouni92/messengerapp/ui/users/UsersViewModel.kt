package com.maouni92.messengerapp.ui.users

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UsersViewModel(application: Application) : AndroidViewModel(application){


    private val usersRepository = UsersRepository(application.applicationContext)
    val users: LiveData<ArrayList<User>> get() = usersRepository.users


    fun getUsers(listener: FirebaseListener){
      viewModelScope.launch(Dispatchers.IO) {usersRepository.getUsers(listener)}
    }
}