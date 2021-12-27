package com.ptithcm.healthcare.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.constant.PERMISSION_CAMERA
import com.ptithcm.healthcare.constant.PERMISSION_GALLERY
import com.ptithcm.healthcare.databinding.ActivityMainBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.viewmodel.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var currentNavController: LiveData<NavController>? = null

    override val layoutId: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.btnNav)

        val navGraphIds = listOf(
            R.navigation.nav_home,
            R.navigation.nav_calendar,
            R.navigation.nav_file,
            R.navigation.nav_notification,
            R.navigation.nav_profile
        )

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

        // Whenever the selected controller changes, setup the action bar.
//        controller.observe(this, Observer { navController ->
//            setupAction
//            BarWithNavController(navController)
//        })

        currentNavController = controller
        updateUIBottomNav()
    }

    fun updateUIBottomNav() {
        viewBinding.btnNav.disableLongClick()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CAMERA, PERMISSION_GALLERY -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPermission()
                }
            }
        }
    }

    private fun checkPermission() {
        when {
            !hasCameraPermission() -> {
                requestCameraPermission(PERMISSION_CAMERA)
            }
            !hasReadStoragePermission() -> {
                requestReadAndWriteStoragePermission(PERMISSION_GALLERY)
            }
        }
    }

}
