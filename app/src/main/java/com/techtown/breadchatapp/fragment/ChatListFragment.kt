package com.techtown.breadchatapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.adapter.ChatListAdapter
import com.techtown.breadchatapp.common.CommonTableName
import com.techtown.breadchatapp.model.Chatlist
import com.techtown.breadchatapp.model.User
import com.techtown.breadchatapp.notification.Token


class ChatListFragment : Fragment() {

    lateinit var recyclerView : RecyclerView

    lateinit var userAdapter : ChatListAdapter
    lateinit var mUsers : ArrayList<User>

    lateinit var firebaseUser : FirebaseUser
    lateinit var reference : DatabaseReference

    lateinit var usersList : ArrayList<Chatlist>

    lateinit var mGlideRequestManager : RequestManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_chat_list, container, false)

        recyclerView = view.findViewById(R.id.chat_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser.uid)

        mGlideRequestManager = Glide.with(this)

        usersList = ArrayList()

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                usersList.clear()
                for(snapShot in dataSnapShot.children){
                    var chatlist = snapShot.getValue(Chatlist::class.java)
                    usersList.add(chatlist!!)
                }

                chatList()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        //updateToken(FirebaseInstanceId.getInstance().token!!)

        return view
    }

    private fun updateToken(token : String){
        var tokens = FirebaseDatabase.getInstance().getReference("Tokens")

        var token = Token(tokens.toString())
        tokens.child(firebaseUser.uid).setValue(token)
    }



    private fun chatList(){
        mUsers = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference(CommonTableName.USERS).child(firebaseUser?.uid!!).child(
            CommonTableName.FRIENDS)


        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                mUsers.clear()
                for(snapShot in dataSnapShot.children){
                    var user = snapShot.getValue(User::class.java)
                    for(chatlist in usersList){
                        if(user?.id.equals(chatlist.id)){
                            mUsers.add(user!!)
                        }
                    }
                }
                userAdapter = ChatListAdapter(context, mUsers, true, mGlideRequestManager)
                userAdapter.notifyDataSetChanged()
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

}
