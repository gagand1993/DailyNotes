package com.dailynotes.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dailynotes.R
import com.dailynotes.databinding.ActivityLoginBinding
import com.dailynotes.util.helper.AppController
import com.dailynotes.util.helper.ConstantUtility.Companion.USER_ID
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.toolbar_common.*

class LoginActivity : AppCompatActivity() {

    lateinit var binding:ActivityLoginBinding
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        tv_title.text=getString(R.string.login)

    }

    fun onLoginClick(view: View) {

        mAuth!!.signInWithEmailAndPassword(binding.etEmail.text.toString().trim(), binding.etPassword.text.toString().trim())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                   // Log.d(TAG, "signInWithEmail:success")
                    val user = mAuth!!.currentUser
                    AppController.getInstance().setString(USER_ID,user!!.uid)
                 //   updateUI(user)
                    Toast.makeText(baseContext, "Login Done",
                        Toast.LENGTH_SHORT).show()
                    var intent=Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                  //  Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }


    }

    fun onSignupClick(view: View) {

        var intent=Intent(this, SignupActivity::class.java)
        startActivity(intent)
    }

    fun onForgotPasswordClick(view: View) {
        var intent=Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
    }

}