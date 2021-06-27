package com.dailynotes.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dailynotes.R
import com.dailynotes.databinding.ActivityForgotPasswordBinding
import com.dailynotes.util.helper.CommonMethods
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.toolbar_common.*


class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tv_title.text=getString(R.string.forgot_password)
        iv_back.setOnClickListener { onBackPressed() }
        iv_back.visibility= View.VISIBLE

    }

    fun onSubmitClick(view: View) {
        CommonMethods.showProgress(this)

        FirebaseAuth.getInstance().sendPasswordResetEmail(binding.etEmail.text.toString().trim())
            .addOnCompleteListener { task ->
                CommonMethods.hideProgress()
                if (task.isSuccessful) {
                    Toast.makeText(this@ForgotPasswordActivity, "Please check email", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                   // Log.d(TAG, "Email sent.")
                }else{
                    Toast.makeText(this@ForgotPasswordActivity, "Please check your email", Toast.LENGTH_SHORT).show()

                }
            }

    }

}