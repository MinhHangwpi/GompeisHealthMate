package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.example.cs528finalproject.databinding.ActivityIntroBinding
import com.example.cs528finalproject.firebase.FireStoreClass

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        /* To move from IntroActivity to SignUp Activity */
        binding.btnSignUpIntro.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        /* To move from IntroActivity to SignIn Activity */
        binding.btnSignInIntro.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        Looper.myLooper()?.let {
            Handler(it).postDelayed({

                /* autologin feature*/
                // <- START ->
                var currentUserID = FireStoreClass().getCurrentUserId()

                if (currentUserID.isNotEmpty()){
                    startActivity(Intent(this, MainActivity::class.java))
                }
                finish()
                // <- END ->

            }, 2500)
        }
    }
}