package com.example.cs528finalproject.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentProfileBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.example.cs528finalproject.services.LocationService
import com.example.cs528finalproject.viewmodels.UserViewModel


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var mUserDetails: User ?= null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var age = binding.etAge
        var height = binding.etHeight
        var weight = binding.etWeight
        var targetGained = binding.etTargetGained
        var targetBurned = binding.etTargetBurned

        binding.btnSave.isEnabled = false

        binding.btnSave.setOnClickListener {
            updateUserProfileData()
        }

        // get the user info from the ViewModel
        val userViewModel: UserViewModel by activityViewModels()
        mUserDetails = userViewModel.selectedUser.value
        Log.d("ProfileFragment", "received mUserDetails object: ${mUserDetails?.name}")
        mUserDetails?.let{
            setUserDataInUI(it)
        }

        binding.btnLogout.setOnClickListener {
            userViewModel.logOut()
        }



        binding.bgStart.setOnClickListener{
            Log.i("LOCATION","TEST CLICK")
            Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                requireActivity().startService(this)
            }
        }
        binding.bgStop.setOnClickListener{
            Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                requireActivity().startService(this)
            }
        }





        // define the text watcher
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // check if both fields have some value
                binding.btnSave.isEnabled = age.text.isNotEmpty() && height.text.isNotEmpty() && weight.text.isNotEmpty() && targetGained.text.isNotEmpty() && targetBurned.text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        age.addTextChangedListener(textWatcher)
        height.addTextChangedListener(textWatcher)
        weight.addTextChangedListener(textWatcher)
        targetGained.addTextChangedListener(textWatcher)
        targetBurned.addTextChangedListener(textWatcher)

        // run checked required fields for all fields
        /*binding.etAge.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkRequiredFields()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkRequiredFields()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkRequiredFields()
            }
        })*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkRequiredFields() {
        binding.btnSave.isEnabled = binding.etAge.text.toString().isNotEmpty() &&
                binding.etHeight.text.toString().isNotEmpty() &&
                binding.etWeight.text.toString().isNotEmpty() &&
                binding.etTargetGained.text.toString().isNotEmpty() &&
                binding.etTargetBurned.text.toString().isNotEmpty()
    }

    private fun setUserDataInUI(user: User) {
        mUserDetails = user
        // update user profile picture
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.profile_pic)
            .into(binding.ivUserProfilePicture)

        // set user name and hints
        binding.tvUser.text = "Hello, ${user.name}"
        binding.etEmail.hint = "${user.email}"

        if (user.age != 0) {
            binding.etAge.hint = user.age.toString()
        }
        if (user.weight != 0.0) {
            binding.etWeight.hint = user.weight.toString()
        }
        if (user.height != 0.0) {
            binding.etHeight.hint = user.height.toString()
        }
        binding.etTargetGained.hint = user.targetGained.toString()
        binding.etTargetBurned.hint = user.targetBurned.toString()
    }

    private fun updateUserProfileData() {
        // create a user hashmap
        val userHashMap = HashMap<String, Any>()
        // keep track of any changes, to call FireBaseStore.update.. if needed
        //var anyChangesMade = false

        /* Note: current business logic assumes that user can't change user name */

        if (binding.etWeight.text.toString().toDouble() != mUserDetails?.weight) {
            userHashMap["weight"] = binding.etWeight.text.toString().toDouble()
            //anyChangesMade = true
        }
        if (binding.etHeight.text.toString().toDouble() != mUserDetails?.height) {
            userHashMap["height"] = binding.etHeight.text.toString().toDouble()
            //anyChangesMade = true
        }

        if (binding.etAge.text.toString().toInt() != mUserDetails?.age) {
            userHashMap["age"] = binding.etAge.text.toString().toInt()
            //anyChangesMade = true
        }

        if (binding.etTargetGained.text.toString().toDouble() != mUserDetails?.targetGained) {
            userHashMap["targetGained"] = binding.etTargetGained.text.toString().toDouble()
            //anyChangesMade = true
        }

        if (binding.etTargetBurned.text.toString().toDouble() != mUserDetails?.targetBurned) {
            userHashMap["targetBurned"] = binding.etTargetBurned.text.toString().toDouble()
            //anyChangesMade = true
        }

        //Log.i("anyChangesMade", anyChangesMade.toString())

        //if (anyChangesMade) {
        FireStoreClass().updateUserProfileData(requireActivity() as MainActivity, userHashMap)
        val userViewModel: UserViewModel by activityViewModels()
            userViewModel.setUser(
                userViewModel.selectedUser.value!!.copy(
                    weight = binding.etWeight.text.toString().toDouble(),
                    height = binding.etHeight.text.toString().toDouble(),
                    age = binding.etAge.text.toString().toInt(),
                    targetGained = binding.etTargetGained.text.toString().toDouble(),
                    targetBurned = binding.etTargetBurned.text.toString().toDouble()
                ))
        }
        //}
}