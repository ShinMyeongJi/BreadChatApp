package com.techtown.breadchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.techtown.breadchatapp.fragment.ChatListFragment
import com.techtown.breadchatapp.fragment.UserListFragment
import com.techtown.breadchatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MainActivity : AppCompatActivity() {

    lateinit var profileImg : CircleImageView
    lateinit var userName : TextView

    lateinit var firebaseUser : FirebaseUser
    lateinit var reference : DatabaseReference

    lateinit var userListFragment : UserListFragment
    lateinit var chatListFragment : ChatListFragment

    lateinit var mGlideRequestManager : RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        userListFragment = UserListFragment()
        chatListFragment = ChatListFragment()

        mGlideRequestManager = Glide.with(this@MainActivity)
        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")

        supportFragmentManager.beginTransaction().replace(R.id.container, userListFragment).commitAllowingStateLoss()

        var bottomNavigation = findViewById<BottomNavigationView>(R.id.main_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(object :
            BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                when(item.itemId){
                    R.id.users_list -> {
                        supportFragmentManager.beginTransaction().replace(R.id.container, userListFragment).commitAllowingStateLoss()
                        return true
                    }
                    R.id.chat_list -> {
                        supportFragmentManager.beginTransaction().replace(R.id.container, chatListFragment).commitAllowingStateLoss()
                        return true
                    }
                }
                return false
            }
        })



        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        profileImg = findViewById(R.id.profile_img)
        userName = findViewById(R.id.user_name)


        profileImg.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            }
        })

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                var user = dataSnapShot.getValue(User::class.java)
                userName.setText(user?.username)
                if(user?.imageURL?.equals("default")!!){
                    profileImg.setImageResource(R.drawable.bread_no_img)
                }else{
                    mGlideRequestManager.load(user?.imageURL).into(profileImg)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, StartActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
               // finish()
                return true
            }
        }
        return false
    }

    private fun status(status : String){
        var hashMap : HashMap<String, Object> = HashMap()

        hashMap.put("status", status as Object)
        reference.updateChildren(hashMap as Map<String, Any>)
    }

    override fun onResume() {
        super.onResume()
        //status("online")
    }

    override fun onPause() {
        super.onPause()
        //status("offline")
    }
}
