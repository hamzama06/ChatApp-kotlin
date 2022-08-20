package com.maouni92.messengerapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.repository.UsersRepository

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val usersRepository = UsersRepository(application.applicationContext)
    var filteredUsers = usersRepository.filteredList


    fun getAllUser(listener: FirebaseListener?) = usersRepository.getUsers(listener)

    fun searchUsers(query:String) = usersRepository.searchUsers(query)
}