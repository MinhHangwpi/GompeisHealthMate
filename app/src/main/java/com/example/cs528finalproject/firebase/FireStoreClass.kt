package com.example.cs528finalproject.firebase

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cs528finalproject.*
import com.example.cs528finalproject.fragment.ProfileFragment
import com.example.cs528finalproject.fragment.ScanFragment
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.security.AccessController.getContext

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

    fun updateUserProfileData(
        activity: MainActivity,
        userHashMap: HashMap<String, Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener{
                Log.e("UPDATE PROFILE", "Profile Data Updated Successfully!")
                Toast.makeText(activity, "Profile Data Updated Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e ->
                Log.e("UPDATE PROFILE", "Profile Data Update error $e")
            }
    }

    /* load user data */

    fun loadUserData(activity: AppCompatActivity, callback: (User?) -> Unit) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null) {
                    when (activity) {
                        is SignInActivity -> activity.signInSuccess(loggedInUser)
                        is MainActivity -> activity.setUserDataInUI(loggedInUser)
                        else -> Log.w(
                            "FIRESTORE CLASS",
                            "Unhandled activity type: ${activity.javaClass.simpleName}"
                        )
                    }
                    callback(loggedInUser)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Error fetching user info from Firestore")
                callback(null)
            }
    }

    /* TODO:  a function to get the meal information */

    fun getMealByUserId(callback: (ArrayList<Meal>?) -> Unit) {
        mFireStore.collection(Constants.MEALS)
            .whereEqualTo("userId", getCurrentUserId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val meals = ArrayList<Meal>()
                // Loop through the documents in the query snapshot to get the activity data
                for (document in querySnapshot.documents) {
                    val mealData = document.toObject(Meal::class.java)
                    if (mealData != null) {
                        Log.d("MEAL INFO", "$mealData")
                        meals.add(mealData)
                    }
                }
                callback(meals)
            }
    }

    /* a function to post a meal to the database*/

    fun postAMealData(mealObj: Meal, activity: MainActivity){
        mFireStore.collection(Constants.MEALS)
            .add(mealObj)
            .addOnSuccessListener{
                Log.d("MEAL ACTIVITY", "Added a meal Successfully to Firebase!")
                Toast.makeText(activity, "Added a meal Successfully to Firebase!", Toast.LENGTH_SHORT).show()
//               TODO: activity.mealUpdateSuccess()
            }
            .addOnFailureListener{e ->
                Log.e("MEAL ACTIVITY", "Error Uploading a Meal $e")
            }
    }

    fun getExerciseByUserId(callback: (ArrayList<Exercise>?) -> Unit){
        mFireStore.collection(Constants.EXERCISES)
            .whereEqualTo("userId", getCurrentUserId())
            .get()
            .addOnSuccessListener { querySnapshot ->
                val exercises = ArrayList<Exercise>()
                // Loop through the documents in the query snapshot to get the activity data
                for (document in querySnapshot.documents) {
                    val exerciseData = document.toObject(Exercise::class.java)

                    if (exerciseData != null) {
                        Log.d("EXERCISE INFO", "$exerciseData")
                        exercises.add(exerciseData)
                    }
                }
                callback(exercises)
            }
    }

    /* function to post the activity information to the database*/
    /* TODO: to invoke this function after an activity ends (i.e. transition = exit) */

    fun postAnExercise(activity: MainActivity, exerciseObj: Exercise){
        mFireStore.collection(Constants.EXERCISES)
            .add(exerciseObj)
            .addOnSuccessListener{
                Log.d("POST EXERCISE", "Exercise Posted Successfully!")
                Toast.makeText(activity, "Exercise Posted Successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{e ->
                Log.e("POST EXERCISE", "Error posting exercise $e")
            }
    }



    /* fetch the list of foods from a location, on a specific day within a certain caloric limit */
    //TODO: To invoke this once the user enters a Geofence zone
    fun fetchFoodMenus(activity: MainActivity){
        mFireStore.collection(Constants.MENUS)
            .whereEqualTo("location", "campus-center")
            .whereEqualTo("date", "2023-4-1")
            .whereLessThanOrEqualTo("calories", 500)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name")
                    val calories = document.getDouble("calories")
                    // Do something with the name and calories
                }
            }
            .addOnFailureListener { e ->
                Log.d("FIRESTORE", "Error fetching menu items $e")
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