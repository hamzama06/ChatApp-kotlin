package com.maouni92.messengerapp.helper

import com.maouni92.messengerapp.BuildConfig

object Constants {

    const val USERS_COLLECTION_PATH = "users"
    const val USER_NAME_KEY = "name"
    const val USER_ID_KEY = "id"
    const val USER_IMAGE_KEY = "user_image_url"
    const val FCM_TOKEN_KEY = "token"
    const val FRIEND_ID_KEY = "friend_id"
    const val FRIEND_IMAGE_KEY = "friend_image_url"
    const val FRIEND_NAME_KEY = "friend_name"
    const val MESSAGE_KEY = "message"
    const val CHATROOM_COLLECTION_PATH = "chatroom"
    const val IMAGES_STORAGE_PATH = "images"
    const val PREFERENCE_NAME = "pref"
    const val FCM_URL = "https://fcm.googleapis.com/fcm/"
    const val CHANNEL_ID = "chat_message"
    const val FRIEND_AVAILABILITY_KEY = "isAvailable"
    const val MSG_AUTHORIZATION = "authorization"
    const val REMOTE_MESSAGE_CONTENT_TYPE = "content-type"

    const val REMOTE_MESSAGE_DATA = "data"
    const val REMOTE_MESSAGE_REGISTRATION_IDS = "registration_ids"
    const val SERVER_KEY = "AAAAHCf7Gck:APA91bGi-QR3ZdPbvoI0FQ4o-2DFVmxjiwB3J3rA14XG_B1icLyA-YN7q_OZJtx9snk_D6wOyg5cpCkGS1LikGe42QNvC4bFdSdViQVKut-wjBpCHi-9qMHtS9vSPgB_PKiHWWnu6r1r"
    private var remoteMessageHeaders: HashMap<String, String>? = null

    val getRemoteMessageHeader : HashMap<String, String>? get(){
          if (remoteMessageHeaders == null){
              remoteMessageHeaders = HashMap()
              remoteMessageHeaders!![MSG_AUTHORIZATION] = "Key=${BuildConfig.API_KEY}"
              remoteMessageHeaders!![REMOTE_MESSAGE_CONTENT_TYPE] = "application/json"
          }
    return remoteMessageHeaders
    }

    const val NOTIFICATIONS_PREFERENCES_KEY = "notifications"
    const val MILLISECONDS_IN_DAY = 86400000   // 24 (hours in day) * 3600 (seconds in hour) *  1000 (milliseconds in second)
    const val MILLISECONDS_IN_WEEK = 604800000 // 86400000 * 7 (days in week)
    const val MILLISECONDS_IN_YEAR = 31536000000 // 86400000 * 365 (days in year)



}