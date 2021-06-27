package com.dailynotes.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.constraintlayout.widget.Constraints
import com.bumptech.glide.Glide
import com.dailynotes.R
import com.dailynotes.databinding.ActivityUpdateInfoBinding
import com.dailynotes.util.helper.AppController
import com.dailynotes.util.helper.CommonMethods
import com.dailynotes.util.helper.ConstantUtility
import com.dailynotes.util.helper.ImagePickerUtility
import com.dailynotes.util.model.User
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.toolbar_common.*
import java.util.*

class UpdateInfoActivity : ImagePickerUtility() {
    lateinit var binding: ActivityUpdateInfoBinding
    var db: FirebaseFirestore? = null
    var storageReference: StorageReference? = null
    var storage: FirebaseStorage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

       db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()

        tv_title.text=getString(R.string.edit_profile)
        iv_back.setOnClickListener { onBackPressed() }
        iv_back.visibility= View.VISIBLE


        getUserData(AppController.getInstance().getString(ConstantUtility.USER_ID))

        binding.ivEdit.setOnClickListener {
            ImagePickers(this)
        }
    }

    var photoUrl=""
    private fun getUserData(user_id: String) {
        CommonMethods.showProgress(this)
        val docRef: DocumentReference = db!!.collection(ConstantUtility.TABLE_NAME_USER).document(
            user_id
        )
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                CommonMethods.hideProgress()
                val document = task.result
                if (document!!.exists()) {
                    val userInfo: User = document.toObject(User::class.java)!!
                    if (userInfo.profilePic!=null){
                        photoUrl=userInfo.profilePic
                        Glide.with(this).load(photoUrl)
                            .placeholder(R.drawable.ic_placeholder)
                            .error(R.drawable.ic_placeholder)
                            .into(binding.ivProfileUser)
                    }

                    binding.etName.setText(userInfo.name)
                } else {
                    Log.d(
                        Constraints.TAG,
                        "No such document"
                    )
                }
            } else {
                CommonMethods.hideProgress()
                Log.d(
                    Constraints.TAG,
                    "get failed with ",
                    task.exception
                )
            }
        }
    }
    fun onUpdatesClick(view: View){


        if (!imagePath.isEmpty()){
            CommonMethods.showProgress(this)
            val ref: StorageReference =
                storageReference!!.child(AppController.getInstance().getString(ConstantUtility.USER_ID) + System.currentTimeMillis().toString() )
            ref.putFile(uri!!)
                .addOnSuccessListener { taskSnapshot ->

                    taskSnapshot.getMetadata()!!.getReference()!!.getDownloadUrl().addOnSuccessListener({ url ->
                        var photoUrl = url.toString()

                        Log.e("pic", photoUrl!!.toString())
                        updateInfo(binding.etName.text.toString().trim(),photoUrl)

                        //updateDatbase(position, fileUrl, user_id + time.toString() + ".jpg")
                        // Log.e("time_second", time.toString())
                    })
                }
                .addOnFailureListener(OnFailureListener { e ->
                    CommonMethods.hideProgress()

                    Toast.makeText(this@UpdateInfoActivity, e.message, Toast.LENGTH_SHORT).show()
                })
        }else{
            CommonMethods.showProgress(this)
             updateInfo(binding.etName.text.toString().trim(),photoUrl)

        }

    }

    fun updateInfo(name:String,photoUrl:String) {
        val geder: MutableMap<String, Any> = HashMap()
        geder["name"] = name
        geder["profilePic"] = photoUrl


        db!!.collection(ConstantUtility.TABLE_NAME_USER)
            .document(AppController.getInstance().getString(ConstantUtility.USER_ID))
            .update(geder)
            .addOnSuccessListener(OnSuccessListener<Void?> {
                CommonMethods.hideProgress()
                onBackPressed()
                Log.e("Success==", "Success")
               /* Toast.makeText(
                    baseContext, "Update Done",
                    Toast.LENGTH_SHORT
                ).show()*/
            })
            .addOnFailureListener(OnFailureListener { e ->
                CommonMethods.hideProgress()

                Log.e("Failure", e.toString()) })

    }
    override fun onBackPressed() {
        super.onBackPressed()
    }


    var imagePath=""
    lateinit var uri:Uri
    override fun selectedImage(imagePaths: String?, uris: Uri?) {

        imagePath=imagePaths!!
        uri=uris!!
        Glide.with(this).load(imagePath)
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .into(binding.ivProfileUser)
    }


}