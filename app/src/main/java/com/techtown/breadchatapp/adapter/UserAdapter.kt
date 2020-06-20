package com.techtown.breadchatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.model.User
import android.view.View
import com.bumptech.glide.Glide
import com.techtown.breadchatapp.MessageActivity
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context: Context?, val items: ArrayList<User>, val ischat : Boolean) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        val user = items[position]
        holder.bind(items[position], context!!)

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

        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var intent = Intent(context, MessageActivity::class.java)
                intent.putExtra("userId", items[position].id)
                context?.startActivity(intent)
            }
        })
    }


    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        var userImg = itemView.findViewById<CircleImageView>(R.id.profile_img)
        var userName = itemView.findViewById<TextView>(R.id.user_name)
        var onlineImg = itemView.findViewById<CircleImageView>(R.id.online)
        var offlineImg = itemView.findViewById<CircleImageView>(R.id.offline)

        fun bind(user : User, context : Context){
            userName.text = user.username

            if(user.imageURL.equals("default"))
                userImg.setImageResource(R.drawable.bread_no_img)
            else
                Glide.with(context).load(user.imageURL).into(userImg)

        }
    }
}