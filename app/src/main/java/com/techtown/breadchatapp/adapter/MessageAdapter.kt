package com.techtown.breadchatapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.model.Chat
import com.techtown.breadchatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text

class MessageAdapter(
    var context: Context,
    var msgs : ArrayList<Chat>,
    var imgURL : String
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>(){

    companion object{
        val MSG_TITLE_RECEIVER = 0
        val MSG_TITLE_SENDER = 1
    }

    lateinit var firebaseUser: FirebaseUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {

        if(viewType == MSG_TITLE_SENDER){
            var view = LayoutInflater.from(context).inflate(R.layout.chat_item_sender, parent, false)
            return MessageViewHolder(view)
        }else{
            var view = LayoutInflater.from(context).inflate(R.layout.chat_item_receiver, parent, false)
            return MessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return msgs.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        var chat = msgs.get(position)

        holder.bind(msgs.get(position), context, imgURL)

        if(position == msgs.size-1){
            Toast.makeText(context, chat.isSeen.toString(), Toast.LENGTH_SHORT).show()
            if(chat.isSeen!!){
                holder.isSeen.setText("읽음")
            }else{
                holder.isSeen.setText("안 읽음")
            }
        }else{
            holder.isSeen.visibility = View.GONE
        }
    }

    class MessageViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var message = itemView.findViewById<TextView>(R.id.show_msg)
        var profile_img = itemView.findViewById<CircleImageView>(R.id.profile_img)
        var isSeen = itemView.findViewById<TextView>(R.id.msgSeen)


        fun bind(chat : Chat, context : Context, imgURL : String) {

            message.setText(chat.message)
            if(imgURL.equals("default")){
                profile_img.setImageResource(R.drawable.bread_no_img)
            }else{
                Glide.with(context).load(imgURL).into(profile_img)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        if(msgs.get(position).sender.equals(firebaseUser.uid)){
            return MSG_TITLE_SENDER
        }else{
            return MSG_TITLE_RECEIVER
        }


    }
}