package com.ptithcm.healthcare.view.clinic

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentListClinicBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.clinic.adapter.ClinicAdapter
import com.ptithcm.healthcare.view.notification.adapter.NotifiactionAdapter
import com.ptithcm.healthcare.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListClinicFragment : BaseFragment<FragmentListClinicBinding>() {

    override val layoutId: Int = R.layout.fragment_list_clinic

    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var clinicAdapter: ClinicAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.getClinic()
    }

    override fun bindEvent() {
        viewBinding.size = 1
        clinicAdapter = ClinicAdapter()
        viewBinding.rvListDoctor.adapter = clinicAdapter
        viewBinding.swipeRf.setOnRefreshListener {
            homeViewModel.getClinic()
        }
        super.bindEvent()
        setupToolbar()
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar,
                true,
                hasBackRight = false,
                hasLeft = false,
                hasCount = false,
            )
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.list_clinic))
            viewBinding.layoutToolbar.ivLeft.setOnClickListener {
                navController.navigate(R.id.nav_search)
            }
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }

    override fun bindViewModel() {
        homeViewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })
        homeViewModel.clinicLiveData.observe(this, Observer {
            viewBinding.swipeRf.setRefreshing(false)
            if (it != null) {
                viewBinding.size = it.size
                clinicAdapter.addToList(it)
            }
        })
        homeViewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })

    }


}
