package com.maouni92.messengerapp.ui.chats

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.repository.MessagesRepository

class ChatsViewModel(application: Application) : AndroidViewModel(application) {


     private val messagesRepository = MessagesRepository(application.applicationContext)
    var chats = messagesRepository.chats

    /*
    private val _chatsList = MutableLiveData<ArrayList<Message>>().apply {


    }
    val text: LiveData<ArrayList<Message>> = _chatsList

    */

    fun getAllChats(){
        messagesRepository.getAllChats()
    }

    fun cancelChatsRegistration(){
        messagesRepository.cancelChatsRegistration()
    }

}