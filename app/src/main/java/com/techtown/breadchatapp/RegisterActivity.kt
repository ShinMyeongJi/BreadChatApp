package com.techtown.breadchatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rengwuxian.materialedittext.MaterialEditText

class RegisterActivity : AppCompatActivity() {

    lateinit var userName : MaterialEditText
    lateinit var userEmail : MaterialEditText
    lateinit var userPwd : MaterialEditText
    lateinit var btnRegister : Button

    lateinit var auth : FirebaseAuth
    lateinit var reference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        var toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("회원가입")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        userName = findViewById(R.id.user_name)
        userEmail = findViewById(R.id.user_email)
        userPwd = findViewById(R.id.user_pwd)

        btnRegister = findViewById(R.id.register_btn)

        auth = FirebaseAuth.getInstance()

        btnRegister.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var nameTxt = userName.text.toString()
                var emailText = userEmail.text.toString()
                var pwdText = userPwd.text.toString()



                if(TextUtils.isEmpty(nameTxt) or TextUtils.isEmpty(emailText) or TextUtils.isEmpty(pwdText)) {
                    Toast.makeText(this@RegisterActivity, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show()
                }else if(pwdText.length < 6){
                    Toast.makeText(this@RegisterActivity, "모든 항목을 입력하세요", Toast.LENGTH_SHORT).show()
                }else{
                    register(
                        nameTxt,
                        emailText,
                        pwdText
                    )
                }
            }
        })
        
    }

    private fun register(userName : String, userEmail : String, userPwd : String){
        auth.createUserWithEmailAndPassword(userEmail, userPwd)
            .addOnCompleteListener(object : OnCompleteListener<AuthResult>{
                override fun onComplete(task: Task<AuthResult>) {
                    if(task.isSuccessful){
                        var firebaseUser = auth.currentUser
                        assert(firebaseUser != null)
                        var userId = firebaseUser?.uid

                        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

                        var hashMap = HashMap<String, String>()
                        hashMap.put("id", userId)
                        hashMap.put("username", userName)
                        hashMap.put("imageURL", "default")

                        reference.setValue(hashMap).addOnCompleteListener(object :
                            OnCompleteListener<Void>{
                            override fun onComplete(task: Task<Void>) {
                                if(task.isSuccessful){
                                    var intent = Intent(this@RegisterActivity, StartActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                        })
                    }else {
                        Toast.makeText(this@RegisterActivity, "회원가입에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}
