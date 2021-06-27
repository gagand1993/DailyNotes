package com.dailynotes.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dailynotes.R
import com.dailynotes.databinding.ActivitySignupBinding
import com.dailynotes.util.helper.ConstantUtility.Companion.TABLE_NAME_USER
import com.dailynotes.util.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.toolbar_common.*


class SignupActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
     private var mAuth: FirebaseAuth? = null
    var userid=""

    private var  db: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// from Firebase Firestore.
        db = FirebaseFirestore.getInstance();

        tv_title.text=getString(R.string.signup)

    }

    fun onSignupClick(view: View) {


        mAuth!!.createUserWithEmailAndPassword(
            binding.etEmail.text.toString().trim(),
            binding.etPassword.text.toString().trim()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   // Log.d(TAG, "createUserWithEmail:success")
                    val user = mAuth!!.currentUser
                    userid=  user!!.uid
                    Toast.makeText(
                        baseContext, "Signup Done",
                        Toast.LENGTH_SHORT
                    ).show()
                    addInfoInFireStore()
                } else {
                    // If sign in fails, display a message to the user.
                    //Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                   // updateUI(null)
                }
            }

    }

    private fun addInfoInFireStore() {

        // creating a collection reference
        // for our Firebase Firetore database.
        // creating a collection reference
        // for our Firebase Firetore database.
        val dbCourses = db!!.collection(TABLE_NAME_USER)

        // adding our data to our courses object class.

        // adding our data to our courses object class.
        val courses = User(binding.etName.text.toString().trim(), binding.etEmail.text.toString().trim(),"")

        // below method is use to add data to Firebase Firestore.

        // below method is use to add data to Firebase Firestore.
        dbCourses.document(userid).set(courses)

            .addOnSuccessListener { // after the data addition is successful

            // we are displaying a success toast message.
            Toast.makeText(
                this@SignupActivity,
                "Your Info has been added to Firebase Firestore",
                Toast.LENGTH_SHORT
            ).show()
            onBackPressed()
        }
            .addOnFailureListener { e -> // this method is called when the data addition process is failed.
                // displaying a toast message when data addition is failed.
                Toast.makeText(this@SignupActivity, "Fail to add course \n$e", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}