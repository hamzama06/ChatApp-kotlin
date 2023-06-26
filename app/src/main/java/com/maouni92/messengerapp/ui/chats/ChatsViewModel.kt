package com.maouni92.messengerapp.ui.chats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.repository.MessagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsViewModel(application: Application) : AndroidViewModel(application) {


   private val messagesRepository = MessagesRepository(application.applicationContext)
    var chats = messagesRepository.chats

    fun getAllChats(){
   viewModelScope.launch(Dispatchers.IO) {  messagesRepository.getAllChats() }
    }

    fun cancelChatsRegistration(){
        messagesRepository.cancelChatsRegistration()
    }

}