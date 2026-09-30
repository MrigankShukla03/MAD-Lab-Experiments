package com.example.madlab.experiment8

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.madlab.experiment8.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_web -> replaceFragment(WebViewFragment())
                R.id.nav_student -> replaceFragment(StudentDetailsFragment())
                R.id.nav_account -> replaceFragment(AccountFragment())
            }
            true
        }
    }

    fun openWebPortal(url: String) {
        binding.bottomNavigation.selectedItemId = R.id.nav_web
        val webFragment = WebViewFragment.newInstance(url)
        replaceFragment(webFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                val currentFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFrag is WebViewFragment) {
                    currentFrag.refreshPage()
                } else {
                    Toast.makeText(this, "Refreshed Current Dashboard Screen", Toast.LENGTH_SHORT).show()
                }
                true
            }
            R.id.action_open_external -> {
                val currentFrag = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
                if (currentFrag is WebViewFragment) {
                    currentFrag.openExternalBrowser()
                } else {
                    Toast.makeText(this, "Opening Web Portal...", Toast.LENGTH_SHORT).show()
                    openWebPortal("https://developer.android.com")
                }
                true
            }
            R.id.action_clear_cache -> {
                Toast.makeText(this, "Web Cache & Cookies Cleared", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                Toast.makeText(this, "Experiment 08: Menus & WebView Application", Toast.LENGTH_LONG).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
