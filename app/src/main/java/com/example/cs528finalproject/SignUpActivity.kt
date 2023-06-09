package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.cs528finalproject.databinding.ActivitySignUpBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* To remove the battery bar on the top*/
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setupActionBar()

        /* for Firebase registration */

        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener {
            var email = binding.etEmail.text.toString()
            var password = binding.etPassword.text.toString()

            if (email == null || email.trim().isEmpty()){
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // return to prevent further execution of code
            }

            if (password == null || email.trim().isEmpty()){
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener // return to prevent further execution of code
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account created, directing to log in...", Toast.LENGTH_SHORT).show()

                        /* pushing the new user info to a document in Firestore */
                        val firebaseUser : FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!
                        val name = binding.etName.text.toString()

                        val user = User(firebaseUser.uid, name, registeredEmail)

                        // register user to Firebase
                        FireStoreClass().registerUser(this, user)

                        /* After this we can redirect the user back to the sign in Screen */
                        startActivity(Intent(this, SignInActivity::class.java))

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("FIREBASE AUTH", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registration failed...",
                            Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                }
        }
    }

    /* Function that pings the Firebase store when the user registers successfully */
    fun userRegisteredSuccess(){
        Toast.makeText(this, "You have successfully registered email", Toast.LENGTH_SHORT).show()
//        FirebaseAuth.getInstance().signOut()
        finish()
    }

    /* Set up an action bar at the top left to go back to the intro activity*/
    private fun setupActionBar(){
        setSupportActionBar(binding.toolbarSignUpActivity)

        val actionBar = supportActionBar
        actionBar?.let{
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        /* To go back to the IntroActivity screen */
        binding.toolbarSignUpActivity.setNavigationOnClickListener{ onBackPressedDispatcher.onBackPressed()}
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