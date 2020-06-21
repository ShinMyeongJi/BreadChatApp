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
import com.techtown.breadchatapp.adapter.UserAdapter
import com.techtown.breadchatapp.model.Chat
import com.techtown.breadchatapp.model.User


class ChatListFragment : Fragment() {

    lateinit var recyclerView : RecyclerView

    lateinit var userAdapter : UserAdapter
    lateinit var mUsers : ArrayList<User>

    lateinit var firebaseUser : FirebaseUser
    lateinit var reference : DatabaseReference

    lateinit var usersList : ArrayList<String>

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
        reference = FirebaseDatabase.getInstance().getReference("Chats")

        mGlideRequestManager = Glide.with(this)

        usersList = ArrayList()

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                usersList.clear()

                for(snapShot in dataSnapShot.children){
                    var chat = snapShot.getValue(Chat::class.java)

                    if(chat?.sender.equals(firebaseUser.uid)){
                        usersList.add(chat?.receiver!!)
                    }

                    if(chat?.receiver.equals(firebaseUser.uid)){
                        usersList.add(chat?.sender!!)
                    }
                }

                var hashSet = HashSet<String>(usersList) as Set<String>
                usersList.clear()
                usersList.addAll(hashSet)


                readChat()
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
        return view
    }


    private fun readChat(){
        mUsers = ArrayList()

        reference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
               // mUsers.clear()

                for(snapShot in dataSnapShot.children){
                    var user = snapShot.getValue(User::class.java) as User

                    for(mId in usersList) {
                        assert(user != null)
                        if (user.id.equals(mId)) {
                            mUsers.add(user)
                        }
                    }
                }
                userAdapter = UserAdapter(context, mUsers, true, mGlideRequestManager)
                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
