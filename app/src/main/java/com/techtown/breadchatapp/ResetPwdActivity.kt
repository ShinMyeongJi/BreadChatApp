package com.techtown.breadchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

class ResetPwdActivity : AppCompatActivity() {

    lateinit var toolbar : Toolbar

    lateinit var send_email : EditText
    lateinit var btn_reset : Button

    lateinit var firebaseAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pwd)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("비밀번호 찾기")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        send_email = findViewById(R.id.send_email)
        btn_reset = findViewById(R.id.btn_reset)

        firebaseAuth = FirebaseAuth.getInstance()

        btn_reset.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var email = send_email.text.toString()
                if(email.equals("")){
                    Toast.makeText(this@ResetPwdActivity, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show()
                }else{
                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(object :
                        OnCompleteListener<Void>{
                        override fun onComplete(task: Task<Void>) {
                            if(task.isSuccessful){
                                Toast.makeText(this@ResetPwdActivity, "이메일 주소를 체크하세요.", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@ResetPwdActivity, LoginActivity::class.java))
                            }else{
                                var error = task.exception?.message
                                Toast.makeText(this@ResetPwdActivity, error, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
        })
    }
}
