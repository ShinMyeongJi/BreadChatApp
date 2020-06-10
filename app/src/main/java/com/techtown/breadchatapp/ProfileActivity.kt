package com.techtown.breadchatapp

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
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.*

import com.techtown.breadchatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    lateinit var profile_img : CircleImageView
    lateinit var username : TextView

    lateinit var referenece : DatabaseReference
    lateinit var firebaseUser : FirebaseUser

    lateinit var storageReference : StorageReference
    lateinit var imageURL : Uri
    lateinit var uploadTask : StorageTask<UploadTask.TaskSnapshot>
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
            uploadTask = fileReference.getFile(imageURL) as UploadTask
            uploadTask.continueWith(object : Continuation<UploadTask.TaskSnapshot, Task<Uri>>{
                override fun then(task: Task<UploadTask.TaskSnapshot>): Task<Uri> {
                    if(!task.isSuccessful){
                        throw task.exception!!
                    }
                    return fileReference.downloadUrl
                }
            }).addOnCompleteListener(object : OnCompleteListener<Task<Uri>>{
                override fun onComplete(task: Task<Task<Uri>>) {
                    if(task.isSuccessful){
                        var downloadUri = task.result
                        var mUri = downloadUri.toString()

                        referenece = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
                        var map : HashMap<String, String> = HashMap()
                        map.put("imageURl", mUri)
                        referenece.updateChildren(map as Map<String, Any>)

                        pd.dismiss()
                    }else{

                    }
                }
            })

        }
    }
}
