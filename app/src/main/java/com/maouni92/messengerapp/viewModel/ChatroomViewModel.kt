package com.maouni92.messengerapp.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.maouni92.messengerapp.repository.MessagesRepository
import com.maouni92.messengerapp.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
    fun getAllMessages(friendId: String, recyclerView: RecyclerView){
       viewModelScope.launch(Dispatchers.IO) {
           messagesRepository.getAllMessages(friendId, recyclerView)
       }
    }

    fun sendNewMessage(messageContent:String,userName:String,
                       userImageUrl:String, friendId:String,
                       friendName:String, friendImageUrl:String,
                       type:String, recyclerView: RecyclerView){
      viewModelScope.launch(Dispatchers.IO) {messagesRepository.sendNewMessage(messageContent, userName, userImageUrl, friendId, friendName, friendImageUrl, type, recyclerView)  }
    }



    fun sendImage(uri: Uri, userName:String,
                  userImageUrl:String,friendId:String,
                  friendName:String, friendImageUrl:String,
                  recyclerView: RecyclerView){
    viewModelScope.launch(Dispatchers.IO) {  messagesRepository.sendImage(uri,userName,userImageUrl,friendId, friendName, friendImageUrl, recyclerView) }
    }

    fun deleteMessage(itemPosition:Int) = viewModelScope.launch(Dispatchers.IO) { messagesRepository.deleteMessage(itemPosition) }

    fun cancelMessagesRegistration() = viewModelScope.launch(Dispatchers.IO) { messagesRepository.cancelMessagesRegistration() }

    fun listenReceiverChanges(friendId:String) = messagesRepository.listenReceiverChanges(friendId)

    fun cancelFriendChangesRegistration() = messagesRepository.cancelFriendChangesRegistration()

}