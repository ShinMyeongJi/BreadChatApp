package com.techtown.breadchatapp.notification

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseIdService3 : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        var firebaseUser = FirebaseAuth.getInstance().currentUser
        Log.d("====================", firebaseUser?.uid)
        /*var refreshToken = FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener{
            task ->
             if(firebaseUser != null) updateToken(task.token)
        }*/
     //   Toast.makeText(applicationContext, firebaseUser?.uid, Toast.LENGTH_SHORT).show()
      //  if(firebaseUser != null){
            updateToken(token)
       // }
    }

    private fun updateToken(refreshToken : String){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        Log.d("====================", firebaseUser?.uid)

        var reference = FirebaseDatabase.getInstance().getReference("Tokens")
        var token = Token(refreshToken)

        reference.child(firebaseUser?.uid!!).setValue(token)
    }



}