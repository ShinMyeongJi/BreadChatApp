package com.techtown.breadchatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.techtown.breadchatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    lateinit var profile_img : CircleImageView
    lateinit var username : TextView

    lateinit var referenece : DatabaseReference
    lateinit var firebaseUser : FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                finish()
            }
        })

        profile_img = findViewById(R.id.profile_img)
        username = findViewById(R.id.user_name)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        referenece = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        referenece.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapShot: DataSnapshot) {
                var user = dataSnapShot.getValue(User::class.java)

                username.setText(user?.username)

                if(user?.imageURL.equals("default")){
                    profile_img.setImageResource(R.drawable.bread_no_img)
                }else{
                    Glide.with(this@ProfileActivity).load(user?.imageURL).into(profile_img)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }
}
