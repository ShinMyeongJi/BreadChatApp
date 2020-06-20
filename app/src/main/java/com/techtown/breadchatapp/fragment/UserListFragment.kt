package com.techtown.breadchatapp.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.adapter.UserAdapter
import com.techtown.breadchatapp.model.User

class UserListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter : UserAdapter
    lateinit var mUsers : ArrayList<User>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_user_list, container, false)

        recyclerView = view.findViewById(R.id.user_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        mUsers = ArrayList()

        readUsers()

        return view
    }

    private fun readUsers(){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var reference = FirebaseDatabase.getInstance().getReference("Users")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {

                for(snapShot in dataSnapShot.children){
                    var user = snapShot.getValue(User::class.java)

                    assert(user != null)
                    assert(firebaseUser != null)
                    if(!(user?.id.equals(firebaseUser?.uid))){
                        mUsers.add(user!!)
                    }
                }

                userAdapter = UserAdapter(context!!, mUsers, false)

                recyclerView.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


}
