package com.ptithcm.healthcare.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.constant.PERMISSION_CAMERA
import com.ptithcm.healthcare.constant.PERMISSION_GALLERY
import com.ptithcm.healthcare.databinding.ActivityMainBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.uploadproduct.UploadProductActivity
import com.ptithcm.healthcare.viewmodel.*
import org.jetbrains.anko.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding>() {

    private var currentNavController: LiveData<NavController>? = null

    override val layoutId: Int = R.layout.activity_main
    private val viewModel: ProductFilterViewModel by viewModel()
    private val brandViewModel: BrandsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        }
        brandViewModel.getBrands()
        brandViewModel.getStories()
        viewModel.getProductFilter()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setupBottomNavigationBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun bindViewModel() {
        super.bindViewModel()

        brandViewModel.pagedList.observe(this, Observer {})
        viewModel.error.observe(this, Observer {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
        brandViewModel.brandsLiveData.observe(this, Observer { })
        brandViewModel.storesLiveData.observe(this, Observer {})
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
            R.navigation.nav_message,
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
            else -> {
                startActivity<UploadProductActivity>()
            }
        }
    }

}
