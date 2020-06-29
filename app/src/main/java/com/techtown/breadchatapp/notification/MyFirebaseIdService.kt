package com.techtown.breadchatapp.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseIdService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)

        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var refreshToken = FirebaseInstanceId.getInstance().getToken()
        if(firebaseUser != null){
            updateToken(refreshToken!!)
        }
    }

    private fun updateToken(refreshToken : String){
        var firebaseUser = FirebaseAuth.getInstance().currentUser

        var reference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token = Token(refreshToken)

        reference.child(firebaseUser?.uid!!).setValue(token)
    }

}