package com.ptithcm.healthcare.view.file


import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Doctor
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.*
import com.ptithcm.healthcare.databinding.FragmentFileBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.doctor.adapter.FavoriteDoctorRecycleViewAdapter
import com.ptithcm.healthcare.viewmodel.WishListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FileFragment : BaseFragment<FragmentFileBinding>(), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_file

    private val wishListViewModel: WishListViewModel by viewModel()
    private var currentAccount: Account? = null
    private lateinit var favoriteDoctorRVAdapter : FavoriteDoctorRecycleViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.isNotEmpty = true
        (activity as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            isShowLoading(false)
        }
        wishListViewModel.getWishList()
        setupToolbar()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeViewModel()
    }

    override fun bindEvent() {
        currentAccount = CoreApplication.instance.account
        favoriteDoctorRVAdapter =
            FavoriteDoctorRecycleViewAdapter(arrayListOf(), this::listenerDoctor)
        viewBinding.rvWishList.adapter = favoriteDoctorRVAdapter

        viewBinding.cvHealth.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("medicalId", 0)
            navController.navigate(R.id.nav_health_record, bundle)
        }

        viewBinding.cvPatient.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("medicalId", 0)
            navController.navigate(R.id.nav_medical_record, bundle)
        }
    }

    private fun observeViewModel() {
        wishListViewModel.wishListResult.observe(this, Observer {
            viewBinding.isNotEmpty = it.size > 0
            if (it != null) {
                favoriteDoctorRVAdapter.setListDoctor(it)
            }
        })
        wishListViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                messageHandler?.runMessageErrorHandler(it.first)
            }
        })
    }




    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar,
                false,
                hasBackRight = false
            )
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.file))
            viewBinding.layoutToolbar.ivLeft.setOnClickListener {
                navController.navigate(R.id.nav_search)
            }
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.cv_patient -> {
                val bundle = Bundle()
                bundle.putInt("medicalId", 0)
                navController.navigate(R.id.nav_medical_record, bundle)
            }

            R.id.cv_health -> {
                val bundle = Bundle()
                bundle.putInt("medicalId", 0)
                navController.navigate(R.id.nav_health_record, bundle)
            }
        }
    }

    private fun listenerDoctor(doctor: Doctor?) {
        navController.navigateAnimation(
            R.id.nav_doctor, bundle =
            bundleOf(KEY_ARGUMENT to doctor)
        )
    }


}