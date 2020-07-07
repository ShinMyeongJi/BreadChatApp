package com.techtown.breadchatapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.*

import com.techtown.breadchatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {

    lateinit var profile_img : CircleImageView
    lateinit var username : TextView

    lateinit var referenece : DatabaseReference
    lateinit var firebaseUser : FirebaseUser

    lateinit var storageReference : StorageReference
    lateinit var imageURL : Uri
    var uploadTask : StorageTask<UploadTask.TaskSnapshot>? = null
    companion object{
        private val IMAGE_REQUEST = 1
    }

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

        storageReference = FirebaseStorage.getInstance().getReference("uploads")

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

        profile_img.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                openImage()
            }
        })

    }

    private fun openImage(){
        var intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, IMAGE_REQUEST)
    }

    private fun getFileExtension(uri : Uri): String? {
        var contentResolver = baseContext.contentResolver
        var mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage(){
        /*val builder = AlertDialog.Builder(this@ProfileActivity)
        builder.setView(R.layout.progress)
        var dialog = builder.create()*/

        val pd = ProgressDialog(this@ProfileActivity)
        pd.setMessage("Upload")
        pd.show()

        if(imageURL != null){
            val fileReference = storageReference.child(System.currentTimeMillis().toString() + "." + getFileExtension(imageURL))

            uploadTask = fileReference.putFile(imageURL).addOnSuccessListener(object :
                OnSuccessListener<UploadTask.TaskSnapshot> {
                override fun onSuccess(task: UploadTask.TaskSnapshot?) {
                    if(task?.task?.isSuccessful!!){
                        fileReference.downloadUrl.addOnSuccessListener(object :
                            OnSuccessListener<Uri>{
                            override fun onSuccess(downloadUrl: Uri?) {
                                referenece = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
                                var map : HashMap<String, String> = HashMap()
                                map.put("imageURL", downloadUrl.toString())
                                referenece.updateChildren(map as Map<String, Any>)

                                pd.dismiss()
                            }
                        }).addOnFailureListener(object : OnFailureListener{
                            override fun onFailure(p0: Exception) {
                                Toast.makeText(this@ProfileActivity, p0.toString(), Toast.LENGTH_SHORT).show()
                            }
                        })
                    }else{
                        Toast.makeText(this@ProfileActivity, "Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }else{
            Toast.makeText(this@ProfileActivity, "이미지가 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null){
            imageURL = data.data!!

            if(uploadTask != null && uploadTask?.isInProgress!!){
                Toast.makeText(this@ProfileActivity, "업로드 중입니다.", Toast.LENGTH_SHORT).show()
            }else{
                uploadImage()
            }
        }
    }
}
