package com.example.cs528finalproject.firebase

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cs528finalproject.*
import com.example.cs528finalproject.fragment.ProfileFragment
import com.example.cs528finalproject.fragment.ScanFragment
import com.example.cs528finalproject.models.Exercise
import com.example.cs528finalproject.models.FoodMenu
import com.example.cs528finalproject.models.Meal
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.security.AccessController.getContext
import java.text.SimpleDateFormat
import java.util.Date

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
                        is MockExerciseActivity -> activity.setUserDataInUI(loggedInUser)
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

    fun getMealByUserId(){
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

    fun getExerciseByUserId(){
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
                Log.d("POST EXERCISE", "Exercise Posted Successfully!")
                Toast.makeText(activity, "Exercise Posted Successfully!", Toast.LENGTH_SHORT).show()
                activity.exerciseUpdateSuccess()
            }
            .addOnFailureListener{e ->
                Log.e("POST EXERCISE", "Error posting exercise $e")
            }
    }

    /* fetch the list of foods from a location, on a specific day within a certain caloric limit */
    //TODO: To invoke this once the user enters a Geofence zone
    fun fetchFoodMenus(activity: MainActivity, location: String, date: Date, callback: (ArrayList<FoodMenu>?) -> Unit){
        val foodMenus = ArrayList<FoodMenu>()

        val dateFormat = SimpleDateFormat("yyyy-M-d")
        val dateString = dateFormat.format(date)

        mFireStore.collection(Constants.MENUS)
            .whereEqualTo("location", location)
            .whereEqualTo("date", dateString)
//            .whereLessThanOrEqualTo("calories", 500)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val name = document.getString("name")
                    val calories = document.getDouble("calories")
                    val id = document.id
                    if (name != null && calories != null && calories != null) {
                        Log.i("FOOD", name)
                        foodMenus.add(FoodMenu(id, name, location, calories))
                    }
                }
                callback(foodMenus)
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