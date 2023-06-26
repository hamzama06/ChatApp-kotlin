package com.maouni92.messengerapp.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import com.maouni92.messengerapp.Preferences
import com.maouni92.messengerapp.R
import com.maouni92.messengerapp.helper.Constants
import com.maouni92.messengerapp.ui.ChatroomActivity
import kotlin.random.Random

class MessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val pref = Preferences(this)
        pref.setToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val pref = Preferences(this)

        if (!pref.isNotificationsEnabled()){
            return
        }

        val title = message.data[Constants.USER_NAME_KEY]
        val messageContent = message.data[Constants.MESSAGE_KEY]

        val userId = message.data[Constants.USER_ID_KEY]
        val userName = message.data[Constants.USER_NAME_KEY]
        val userImageUrl = message.data[Constants.USER_NAME_KEY]


        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelName = "channel_name"
            val channelDescription = "notification_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(Constants.CHANNEL_ID, channelName, importance).apply {
                description = channelDescription
            }

            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, ChatroomActivity::class.java)
        //  intent.putExtra(Constants.USER_ID_KEY,userId)
        intent.putExtra(Constants.FRIEND_ID_KEY,userId)
        intent.putExtra(Constants.FRIEND_NAME_KEY, userName)
        intent.putExtra(Constants.FRIEND_IMAGE_KEY, userImageUrl)


        val pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(this,Constants.CHANNEL_ID ).apply {
            setSmallIcon(R.drawable.app_icon)
            setContentTitle(title)
            setContentText(messageContent)
            priority = (NotificationCompat.PRIORITY_DEFAULT)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }


        notificationManager.notify(0, builder.build())



    }
}