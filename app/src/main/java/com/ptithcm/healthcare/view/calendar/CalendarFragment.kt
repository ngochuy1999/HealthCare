package com.ptithcm.healthcare.view.calendar

import android.os.Bundle
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
import com.ptithcm.healthcare.viewmodel.ShoppingViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class CalendarFragment : BaseFragment<FragmentCalendarBinding>() {

    override val layoutId: Int = R.layout.fragment_calendar

    private val providersViewModel: ProvidersViewModel by viewModel()
    private val shoppingViewModel: ShoppingViewModel by viewModel()

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
                            navController.navigateAnimation(
                                R.id.nav_shopping_card,
                                isBotToTop = true, bundle = bundleOf(KEY_ARGUMENT to true)
                            )
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
        setupToolbar()
        CoreApplication.instance.account?.accountId?.let { shoppingViewModel.getAllConsultPatient(it) }
        consultationAdapter =
            ConsultationRecyclerViewAdapter(arrayListOf(), this::listenerConsult)
        viewBinding.rvConsult.adapter = consultationAdapter
    }


    override fun bindViewModel() {
        super.bindViewModel()

        shoppingViewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })

        shoppingViewModel.consultationPatientResult.observe(this, Observer {
            if (it != null) {
                consultationAdapter.setListConsult(it)
            }
            viewBinding.size = it.size
        })

        shoppingViewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })

        providersViewModel.providersResult.observe(this, Observer {
        })

        providersViewModel.isLoading.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })

        providersViewModel.error.observe(this, Observer {
            isError =  true
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })
    }

    override fun bindEvent() {
        (requireActivity() as MainActivity).btnNav.visible()
    }


    private fun getProviders() {
        providersViewModel.getProviders()
    }

    private fun listenerConsult(medicalBill: MedicalBill?) {
        navController.navigateAnimation(
            R.id.nav_doctor, bundle =
            bundleOf(KEY_ARGUMENT to medicalBill)
        )
    }
}