package com.techtown.breadchatapp.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.techtown.breadchatapp.MainActivity

import com.techtown.breadchatapp.R
import com.techtown.breadchatapp.adapter.UserAdapter
import com.techtown.breadchatapp.common.CommonTableName
import com.techtown.breadchatapp.model.User

class UserListFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var userAdapter : UserAdapter
    lateinit var mUsers : ArrayList<User>

    lateinit var mGlideRequestManager : RequestManager

    lateinit var searchBar : EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view = inflater.inflate(R.layout.fragment_user_list, container, false)

        recyclerView = view.findViewById(R.id.user_recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)

        mGlideRequestManager = Glide.with(this)

        searchBar = view.findViewById(R.id.search_bar)

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!s.toString().isEmpty()){
                    searchUsers(s.toString())
                }else{
                    if(!s.toString().equals("")){

                    }else if(s.toString().equals("")){
                       Toast.makeText(context, "호출", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })


        mUsers = ArrayList()



        return view
    }

    private fun readUsers(){
        var firebaseUser = FirebaseAuth.getInstance().currentUser
        var reference = FirebaseDatabase.getInstance().getReference(CommonTableName.USERS).child(firebaseUser?.uid!!).child(CommonTableName.FRIENDS)

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {

                for(snapShot in dataSnapShot.children){
                    var user = snapShot.getValue(User::class.java)

                    assert(user != null)
                    assert(firebaseUser != null)
                    mUsers.add(user!!)
                }


                userAdapter = UserAdapter(activity, mUsers, false, mGlideRequestManager)
                userAdapter.notifyDataSetChanged()
                recyclerView.adapter = userAdapter

            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        readUsers()
    }


    private fun searchUsers(str : String){
        val fuser = FirebaseAuth.getInstance().currentUser
        var query = FirebaseDatabase.getInstance().getReference(CommonTableName.USERS)
            .child(fuser?.uid!!)
            .child(CommonTableName.FRIENDS)
            .orderByChild("username")
            .startAt(str)
            .endAt(str+"\uf0ff")


        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if(searchBar.text.toString().isNotEmpty()) {

                    mUsers.clear()
                    for (snapShot in dataSnapShot.children) {
                        var user = snapShot.getValue(User::class.java)

                        assert(user != null)
                        assert(fuser != null)
                        if (!user?.id.equals(fuser?.uid)) {
                            mUsers.add(user!!)
                        }
                    }

                    userAdapter = UserAdapter(context, mUsers, false, mGlideRequestManager)
                    recyclerView.adapter = userAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
