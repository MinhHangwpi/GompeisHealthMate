package com.example.cs528finalproject.firebase

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cs528finalproject.*
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.Meal
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
                    is SignInActivity -> activity.signInSuccess(loggedInUser)
                    is UserProfileActivity -> activity.setUserDataInUI(loggedInUser)
                    is MainActivity -> activity.setUserDataInUI(loggedInUser)
                    is MockMealActivity -> activity.setUserDataInUI(loggedInUser)
                    is MockExerciseActivity -> activity.setUserDataInUI(loggedInUser)
                    else -> Log.w("FIRESTORE CLASS", "Unhandled activity type: ${activity.javaClass.simpleName}")
                    // TODO: If any future activities need to display user data, add them here too
                }
            }
            .addOnFailureListener{ e ->
                Log.e("FIRESTORE", "Error fetching user info from Firestore")
            }
    }

    /* TODO:  a function to get the meal information */

    fun getMealByUserId(activity: AppCompatActivity){
        mFireStore.collection(Constants.MEALS)
            .whereEqualTo("userId", getCurrentUserId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Loop through the documents in the query snapshot to get the activity data
                for (document in querySnapshot.documents) {
                    val mealData = document.data
                    // Do something with the activity data here
                    Log.d("MEAL INFO", "$mealData")
                }
            }
    }

    /* a function to post a meal to the database*/
    /* TODO: to invoke this function after you got the nutrition from NutritionX or WPIEats */

    fun postAMealData(activity: MockMealActivity,
                              mealObj: Meal){
        mFireStore.collection(Constants.MEALS)
            .add(mealObj)
            .addOnSuccessListener{
                Log.e("MEAL ACTIVITY", "Added a meal Successfully to Firebase!")
                Toast.makeText(activity, "Added a meal Successfully to Firebase!", Toast.LENGTH_SHORT).show()
//               TODO: activity.mealUpdateSuccess()
            }
            .addOnFailureListener{e ->
                Log.e("MEAL ACTIVITY", "Error Uploading a Meal $e")
            }
    }

    /* TODO:  a function to get the activity information */

    fun getExerciseByUserId(activity: AppCompatActivity){
        mFireStore.collection(Constants.EXERCISES)
            .whereEqualTo("userId", getCurrentUserId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Loop through the documents in the query snapshot to get the activity data
                for (document in querySnapshot.documents) {
                    val activityData = document.data
                    // Do something with the activity data here
                    Log.d("EXERCISE INFO", "$activityData")
                }
            }
    }

    /* function to post the activity information to the database*/
    /* TODO: to invoke this function after you got record an activity/exercise */

    fun postAnExercise(activity: MockExerciseActivity, exerciseObj: Exercise){
        mFireStore.collection(Constants.EXERCISES)
            .add(exerciseObj)
            .addOnSuccessListener{
                Log.e("POST EXERCISE", "Exercise Posted Successfully!")
                Toast.makeText(activity, "Exercise Posted Successfully!", Toast.LENGTH_SHORT).show()
                activity.exerciseUpdateSuccess()
            }
            .addOnFailureListener{e ->
                Log.e("POST EXERCISE", "Error posting exercise $e")
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