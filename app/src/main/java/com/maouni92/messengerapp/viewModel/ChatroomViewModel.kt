package com.maouni92.messengerapp.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.maouni92.messengerapp.repository.MessagesRepository
import com.maouni92.messengerapp.repository.UsersRepository

class ChatroomViewModel(application: Application): AndroidViewModel(application) {

    private val messagesRepository = MessagesRepository(application.applicationContext)
    private val usersRepository = UsersRepository(application.applicationContext)
    var messages = messagesRepository.messages
    var currentUser = usersRepository.currentUserInfo
    var isFriendAvailable = messagesRepository.isReceiverAvailable
    var friendToken = messagesRepository.friendToken


    init {
        usersRepository.getCurrentUser()
    }

    fun getAllMessages(friendsId:String) = messagesRepository.getAllMessages(friendsId)


    fun sendNewMessage(messageContent:String,userName:String, userImageUrl:String, type:String, recyclerView: RecyclerView){
        messagesRepository.sendNewMessage(messageContent, userName, userImageUrl, type, recyclerView)
    }

    fun sendImage(uri: Uri, userName:String, userImageUrl:String, recyclerView: RecyclerView){
        messagesRepository.sendImage(uri,userName,userImageUrl, recyclerView)
    }

    fun deleteMessage(itemPosition:Int) = messagesRepository.deleteMessage(itemPosition)

    fun cancelMessagesRegistration() = messagesRepository.cancelMessagesRegistration()

    fun listenReceiverChanges(friendId:String) = messagesRepository.listenReceiverChanges(friendId)

    fun cancelFriendChangesRegistration() = messagesRepository.cancelFriendChangesRegistration()

}