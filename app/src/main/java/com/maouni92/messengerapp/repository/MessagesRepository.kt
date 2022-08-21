package com.maouni92.messengerapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.FirebaseInstances
import com.maouni92.messengerapp.model.Message
import com.maouni92.messengerapp.model.MessageType
import com.maouni92.messengerapp.network.ApiClient
import com.maouni92.messengerapp.network.ApiService
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessagesRepository(val context: Context) {


    private var chatsList= ArrayList<Message>()
    var chats = MutableLiveData<ArrayList<Message>>()

    private var messagesList = ArrayList<Message>()
    var messages = MutableLiveData<ArrayList<Message>>()

    private var _isReceiverAvailable:Boolean = false
    var isReceiverAvailable = MutableLiveData<Boolean>()

    private lateinit var _friendToken:String
    var friendToken = MutableLiveData<String>()

   private val auth: FirebaseAuth by lazy { Firebase.auth }
   private  val messagesRef = FirebaseInstances.messagesRef
   private  val storageRef = FirebaseInstances.storageRef
    private val currentUser get() = auth.currentUser!!

    private val usersRef = FirebaseInstances.usersRef

    private  var chatsRegistration: ListenerRegistration? = null
    private  var messagesRegistration: ListenerRegistration? = null
    private  var friendChangesRegistration:ListenerRegistration? = null

   // private lateinit var _friendName:String
    //private lateinit var _friendImageUrl:String
   // private lateinit var _friendId: String

    fun getAllChats(){
        chatsRegistration =   usersRef.document(currentUser.uid).collection("chats").addSnapshotListener { value, error ->


            if (value == null) return@addSnapshotListener

            chatsList.clear()

            value.documents.forEach {
                val chat = it.toObject(Message::class.java)
                chatsList.add(chat!!)

            }

           chats.postValue(chatsList)
        }
    }



    fun cancelChatsRegistration(){

        chatsRegistration?.remove()
    }

    fun getAllMessages(friendId:String){

        Log.d("Messages Repository", "//////////////////// friend id : $friendId")
        messagesRegistration =   messagesRef.document(currentUser.uid)
            .collection(friendId).orderBy("time", Query.Direction.ASCENDING).addSnapshotListener { value, _ ->
                Log.d("Messages Repository", "//////////////////// inside snapshot messages")

                if (value == null) return@addSnapshotListener

                messagesList.clear()
                value.documents.forEach {
                    val message = it.toObject(Message::class.java)

                    //  messagesAdapter.add(message!!)
                    messagesList.add(message!!)

                }

                messages.postValue(messagesList)
             }
    }


    fun sendNewMessage(messageContent:String,userName:String,
                       userImageUrl:String,friendId:String,
                       friendName:String, friendImageUrl:String,
                       type:String, recyclerView: RecyclerView){
        val simpleDateFormat = SimpleDateFormat("EEE,MMM dd,yyyy,HH:mm", Locale.US)
        val date = simpleDateFormat.format(Date())


        val time = System.currentTimeMillis()


        val message = Message("", messageContent, currentUser.uid, currentUser.uid, friendId, friendName, friendImageUrl, type, date,time)
        val messageQuery = messagesRef.document(currentUser.uid).collection(friendId)

        //   messagesAdapter.add(message)

        messageQuery.add(message).addOnSuccessListener {

            message.messageId = it.id

            it.update("messageId", message.messageId)

           usersRef.document(currentUser.uid).collection("chats").document(friendId).set(message).addOnSuccessListener {

            }
            recyclerView.smoothScrollToPosition(messagesList.size-1)
            message.userId = friendId
            message.friendId = currentUser.uid
            message.friendName = userName
            message.friendImageUrl = userImageUrl
           messagesRef.document(friendId).collection(currentUser.uid).add(message)
            usersRef.document(friendId).collection("chats").document(currentUser.uid).set(message).addOnSuccessListener {

            }

            if (isReceiverAvailable.value == false){

                try {
                    val tokens = JSONArray()
                    tokens.put(friendToken.value)
                    val data = JSONObject()
                    data.put(Constants.USER_ID_KEY, currentUser.uid)
                    data.put(Constants.USER_NAME_KEY, userName)
                    data.put(Constants.USER_IMAGE_KEY, userImageUrl)
                    data.put(Constants.FCM_TOKEN_KEY, friendToken.value)
                    data.put(Constants.MESSAGE_KEY, if (type == MessageType.TEXT) messageContent else "Sent you an image")

                    val body = JSONObject()
                    body.put(Constants.REMOTE_MESSAGE_DATA, data)
                    body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens)

                    sendNotification(body.toString())

                }catch (exception: Exception){

                } }
        }
    }

    private fun sendNotification(messageBody: String){

        ApiClient.getClient.create(ApiService::class.java).sendMessage(
            Constants.getRemoteMessageHeader,
            messageBody
        ).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    try {
                        if (response.body() != null){

                            val responseJson = JSONObject(response.body()!!)
                            val results = responseJson.getJSONArray("results")
                            if (responseJson.getInt("failure") == 1){
                                return
                            }

                        }
                    }catch (e: JSONException){
                        Log.d("Messages repository", "/////////// notification exception : ${e.message}" )
                    }

                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {

            }

        })


    }

    fun sendImage(uri: Uri,userName:String,
                  userImageUrl:String,friendId:String,
                  friendName:String, friendImageUrl:String,
                  recyclerView: RecyclerView){

        val imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
        val imageBytes = outputStream.toByteArray()
        val ref =  storageRef.child("${currentUser.uid}/messages/${UUID.nameUUIDFromBytes(imageBytes)}")
        ref.putBytes(imageBytes).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                ref.downloadUrl.addOnSuccessListener {

                    sendNewMessage(it.toString(),userName, userImageUrl,friendId, friendName, friendImageUrl, MessageType.IMAGE, recyclerView)

                }}
        }
    }


    fun deleteMessage(itemPosition:Int){

        val message = messagesList[itemPosition]
         var friendId = message.friendId
        messagesList.removeAt(itemPosition)
        messagesRef.document(message.userId!!)
            .collection(friendId!!).document(message.messageId!!).delete().addOnCompleteListener {
                if (it.isSuccessful){
                    if (messagesList.isEmpty()){

                      usersRef.document(message.userId!!).collection("chats").document(friendId!!).delete()
                    }else if (itemPosition == messagesList.size ){
                        val lastMessage = messagesList.last()
                       usersRef.document(message.userId!!).collection("chats").document(friendId!!).set(lastMessage)
                    }

                    messages.postValue(messagesList)
                }
            }
    }




    fun cancelMessagesRegistration(){

        messagesRegistration?.remove()
    }

    fun listenReceiverChanges(friendId: String){


        friendChangesRegistration =  usersRef.document(friendId).addSnapshotListener { value, _ ->
            if (value != null){
                _isReceiverAvailable =  value[Constants.FRIEND_AVAILABILITY_KEY] as Boolean
                isReceiverAvailable.postValue(_isReceiverAvailable)
                if(value.contains("token")){
                    _friendToken = value["token"] as String
                    friendToken.postValue(_friendToken)
                //    _friendName = value[Constants.USER_NAME_KEY] as String
                 //   _friendImageUrl = value["imageUrl"] as String
                }
            }
        }
    }

    fun cancelFriendChangesRegistration(){
        friendChangesRegistration?.remove()
    }



}