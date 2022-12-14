package com.sameh.todoapp.activities

import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.sameh.todoapp.R
import com.sameh.todoapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {

//        var keepSplashOnScreen = true
//        val delay = 2000L
//        installSplashScreen().setKeepOnScreenCondition { keepSplashOnScreen }
//        Handler(Looper.getMainLooper()).postDelayed({ keepSplashOnScreen = false }, delay)

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar: ActionBar? = supportActionBar
        val color: Int = getColor(R.color.colorPrimaryForActionBar)
        val colorDrawable = ColorDrawable(color)
        actionBar?.setBackgroundDrawable(colorDrawable)

        val navController =
            binding.fragmentContainerView.getFragment<NavHostFragment>().navController
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}