package com.dailynotes.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
 import com.dailynotes.R
import com.dailynotes.util.helper.AppController
import com.dailynotes.util.helper.ConstantUtility

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        Handler(Looper.getMainLooper()).postDelayed({

            if (!AppController.getInstance().getString(ConstantUtility.USER_ID).isEmpty()){
                var intent=Intent(this, DashboardActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                var intent= Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }


        }, 3000)
    }
}