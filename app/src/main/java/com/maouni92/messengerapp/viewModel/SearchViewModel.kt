package com.maouni92.messengerapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val usersRepository = UsersRepository(application.applicationContext)
    var filteredUsers = usersRepository.filteredList


    fun getAllUser(listener: FirebaseListener?) = usersRepository.getUsers(listener)

    fun searchUsers(query:String) = usersRepository.searchUsers(query)
}