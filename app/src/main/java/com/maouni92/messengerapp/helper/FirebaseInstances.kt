package com.maouni92.messengerapp.helper

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

object FirebaseInstances {

    private val database : FirebaseFirestore get() = Firebase.firestore
    val usersRef : CollectionReference get() = database.collection(Constants.USERS_COLLECTION_PATH)
    private val storage: FirebaseStorage get() =  Firebase.storage
    val storageRef: StorageReference get() =  storage.reference.child(Constants.IMAGES_STORAGE_PATH)
    val messagesRef : CollectionReference get() = database.collection(Constants.CHATROOM_COLLECTION_PATH)
}