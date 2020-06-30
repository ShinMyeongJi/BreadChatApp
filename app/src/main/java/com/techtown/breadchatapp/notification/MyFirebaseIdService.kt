package com.techtown.breadchatapp.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseIdService : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        updateToken(token)
        Log.d("===", token)
    }

    private fun updateToken(refreshToken : String){
        var firebaseUser = FirebaseAuth.getInstance().currentUser

        var reference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token = Token(refreshToken)

        reference.child(firebaseUser?.uid!!).setValue(token)
    }

}
