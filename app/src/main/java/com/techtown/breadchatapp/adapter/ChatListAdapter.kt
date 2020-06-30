package com.techtown.breadchatapp.adapter

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.model.User
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techtown.breadchatapp.MainActivity
import com.techtown.breadchatapp.MessageActivity
import com.techtown.breadchatapp.TestActivity
import com.techtown.breadchatapp.model.Chat
import de.hdodenhof.circleimageview.CircleImageView

class ChatListAdapter(val context: Context?, val items: ArrayList<User>, val ischat : Boolean, val requestManager : RequestManager) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>(){

    lateinit var theLastMsg : String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {


        val user = items[position]
        holder.bind(items[position], context!!, requestManager)

        if(ischat){
            if(user.status.equals("online")){
                holder.onlineImg.visibility = View.VISIBLE
                holder.offlineImg.visibility = View.INVISIBLE
            }else if(user.status.equals("offline")){
                holder.onlineImg.visibility = View.INVISIBLE
                holder.offlineImg.visibility = View.VISIBLE
            }
        }else{
            holder.onlineImg.visibility = View.GONE
            holder.offlineImg.visibility = View.GONE
        }

        if(ischat){
            lastMessage(user.id, holder.last_msg)
        }else{
            holder.last_msg.visibility = View.GONE
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {


                var intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("userId", items[position].id)

                context?.startActivity(intent)

            }
        })
    }


    class ChatViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        var userImg = itemView.findViewById<CircleImageView>(R.id.profile_img)
        var userName = itemView.findViewById<TextView>(R.id.user_name)
        var onlineImg = itemView.findViewById<CircleImageView>(R.id.online)
        var offlineImg = itemView.findViewById<CircleImageView>(R.id.offline)
        var last_msg = itemView.findViewById<TextView>(R.id.last_msg)

        fun bind(user : User, context : Context, requestManager : RequestManager){
            userName.text = user.username

            if(user.imageURL.equals("default"))
                userImg.setImageResource(R.drawable.bread_no_img)
            else
                requestManager.load(user.imageURL).into(userImg)



            //Glide.with(context).load(user.imageURL).into(userImg)

        }
    }

    private fun lastMessage(userId : String, last_msg : TextView){
        theLastMsg = "default"

        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var reference = FirebaseDatabase.getInstance().getReference("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                for(snapShot in dataSnapShot.children){
                    var chat = snapShot.getValue(Chat::class.java)

                    if(
                        chat?.receiver.equals(firebaseUser?.uid) &&
                        chat?.sender.equals(userId) ||
                        chat?.receiver.equals(userId) &&
                        chat?.sender.equals(firebaseUser?.uid)
                    ){
                        theLastMsg = chat?.message!!
                    }
                }

                when(theLastMsg){
                    "default" -> last_msg.setText("No Message")
                    else -> last_msg.setText(theLastMsg)
                }

                theLastMsg = "default"
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }



}