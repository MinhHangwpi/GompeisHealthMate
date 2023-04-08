package com.example.cs528finalproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.cs528finalproject.databinding.ActivityUserProfileBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth

class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var mUserDetails: User

    // constant for the permission call
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FireStoreClass().loadUserData(this)

        /* Update user profile*/
        binding.btnSave.setOnClickListener {
            updateUserProfileData()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            reload()
        }

        binding.bottomNavigationView.setOnItemReselectedListener {
            when (it.itemId){
                R.id.activities -> startActivity(Intent(this@UserProfileActivity, MockExerciseActivity::class.java))
                R.id.food -> startActivity(Intent(this@UserProfileActivity, MockMealActivity::class.java))
                R.id.profile -> startActivity(Intent(this@UserProfileActivity, UserProfileActivity::class.java))
                R.id.scan -> startActivity(Intent(this@UserProfileActivity, BarcodeScan::class.java))
                else -> {
                }
            }
            true
        }


        /* TODO: To change user profile picture */
//        binding.ivUserProfilePicture.setOnClickListener {
//            if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
//
//            } else {
//                ActivityCompat.requestPermissions(
//                    this, arrayOf(READ_EXTERNAL_STORAGE),
//                    READ_STORAGE_PERMISSION_CODE
//                )
//            }
//        }
    }
    /* TODO This function will identify the result of runtime permission after the user allows or deny based on the unique code. */

//    override fun onRequestPermissionResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ){
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == READ_STORAGE_PERMISSION_CODE){
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                // show image chooser
//            }
//        }
//    }

    /* set user information such as image, etc */
    fun setUserDataInUI(user: User){

        mUserDetails = user
        // update user profile picture
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.profile_pic)
            .into(binding.ivUserProfilePicture)

        // set user name
        binding.tvUser.text = "Hello, ${user.name}"
        binding.etEmail.hint = "${user.email}"

        if (user.age != 0){
            binding.etAge.hint = user.age.toString()
        }
        if (user.weight != 0.0){
            binding.etWeight.hint = user.weight.toString()
        }
        if (user.height != 0.0){
            binding.etHeight.hint = user.height.toString()
        }
    }

    private fun updateUserProfileData(){
        // create a user hashmap
        val userHashMap = HashMap<String, Any>()
        // keep track of any changes, to call FireBaseStore.update.. if needed
        var anyChangesMade = false

        /* Note: current business logic assumes that user can't change user name */

        if (!binding.etWeight.text.isNullOrEmpty() && binding.etWeight.text.toString().toDouble() != mUserDetails.weight) {
            userHashMap["weight"] = binding.etWeight.text.toString().toDouble()
            anyChangesMade = true
        }
        if (!binding.etHeight.text.isNullOrEmpty() && binding.etHeight.text.toString().toDouble() != mUserDetails.height) {
            userHashMap["height"] = binding.etHeight.text.toString().toDouble()
            anyChangesMade = true
        }

        if (!binding.etAge.text.isNullOrEmpty() && binding.etAge.text.toString().toInt() != mUserDetails.age) {
            userHashMap["age"] = binding.etAge.text.toString().toInt()
            anyChangesMade = true
        }

        if (anyChangesMade) {
            binding.btnSave.isEnabled = true
            binding.btnSave.setOnClickListener {
                FireStoreClass().updateUserProfileData(this, userHashMap)
                binding.btnSave.isEnabled = false
            }
        }
    }

    /* TODO: to implement the function to update profile */
    fun profileUpdateSuccess(){

    }

    private fun reload() {
        startActivity(Intent(this, IntroActivity::class.java))
        finish()
    }
}