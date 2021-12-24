package com.ptithcm.healthcare.view.calendar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentCalendarBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.doctor.adapter.ConsultationRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.ProvidersViewModel
import com.ptithcm.healthcare.viewmodel.MedicalBillViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    override val layoutId: Int = R.layout.fragment_calendar

    private val providersViewModel: ProvidersViewModel by viewModel()
    private val medicalBillViewModel: MedicalBillViewModel by viewModel()

    private lateinit var consultationAdapter: ConsultationRecyclerViewAdapter

    private var isError = false

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar, false, hasBackRight = false
            )
            setupToolbar(
                viewBinding.layoutToolbar.toolbar, getString(R.string.ex_calendar),
                messageQueue = {
                    when (it.id) {
                        R.id.ivRight, R.id.tvCount -> {
                            navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
                        }
                        R.id.ivLeft -> {
                            navController.navigate(R.id.nav_search)
                        }
                    }
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getProviders()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.size = 1
        setupToolbar()
        CoreApplication.instance.account?.accountId?.let { medicalBillViewModel.getAllConsultPatient(it) }
        consultationAdapter =
            ConsultationRecyclerViewAdapter(arrayListOf(), this::listenerConsult)
        viewBinding.rvConsult.adapter = consultationAdapter

        viewBinding.edtSearchProducts.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                eventSearch(s.toString())
            }
        })
    }


    override fun bindViewModel() {
        super.bindViewModel()

//        medicalBillViewModel.networkState.observe(this, Observer {
//            (requireActivity() as? MainActivity)?.isShowLoading(it)
//        })

        medicalBillViewModel.consultationPatientResult.observe(this, Observer {
            viewBinding.swipeRf.setRefreshing(false)
            if (it != null) {
                consultationAdapter.setListConsult(it)
            }
            viewBinding.size = it.size
        })

        medicalBillViewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })

//        providersViewModel.providersResult.observe(this, Observer {
//        })
//
//        providersViewModel.isLoading.observe(this, Observer {
//            (requireActivity() as? MainActivity)?.isShowLoading(it)
//        })
//
//        providersViewModel.error.observe(this, Observer {
//            isError =  true
//            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
//        })
    }

    override fun bindEvent() {
        (requireActivity() as MainActivity).btnNav.visible()
        viewBinding.swipeRf.setOnRefreshListener {
            CoreApplication.instance.account?.accountId?.let { medicalBillViewModel.getAllConsultPatient(it) }
        }
    }


    private fun getProviders() {
        providersViewModel.getProviders()
    }

    private fun listenerConsult(medicalBill: MedicalBill?) {
        navController.navigateAnimation(
            R.id.mediacalBillDetailFragment, bundle =
            bundleOf(KEY_ARGUMENT to medicalBill)
        )
    }

    fun eventSearch(strSearch: String) {
//        if(strSearch == ""){
//            productAdapter.setProductList(productViewModel.listProducts.value?: arrayListOf())
//        }
        consultationAdapter.search(strSearch, onNothingFound={
            consultationAdapter.removeAllData()
            viewBinding.size = 0
        }, onSearchResult={
            consultationAdapter.addDataSearch(it)
            viewBinding.size = 1
        })
    }
}