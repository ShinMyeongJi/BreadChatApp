package com.techtown.breadchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.techtown.breadchatapp.adapter.MessageAdapter
import com.techtown.breadchatapp.model.Chat
import com.techtown.breadchatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MessageActivity : AppCompatActivity() {

    lateinit var profileImg : CircleImageView
    lateinit var userName : TextView

    lateinit var firebaseUser : FirebaseUser
    lateinit var reference : DatabaseReference

    lateinit var sendBtn : ImageButton
    lateinit var msgSendBox : EditText

    lateinit var messageAdapter : MessageAdapter
    lateinit var mchat : ArrayList<Chat>

    lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@MessageActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            }
        })

        profileImg = findViewById(R.id.profile_img)
        userName = findViewById(R.id.user_name)
        sendBtn = findViewById(R.id.btn_send)
        msgSendBox = findViewById(R.id.msg_send_box)

        recyclerView = findViewById(R.id.chat_recycler_view)
        recyclerView.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(this@MessageActivity)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        var intent = intent
        var receiverId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        sendBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var msg = msgSendBox.text.toString()
                if(!msg.equals("")) {
                    sendMessage(firebaseUser.uid, receiverId, msgSendBox.text.toString())
                    msgSendBox.setText("")
                }
            }
        })


        reference = FirebaseDatabase.getInstance().getReference("Users").child(receiverId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapShot: DataSnapshot) {
                var user = snapShot.getValue(User::class.java)
                userName.setText(user?.username)
                if(user?.imageURL.equals("default")){
                    profileImg.setImageResource(R.drawable.bread_no_img)
                }else{
                    Glide.with(this@MessageActivity).load(user?.imageURL).into(profileImg)
                }
                readMessage(firebaseUser.uid, receiverId, user?.imageURL!!)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun sendMessage(sender : String, receiver : String, message : String){
        var reference = FirebaseDatabase.getInstance().getReference()

        var hashMap : HashMap<String, Object> = HashMap();
        hashMap.put("sender", sender as Object)
        hashMap.put("receiver", receiver as Object)
        hashMap.put("message", message as Object)

        reference.child("Chats").push().setValue(hashMap)
    }

    private fun readMessage(myId : String, receiverId : String, imgURL : String){
        mchat = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Chats") // 키로 받아오기
        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                mchat.clear()
                for(snapShot in dataSnapShot.children){
                    var chat = snapShot.getValue(Chat::class.java)!!
                    if(
                        chat.receiver.equals(receiverId) && chat.sender.equals(myId) || //or와 ||가 뭐가 다른지;;;?
                        chat.receiver.equals(myId) && chat.sender.equals(receiverId)
                    ){
                        mchat.add(chat)
                    }
                    messageAdapter = MessageAdapter(this@MessageActivity, mchat, imgURL)
                    recyclerView.adapter = messageAdapter

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun status(status : String){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        var hashMap : HashMap<String, Object> = HashMap()

        hashMap.put("status", status as Object)
        reference.updateChildren(hashMap as Map<String, Any>)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}
