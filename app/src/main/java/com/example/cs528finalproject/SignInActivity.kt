package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.cs528finalproject.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* To remove the battery bar on the top*/
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setupActionBar()

        /* For Firebase sign in */
        auth = FirebaseAuth.getInstance()


        // when a user signs in, pass the email and password
        binding.btnSignIn.setOnClickListener {
            var email = binding.etEmail.text.toString()
            var password = binding.etPassword.text.toString()

            if (email == null || email.trim().isEmpty()){
                this?.let{
                    Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener // return to prevent further execution of code
            }

            if (password == null || email.trim().isEmpty()){
                this?.let{
                    Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener // return to prevent further execution of code
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
//                        // Sign in success, update UI with the signed-in user's information
                        Log.d("FIREBASE AUTH", "signInWithEmail:success")
//                        val user = auth.currentUser
////                        updateUI(user)
                        Toast.makeText(this, "sign in successful...", Toast.LENGTH_SHORT).show()

                        /* After this we can redirect the user to the MainActivity Screen */
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("FIREBASE AUTH", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Sign In failed...",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
        }
    }

    /* Set up an action bar at the top left to go back to the intro activity */
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignInActivity)

        val actionBar = supportActionBar
        actionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        /* To go back to the IntroActivity screen */
        binding.toolbarSignInActivity.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    public override fun onStart() {
        super.onStart()
        // Check if the user is already logged in, then will open the mainActivity
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload()
        }
    }

    private fun reload(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}