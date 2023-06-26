package com.maouni92.messengerapp.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.maouni92.messengerapp.interfaces.FirebaseListener
import com.maouni92.messengerapp.Preferences
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.helper.FirebaseInstances
import com.maouni92.messengerapp.model.User

class AuthenticationRepository(context: Context) {

    private val auth: FirebaseAuth by lazy { Firebase.auth }

    private val usersRef = FirebaseInstances.usersRef

    private val sharedPref = Preferences(context)

    var userLiveData : MutableLiveData<FirebaseUser>? = MutableLiveData<FirebaseUser>()


    init {
        if (auth.currentUser != null){
            userLiveData?.postValue(auth.currentUser!!)
        }}
    private val token:String by lazy {
        sharedPref.getToken()!!
    }

    fun login(email:String, password:String, authenticationListener: FirebaseListener){

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful){
                usersRef.document(it.result.user!!.uid).update("token",token)
                userLiveData?.postValue(it.result.user)
                authenticationListener.onCompleteListener()
            }
        }.addOnFailureListener {
            authenticationListener.onFailureListener()
        }

    }

    fun register(name:String, email:String, password:String, authenticationListener: FirebaseListener ){

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task->
            if(task.isSuccessful){

                val user = User(auth.currentUser!!.uid, name, email, "")

                usersRef.document(auth.currentUser!!.uid).set(user).addOnCompleteListener {
                    if (it.isSuccessful){
                        usersRef.document(user.id!!).update(Constants.FCM_TOKEN_KEY, token)
                        authenticationListener.onCompleteListener()

                    }
                }}
        }.addOnFailureListener {
            authenticationListener.onFailureListener()
        }
    }

    fun signOut(){
        usersRef.document(auth.currentUser!!.uid).update(mapOf(
            Constants.FCM_TOKEN_KEY to FieldValue.delete(),
            Constants.FRIEND_AVAILABILITY_KEY to false
        ))
        auth.signOut()


    }
}