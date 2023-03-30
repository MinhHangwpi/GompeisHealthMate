package com.example.cs528finalproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.cs528finalproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        replaceFragment(Activities());

        binding.bottomNavigationView.setOnItemSelectedListener {

            when(it.itemId){

                R.id.activities -> replaceFragment(Activities())
                R.id.food -> replaceFragment(Food())
                R.id.profile -> replaceFragment(Profile())
                R.id.scan -> replaceFragment(Scan())

                else ->{



                }

            }

            true

        }
    }

    private fun replaceFragment(fragment : Fragment){

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()


    }
}