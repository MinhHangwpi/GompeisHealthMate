package com.example.cs528finalproject.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentProfileBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
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

        // run checked required fields for all fields
        binding.etAge.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkRequiredFields()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                checkRequiredFields()
            }

            override fun afterTextChanged(p0: Editable?) {
                checkRequiredFields()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun checkRequiredFields() {
        binding.btnSave.isEnabled = binding.etAge.text.toString().isNotEmpty() &&
                binding.etHeight.text.toString().isNotEmpty() &&
                binding.etWeight.text.toString().isNotEmpty() &&
                binding.etTarget.text.toString().isNotEmpty()
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
    }

    private fun updateUserProfileData() {
        // create a user hashmap
        val userHashMap = HashMap<String, Any>()
        // keep track of any changes, to call FireBaseStore.update.. if needed
        var anyChangesMade = false

        /* Note: current business logic assumes that user can't change user name */

        if (binding.etWeight.text.toString().toDouble() != mUserDetails?.weight) {
            userHashMap["weight"] = binding.etWeight.text.toString().toDouble()
            anyChangesMade = true
        }
        if (binding.etHeight.text.toString().toDouble() != mUserDetails?.height) {
            userHashMap["height"] = binding.etHeight.text.toString().toDouble()
            anyChangesMade = true
        }

        if (binding.etAge.text.toString().toInt() != mUserDetails?.age) {
            userHashMap["age"] = binding.etAge.text.toString().toInt()
            anyChangesMade = true
        }

        if (anyChangesMade) {
            binding.btnSave.isEnabled = true
            FireStoreClass().updateUserProfileData(requireActivity() as MainActivity, userHashMap)
        }
    }
}