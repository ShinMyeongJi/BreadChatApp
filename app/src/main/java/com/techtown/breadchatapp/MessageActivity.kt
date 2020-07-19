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
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.uuid.UUIDGenerator
import com.fasterxml.uuid.UUIDTimer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.techtown.breadchatapp.adapter.MessageAdapter
import com.techtown.breadchatapp.common.CommonTableName
import com.techtown.breadchatapp.fragment.APIService
import com.techtown.breadchatapp.model.Chat
import com.techtown.breadchatapp.model.User
import com.techtown.breadchatapp.notification.*
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    lateinit var seenListener : ValueEventListener

    lateinit var apiService : APIService

    lateinit var curUserId : String
    var notify = false

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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)


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
        curUserId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        sendBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                notify = true
                var msg = msgSendBox.text.toString()
                if(!msg.equals("")) {
                    sendMessage(firebaseUser.uid, curUserId, msgSendBox.text.toString())
                    msgSendBox.setText("")
                }
            }
        })

        reference = FirebaseDatabase.getInstance().getReference("Users").child(curUserId)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapShot: DataSnapshot) {
                var user = snapShot.getValue(User::class.java)
                userName.setText(user?.username)
                if(user?.imageURL.equals("default")){
                    profileImg.setImageResource(R.drawable.bread_no_img)
                }else{
                    Glide.with(applicationContext).load(user?.imageURL).into(profileImg)
                }
                readMessage(firebaseUser.uid, curUserId, user?.imageURL!!)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        seenMessage(curUserId)
    }

    private fun seenMessage(userId : String){
        var receiver = FirebaseDatabase.getInstance().getReference(CommonTableName.USERS).child(userId).child(CommonTableName.CHATS)
        var sender = FirebaseDatabase.getInstance().getReference(CommonTableName.USERS).child(firebaseUser.uid).child(CommonTableName.CHATS)
        seenListener = sender.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                for(snapShot in dataSnapShot.children){
                    var chat = snapShot.getValue(Chat::class.java)

                    if(!chat?.isSend!!) {
                        if (chat?.receiver.equals(userId) && chat?.sender.equals(firebaseUser.uid)) {
                            var hashMap: HashMap<String, Object> = HashMap()
                            hashMap.put("seen", true as Object)
                            snapShot.ref.updateChildren(hashMap as Map<String, Any>)

                            receiver.child(snapShot.key!!).updateChildren(hashMap as Map<String, Any>)
                        }
                    }

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    /**
     * receiver : 발신자 (상대방)
     * sender : 받는 사람 (나)
     */
    private fun sendMessage(sender : String, receiver : String, message : String){
        var senderReference = FirebaseDatabase.getInstance().getReference("Users").child(sender)
        var receiverReference = FirebaseDatabase.getInstance().getReference("Users").child(receiver)

        var hashMap : HashMap<String, Object> = HashMap();
        hashMap.put("sender", sender as Object)
        hashMap.put("receiver", receiver as Object)
        hashMap.put("message", message as Object)
        hashMap.put("seen", false as Object)
        hashMap.put("send", true as Object) //내거

        var uuid = System.currentTimeMillis().toString()
        senderReference.child(CommonTableName.CHATS).child(uuid).setValue(hashMap)

        var recehash : HashMap<String, Object> = HashMap();
        recehash.put("sender", receiver as Object)
        recehash.put("receiver", sender as Object)
        recehash.put("message", message as Object)
        recehash.put("seen", false as Object)
        recehash.put("send", false as Object)

        receiverReference.child(CommonTableName.CHATS).child(uuid).setValue(recehash)

        val chatRef = FirebaseDatabase.getInstance().getReference(CommonTableName.USERS)
            .child(firebaseUser.uid)
            .child("Chatlist")

        chatRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(!dataSnapshot.exists()){
                    chatRef.child("id").setValue(receiver);
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })



        val msg = message

        var ref = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {

                var user = dataSnapShot.getValue(User::class.java)
                if(notify) {
                    sendNotification(receiver, user?.username!!, msg)
                }
                //notify = false
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun sendNotification(receiver : String, username : String, msg : String){
        var tokens = FirebaseDatabase.getInstance().getReference("Tokens")
        var query = tokens.orderByKey().equalTo(receiver)
        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for(snapshot in dataSnapshot.children) {
                    var token = snapshot.getValue(Token::class.java)

                    var data = Data(
                    firebaseUser.uid,
                    R.drawable.bread_no_img,
                    msg,
                    username,
                    receiver
                    )

                    var notification = Notification(
                        msg,
                        username
                    )

                    var sender = Sender(notification, token?.token!!, data) //

                    apiService.sendNotification(sender)
                        .enqueue(object : Callback<MyResponse>{
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if(response.code() == 200){
                                    if(response.body()?.success == 1){

                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }
                    })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    private fun readMessage(myId : String, receiverId : String, imgURL : String){
        mchat = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid).child("Chats") // 키로 받아오기
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
                    messageAdapter.notifyDataSetChanged()
                    recyclerView.adapter = messageAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        reference.removeEventListener(seenListener)
    }

    override fun onBackPressed() {
        startActivity(Intent(this@MessageActivity, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
    }


}
