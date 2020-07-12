package com.techtown.breadchatapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.techtown.breadchatapp.MessageActivity
import com.techtown.breadchatapp.R
import java.text.SimpleDateFormat
import java.util.*

class MyFirebaseIdService : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
    }

    private fun updateToken(refreshToken : String){
        var firebaseUser = FirebaseAuth.getInstance().currentUser

        var reference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token = Token(refreshToken)

        reference.child(firebaseUser?.uid!!).setValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        sendNotification(remoteMessage)
    }


    fun sendNotification(remoteMessage: RemoteMessage) {

        var icon = remoteMessage.data.getValue("icon")
        var body = remoteMessage.data.getValue("body")
        var title = remoteMessage.data.getValue("title")
        var user = remoteMessage.data.getValue("user")

        var intent = Intent(this, MessageActivity::class.java)


        var bundle = Bundle()
        bundle.putString("userId", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

       // var id = SimpleDateFormat("ddHHmmss", Locale.KOREA).format(Date()).toInt()
        var pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


        var channelId = "chatChannel"

        var defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        var notificationBuilder = NotificationCompat.Builder(this, channelId)
        notificationBuilder
            .setSmallIcon(icon?.toInt()!!)
            .setLargeIcon((resources.getDrawable(R.drawable.large_icon) as BitmapDrawable).bitmap)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent);


        var notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            var channelName = "channelName"

            var channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(channel);

        }


        notificationManager.notify(1, notificationBuilder.build());

    }

}
