package com.example.tripjournal.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tripjournal.R
import com.example.tripjournal.databinding.ActivityMainBinding
import com.example.tripjournal.fragments.ExploreFragment
import com.example.tripjournal.fragments.MapFragment
import com.example.tripjournal.fragments.MemoriesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(ExploreFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_explore -> replaceFragment(ExploreFragment())
                R.id.nav_map -> replaceFragment(MapFragment())
                R.id.nav_memories -> replaceFragment(MemoriesFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
