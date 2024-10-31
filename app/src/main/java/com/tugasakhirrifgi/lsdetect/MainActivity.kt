package com.tugasakhirrifgi.lsdetect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tugasakhirrifgi.lsdetect.databinding.ActivityMainBinding
import com.tugasakhirrifgi.lsdetect.ui.login.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Thread.sleep(800)
        installSplashScreen()

        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
        }catch (e: Exception) {
            Log.e(TAG, "onCreateView", e)
            throw e
        }

        auth = Firebase.auth

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_detection,
                R.id.navigation_profile,
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onStart() {
        super.onStart()

        val firebaseUser = auth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
    }

    companion object {
        const val TAG = "MainActivity"
    }
}