package com.techtown.breadchatapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.techtown.breadchatapp.MessageActivity

class MyFirebaseMessaging : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var sented = remoteMessage.data.get("sented")

        var firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null && sented.equals(firebaseUser.uid)){
            sendNotification(remoteMessage)
        }
    }

    fun sendNotification(remoteMessage: RemoteMessage){
        var user = remoteMessage.data.get("user")
        var icon = remoteMessage.data.get("icon")
        var title = remoteMessage.data.get("title")
        var body = remoteMessage.data.get("body")

        var channelId = "chatNoti"

        var notification = remoteMessage.notification as RemoteMessage.Notification

        var j = user?.replace("[\\D]", "")?.toInt()!!

        var intent = Intent(this, MessageActivity::class.java)

        var bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)

        var pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT)

        var defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        var builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(icon?.toInt()!!)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSound)
            .setContentIntent(pendingIntent)

        var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        var i = 0
        if(j > 0){
            i = j
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            var channelName = "chatNofiChannel"
            var channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(i, builder.build())
    }
}