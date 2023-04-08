package com.example.cs528finalproject.fragment

import android.os.Bundle
import android.telecom.Call.Details
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.cs528finalproject.MainActivity
import com.example.cs528finalproject.R
import com.example.cs528finalproject.databinding.FragmentProfileBinding
import com.example.cs528finalproject.firebase.FireStoreClass
import com.example.cs528finalproject.models.User
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        if (arguments != null){
            mUserDetails = requireArguments().getParcelable(ARG_PARAM)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        // retrieve the mUserDetails object from MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FireStoreClass().loadUserData(activity as MainActivity)

        binding.btnSave.setOnClickListener {
            updateUserProfileData()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setUserDataInUI(user: User) {

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

        if (binding.etWeight.text.toString().toDouble() != mUserDetails.weight) {
            userHashMap["weight"] = binding.etWeight.text.toString().toDouble()
            anyChangesMade = true
        }
        if (binding.etHeight.text.toString().toDouble() != mUserDetails.height) {
            userHashMap["height"] = binding.etHeight.text.toString().toDouble()
            anyChangesMade = true
        }

        if (binding.etAge.text.toString().toInt() != mUserDetails.age) {
            userHashMap["age"] = binding.etAge.text.toString().toInt()
            anyChangesMade = true
        }

        if (anyChangesMade) {
            binding.btnSave.isEnabled = true

//            FireStoreClass().updateUserProfileData(this, userHashMap)
        }
    }

    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2

        private val ARG_PARAM = "userDetails"
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param myUser as User.
         * @return A new instance of fragment ProfileFragment.
         */
        fun newInstance(mUserDetails: User): ProfileFragment {
            val fragment = ProfileFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_PARAM, mUserDetails)
            fragment.arguments = bundle
            return fragment
        }
    }

}