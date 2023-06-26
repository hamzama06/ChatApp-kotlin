package com.maouni92.messengerapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.FirebaseInstances
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.model.User
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class UsersRepository(val context:Context) {


    private var usersList = ArrayList<User>()
    var users = MutableLiveData<ArrayList<User>>()
    var currentUserInfo = MutableLiveData<User>()

    private var _filteredList = ArrayList<User>()
    var filteredList = MutableLiveData<ArrayList<User>>()
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val currentUser:FirebaseUser? get() =  auth.currentUser

    fun getUsers(listener: FirebaseListener?) {
        FirebaseInstances.usersRef.get().addOnSuccessListener {
            for (userSnapshot in it.documents) {
                val user = userSnapshot.toObject(User::class.java)

                if (user?.id != currentUser?.uid) {
                    usersList.add(user!!)
                }
            }
            users.postValue(usersList)

        }.addOnCompleteListener {
            listener?.onCompleteListener()
        }.addOnFailureListener {
            listener?.onFailureListener()
        }}

    fun getCurrentUser() {
        if (currentUser != null){
            FirebaseInstances.usersRef.document(currentUser!!.uid).get().addOnSuccessListener {
                val user: User = it.toObject(User::class.java)!!
                currentUserInfo.postValue(user)
            }
        }

    }

    fun changeUserName(name: String) {
        FirebaseInstances.usersRef.document(currentUser!!.uid).update(Constants.USER_NAME_KEY, name)
    }

    fun changePassword(currentPassword: String, newPassword: String, listener: FirebaseListener) {


        val credential = EmailAuthProvider
            .getCredential(currentUser!!.email!!, currentPassword)

        currentUser!!.reauthenticate(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    currentUser?.updatePassword(newPassword)!!
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                listener.onCompleteListener()
                            }
                        }
                }
            }.addOnFailureListener {
                listener.onFailureListener()
            }

    }

    fun updateUserAvailability(isAvailable:Boolean){
        if (currentUser != null){
            FirebaseInstances.usersRef.document(currentUser!!.uid).update(Constants.FRIEND_AVAILABILITY_KEY, isAvailable)
        }

    }

    fun searchUsers(query: String) {

        _filteredList.clear()

        if (query.isEmpty()) {
            _filteredList.addAll(usersList)
        } else {

            usersList.forEach {
                if (it.name!!.lowercase().contains(query.lowercase())) {
                    _filteredList.add(it)
                }  } }

        filteredList.postValue(_filteredList)
    }

    fun uploadProfileImage(uri: Uri) {
        val imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val outputStream = ByteArrayOutputStream()
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, outputStream)
        val imageBytes = outputStream.toByteArray()
        val ref = FirebaseInstances.storageRef.child(
            "${currentUser!!.uid}/profile/${
                UUID.nameUUIDFromBytes(imageBytes)
            }"
        )
        ref.putBytes(imageBytes).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ref.downloadUrl.addOnSuccessListener {
                    FirebaseInstances.usersRef.document(currentUser!!.uid)
                        .update("imageUrl", it.toString())

                }}
        }
    }

}