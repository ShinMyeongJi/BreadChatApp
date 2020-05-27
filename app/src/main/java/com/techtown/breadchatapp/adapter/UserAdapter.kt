package com.techtown.breadchatapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.model.User
import android.view.View
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(val context : Context, val items : ArrayList<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false)
        return UserViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
          holder.bind(items[position], context)
    }


    class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var userImg = itemView.findViewById<CircleImageView>(R.id.profile_img)
        var userName = itemView.findViewById<TextView>(R.id.user_name)

        @SuppressLint("ResourceType")
        fun bind(user : User, context : Context){
            userName.text = user.username

            if(user.imageURL.equals("default"))
                userImg.setImageResource(R.drawable.bread_no_img)
            else
                Glide.with(context).load(user.imageURL).into(userImg)

        }
    }
}