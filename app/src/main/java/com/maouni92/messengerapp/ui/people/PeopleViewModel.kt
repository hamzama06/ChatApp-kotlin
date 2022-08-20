package com.maouni92.messengerapp.ui.people

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.model.User
import com.maouni92.messengerapp.repository.UsersRepository

class PeopleViewModel(application: Application) : AndroidViewModel(application){


    private val usersRepository = UsersRepository(application.applicationContext)
    val users: LiveData<ArrayList<User>> get() = usersRepository.users


    fun getUsers(listener: FirebaseListener){
       usersRepository.getUsers(listener)
    }


}