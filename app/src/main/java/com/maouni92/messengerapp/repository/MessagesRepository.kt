package com.maouni92.messengerapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.AbsListView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
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

    private lateinit var lastVisible:DocumentSnapshot
    private var isScrolling = false
    private var isLastItem = false

    fun getAllMessages(friendId: String, recyclerView: RecyclerView){
        val firstQuery = messagesRef.document(currentUser.uid)
            .collection(friendId).orderBy("time", Query.Direction.ASCENDING).limit(15)
        messagesRegistration =  firstQuery.addSnapshotListener {documentSnapshots,_ ->
            if (documentSnapshots == null) return@addSnapshotListener
            messagesList.clear()
            documentSnapshots.documents.forEach {
                val message = it.toObject(Message::class.java)
                messagesList.add(message!!) }
            messages.postValue(messagesList)
            Log.d("Messages repo", "//////// page loaded" )
           lastVisible = documentSnapshots.documents[documentSnapshots.size() - 1]
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                        isScrolling = true
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount

                        if (isScrolling && (firstVisibleItemPosition + visibleItemCount == totalItemCount) && !isLastItem){
                            isScrolling = false
                       val nextQuery =  messagesRef.document(currentUser.uid)
                             .collection(friendId).orderBy("time", Query.Direction.ASCENDING)
                             .startAfter(lastVisible)
                             . limit(15)

                          nextQuery.addSnapshotListener {query,_->
                                if (query != null){
                                    query.documents.forEach {
                                        val message = it.toObject(Message::class.java)
                                        messagesList.add(message!!)

                                    }
                                    messages.postValue(messagesList)
                                    lastVisible = query.documents[query.size()-1]
                                    if (query.documents.size < 15){
                                        isLastItem = true
                                    }
                                } } }
                    }
                }
            })
        }
    }


    fun getAllChats(){
        chatsRegistration =   usersRef.document(auth.currentUser!!.uid).collection("chats").addSnapshotListener { value, error ->


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

   fun sendNewMessage(messageContent:String,userName:String, userImageUrl:String,friendId:String,
                      friendName:String,friendImageUrl:String, type:String, recyclerView: RecyclerView){
        val simpleDateFormat = SimpleDateFormat("EEE,MMM dd,yyyy,HH:mm", Locale.US)
        val date = simpleDateFormat.format(Date())
        val time = System.currentTimeMillis()
        val message = Message("", messageContent, currentUser.uid, currentUser.uid, friendId, friendName, friendImageUrl, type, date,time)
        val messageQuery = messagesRef.document(currentUser.uid).collection(friendId)

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

            if (_isReceiverAvailable){

                try {
                    val tokens = JSONArray()
                    tokens.put(friendToken)
                    val data = JSONObject()
                    data.put(Constants.USER_ID_KEY, currentUser.uid)
                    data.put(Constants.USER_NAME_KEY, userName)
                    data.put(Constants.USER_IMAGE_KEY, userImageUrl)
                    data.put(Constants.FCM_TOKEN_KEY, friendToken)
                    data.put(Constants.MESSAGE_KEY, if (type == MessageType.TEXT) messageContent else "Sent you an image")

                    val body = JSONObject()
                    body.put(Constants.REMOTE_MESSAGE_DATA, data)
                    body.put(Constants.REMOTE_MESSAGE_REGISTRATION_IDS, tokens)

                    sendNotification(body.toString())

                }catch (e: Exception){
                    Log.d("Messages repository", "${e.message}" )
                }
            }
        }
    }


     fun sendImage(uri: Uri,userName:String, userImageUrl:String,friendId:String,
                   friendName:String,friendImageUrl:String, recyclerView: RecyclerView){

            val imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            val outputStream = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
            val imageBytes = outputStream.toByteArray()
            val ref = storageRef.child("${currentUser.uid}/messages/${UUID.nameUUIDFromBytes(imageBytes)}")

            ref.putBytes(imageBytes).addOnCompleteListener{ task ->
                if (task.isSuccessful){

                    ref.downloadUrl.addOnSuccessListener {

                        sendNewMessage(it.toString(),userName, userImageUrl,friendId, friendName,friendImageUrl, MessageType.IMAGE, recyclerView)

                    }


                }
            }
    }


  fun deleteMessage(itemPosition:Int){

        val message = messagesList[itemPosition]
      val  friendId = message.friendId ?: return

      messagesList.removeAt(itemPosition)
        messagesRef.document(message.userId!!)
            .collection(friendId).document(message.messageId!!).delete().addOnCompleteListener {
                if (it.isSuccessful){
                    if (messagesList.isEmpty()){

                        usersRef.document(message.userId!!).collection("chats").document(friendId).delete()
                    }else if (itemPosition == messagesList.size ){
                        val lastMessage = messagesList.last()
                        usersRef.document(message.userId!!).collection("chats").document(friendId).set(lastMessage)
                    }

                    messages.postValue(messagesList)
                }
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
                                var error:JSONObject = results.get(0) as JSONObject
                                return
                            }

                        }
                    }catch (e: JSONException){
                        Log.d("Messages repository", "notification exception : ${e.message}" )
                    }

                    Log.d("Messages repository", "///// notification sent" )
                }else{
                    Log.d("Messages repository", "///// notification failed" )
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("Messages repository", " notification fail : ${t.message}" )

            }

        })


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

                }
            }
        }
    }

    fun cancelFriendChangesRegistration(){
        friendChangesRegistration?.remove()
    }



}