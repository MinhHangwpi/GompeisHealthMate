package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.cs528finalproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        user = auth.currentUser


        if (user == null){
            reload()
        } else {
            binding.tvUser.text = user?.email
        }
        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            reload()
        }
    }

    private fun reload(){
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }
}