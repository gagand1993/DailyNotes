package com.dailynotes.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.dailynotes.R
import com.dailynotes.adapter.NotesAdapter
import com.dailynotes.databinding.ActivityHomeBinding
import com.dailynotes.util.helper.AppController
import com.dailynotes.util.helper.CommonMethods
import com.dailynotes.util.helper.ConstantUtility
import com.dailynotes.util.helper.ConstantUtility.Companion.ADD
import com.dailynotes.util.helper.ConstantUtility.Companion.STATUS
import com.dailynotes.util.helper.ConstantUtility.Companion.TABLE_NAME_NOTES
import com.dailynotes.util.helper.ConstantUtility.Companion.TABLE_NAME_USER
import com.dailynotes.util.helper.ConstantUtility.Companion.USER_ID
import com.dailynotes.util.model.Notes
import com.dailynotes.util.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.toolbar_common.*


class DashboardActivity : AppCompatActivity() {

    lateinit var binding: ActivityHomeBinding
    var db: FirebaseFirestore? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        db = FirebaseFirestore.getInstance()


        tv_title.text=getString(R.string.app_name)
        iv_logout.setOnClickListener { logout() }

        iv_logout.visibility=View.VISIBLE
        iv_profile.visibility=View.VISIBLE
        binding.ivAdd.visibility=View.VISIBLE


    }

    fun onClickProfile(view: View?){

        var intent=Intent(this,UpdateInfoActivity::class.java)
        startActivity(intent)
    }

    fun logout(){


        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to logout?")
        builder.setCancelable(true)
        builder.setPositiveButton("Yes") { dialog, which ->
            dialog.cancel()
            AppController.getInstance().clearData()
            FirebaseAuth.getInstance().signOut();
            val intent = Intent(this@DashboardActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }


        builder.setNegativeButton("No") { dialog, which ->
            dialog.cancel()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()


    }
    fun onAddNote(view: View){
        var intent= Intent(this, AddNoteActivity::class.java)
        intent.putExtra(STATUS, ADD)
        startActivity(intent)
    }

    fun onInfoUpdateClick(view: View){
        var intent= Intent(this, UpdateInfoActivity::class.java)
        startActivity(intent)
    }
    override fun onResume() {
        super.onResume()
        loadNotes(AppController.getInstance().getString(ConstantUtility.USER_ID))
         getUserData(AppController.getInstance().getString(USER_ID))
    }

    private fun loadNotes(userId: String) {


        db!!.collection(ConstantUtility.TABLE_NAME_USER)
                .document(userId).collection(TABLE_NAME_NOTES)
                .addSnapshotListener { value, error ->

                    try {
                        value?.let {
                            val tempNoteList = ArrayList<Notes>()
                            for(doc in value.iterator()) {
                                val note = doc.toObject(Notes::class.java)
                                tempNoteList.add(note)
                            }
                            Log.e("sizeCheck", tempNoteList.size.toString())

                            var adpter=NotesAdapter(this, tempNoteList)


                           /* val gridLayoutManager = GridLayoutManager(this, 2)
                            gridLayoutManager.spanSizeLookup = object : SpanSizeLookup() {
                                override fun getSpanSize(position: Int): Int {
                                    //define span size for this position
                                    //for example, if you have 2 column per row, you can implement something like that:
                                    return if (position % 3 == 0)
                                        2
                                    else
                                        1
                                }
                            }*/
                            val gridLayoutManager = GridLayoutManager(this, 2)

                            binding.rvNotes.layoutManager=gridLayoutManager
                            binding.rvNotes.adapter=adpter

                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                                baseContext, "Try again",
                                Toast.LENGTH_SHORT
                        ).show()
                    }


                }

    }



    private fun getUserData(user_id: String) {
        val docRef: DocumentReference = db!!.collection(TABLE_NAME_USER).document(user_id)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document!!.exists()) {
                    val userInfo: User = document.toObject(User::class.java)!!

                    if (userInfo.profilePic!=null){
                      var  photoUrl=userInfo.profilePic
                        Glide.with(this).load(photoUrl)
                            .placeholder(R.drawable.ic_empty_image)
                            .error(R.drawable.ic_empty_image)
                            .into(iv_profile)
                    }
                  binding.tvName.text=  userInfo.name
                } else {
                    Log.d(
                            Constraints.TAG,
                            "No such document"
                    )
                }
            } else {
                Log.d(
                        Constraints.TAG,
                        "get failed with ",
                        task.exception
                )
            }
        }
    }
}