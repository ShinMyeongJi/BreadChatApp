package com.techtown.breadchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlin.math.log

class StartActivity : AppCompatActivity() {

    lateinit var login : Button
    lateinit var register : Button


    var auth : FirebaseAuth = FirebaseAuth.getInstance()
    var firebaseUser : FirebaseUser? = auth.currentUser
        override fun onStart() {
        super.onStart()



        if(firebaseUser != null){
            var intent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else{

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        login = findViewById(R.id.btn_login)
        register = findViewById(R.id.btn_register)

        login.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@StartActivity, LoginActivity::class.java))
            }
        })

        register.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@StartActivity, RegisterActivity::class.java))
            }
        })
    }
}
