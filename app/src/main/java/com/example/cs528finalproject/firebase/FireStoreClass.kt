package com.example.cs528finalproject.firebase

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.SignInActivity
import com.example.cs528finalproject.SignUpActivity
import com.example.cs528finalproject.UserProfileActivity
import com.example.cs528finalproject.models.Activity
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FireStoreClass {
    /* Fire store related code */
    private val mFireStore = FirebaseFirestore.getInstance()

    // add the user to the database every time the user signs up
    fun registerUser(activity: SignUpActivity, userInfo: User){
        mFireStore.collection(Constants.USERS) // this performs the same thing as if we are creating a new collection via Firestore console
            .document(getCurrentUserId()) // want to create doc for every user we have
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE", "Error registering user to Firestore")
            }
    }

    fun updateUserProfileData(activity: UserProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                Log.e("UPDATE PROFILE", "Profile Data Updated Successfully!")
                Toast.makeText(activity, "Profile Data Updated Successfully!", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener{e ->
                Log.e("UPDATE PROFILE", "Profile Data Update error $e")
            }
    }



    /* load user data */

    fun loadUserData(activity: AppCompatActivity){
        mFireStore.collection(Constants.USERS) // this performs the same thing as if we are creating a new collection via Firestore console
            .document(getCurrentUserId()) // want to create doc for every user we have
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!! // make a user object out of the document

                // behave differently depending on different activities
                when (activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is UserProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE", "Error fetching user info from Firestore")
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser // if this return null, there is no current user
        var currentUserId = ""
        if (currentUser != null){
            currentUserId = currentUser.uid
        }
        return currentUserId
    }
}