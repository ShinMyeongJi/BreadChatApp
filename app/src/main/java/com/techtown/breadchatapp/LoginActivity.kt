package com.techtown.breadchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.rengwuxian.materialedittext.MaterialEditText

class LoginActivity : AppCompatActivity() {

    lateinit var userEmail : MaterialEditText
    lateinit var userPwd : MaterialEditText

    lateinit var loginBtn : Button
    lateinit var forgot_pwd : TextView

    lateinit var auth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("로그인")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userEmail = findViewById(R.id.user_email)
        userPwd = findViewById(R.id.user_pwd)

        loginBtn = findViewById(R.id.login_btn)

        forgot_pwd = findViewById(R.id.forgot_pwd)

        forgot_pwd.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                startActivity(Intent(this@LoginActivity, ResetPwdActivity::class.java))
            }
        })


        auth = FirebaseAuth.getInstance()

        loginBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var email = userEmail.text.toString()
                var pwd = userPwd.text.toString()

                if(TextUtils.isEmpty(email) or TextUtils.isEmpty(pwd)){
                    Toast.makeText(this@LoginActivity, "모든 항목을 입력하세요.", Toast.LENGTH_SHORT).show()
                }else{
                    auth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                            override fun onComplete(task: Task<AuthResult>) {
                                if(task.isSuccessful){
                                    var intent = Intent(this@LoginActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                }
            }
        })
    }
}
