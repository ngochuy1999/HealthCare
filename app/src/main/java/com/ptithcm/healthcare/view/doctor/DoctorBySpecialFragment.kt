package com.ptithcm.healthcare.view.doctor

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import com.ptithcm.core.model.Doctor
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.ptithcm.core.model.Specialize
import com.ptithcm.healthcare.databinding.FragmentDoctorBySpecicalBinding
import com.ptithcm.healthcare.view.home.adapter.DoctorRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.HomeViewModel

class DoctorBySpecialFragment : BaseFragment<FragmentDoctorBySpecicalBinding>() {

    override val layoutId: Int = R.layout.fragment_doctor_by_specical

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var doctorAdapter: DoctorRecyclerViewAdapter
    private lateinit var specialize: Specialize

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            isShowLoading(false)
        }
        specialize = arguments?.getParcelable(KEY_ARGUMENT) ?: Specialize()
        specialize.specialityId?.let { viewModel.getDoctorBySpecialize(it) }

        setupToolbar()

    }


    override fun bindEvent() {
        doctorAdapter =
            DoctorRecyclerViewAdapter(arrayListOf(), this::listenerDoctor)
        viewBinding.rvListDoctor.adapter = doctorAdapter

        viewBinding.edtSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                eventSearch(s.toString())
            }
        })

        viewBinding.swipeRf.setOnRefreshListener {
            specialize.specialityId?.let { viewModel.getDoctorBySpecialize(it) }
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()

        viewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })


        viewModel.doctorSpecializeLiveData.observe(this, Observer {
            viewBinding.swipeRf.setRefreshing(false)
            viewBinding.size = it.size
            if (it != null) {
                doctorAdapter.setListDoctor(it)
            }
        })

        viewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })
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
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.list_doctor))
            viewBinding.layoutToolbar.ivLeft.setOnClickListener {
                navController.navigate(R.id.nav_search)
            }
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }


    private fun listenerDoctor(doctor: Doctor?) {
        navController.navigateAnimation(
            R.id.nav_doctor, bundle =
            bundleOf(KEY_ARGUMENT to doctor)
        )
    }
    fun eventSearch(strSearch: String) {
//        if(strSearch == ""){
//            productAdapter.setProductList(productViewModel.listProducts.value?: arrayListOf())
//        }
        doctorAdapter.search(strSearch, onNothingFound={
            doctorAdapter.removeAllData()
            viewBinding.size = 0
        }, onSearchResult={
            doctorAdapter.addDataSearch(it)
            viewBinding.size = 1
        })
    }

}