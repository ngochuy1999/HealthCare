package com.ptithcm.healthcare.view.home

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.Specialize
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentHomeBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.home.adapter.DoctorRecyclerViewAdapter
import com.ptithcm.healthcare.view.home.adapter.SpecializeRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.layout_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(), View.OnClickListener {

    override val layoutId: Int
        get() = R.layout.fragment_home

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var specializeAdapter: SpecializeRecyclerViewAdapter
    private lateinit var doctorAdapter: DoctorRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getTopSpecialize()
        viewModel.getTopDoctor()
    }

    override fun bindEvent() {
        super.bindEvent()
        setupToolbar()
        viewBinding.fragment = this
        initHomeAdapter()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.qr -> {
                navController.navigateAnimation(
                    R.id.nav_qrcode,
                    isBotToTop = true
                )
            }
            R.id.clinic -> {
                navController.navigateAnimation(
                    R.id.listClinicFragment
                )
            }
            R.id.hospital -> {
                navController.navigate(
                    R.id.documentFragment,
                    bundleOf(KEY_ARGUMENT to resources.getString(R.string.hospital))
                )
            }
            R.id.show_doctor ->{
                navController.navigateAnimation(
                    R.id.listDoctorFragment
                )
            }
            R.id.show_special -> {
                navController.navigateAnimation(
                    R.id.listSpecializeFragment
                )
            }
            R.id.doctor ->{
                navController.navigateAnimation(
                    R.id.listDoctorFragment
                )
            }
            R.id.clinic ->{
                navController.navigateAnimation(
                    R.id.listClinicFragment
                )
            }
        }
    }


    override fun bindViewModel() {
        super.bindViewModel()

        viewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })

        viewModel.specializeLiveData.observe(this, Observer {
            if (it != null) {
                specializeAdapter.setSpecialize(it)
            }
        })

        viewModel.doctorLiveData.observe(this, Observer {
            if (it != null) {
                doctorAdapter.setListDoctor(it)
            }
        })

        viewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })
    }

    private fun initHomeAdapter() {
        specializeAdapter =
            SpecializeRecyclerViewAdapter(arrayListOf(), this::listenerSpecialize)
        viewBinding.rvSpecialize.adapter = specializeAdapter
//        viewBinding.rvSpecialize.setHasFixedSize(true)
//        viewBinding.rvSpecialize.setItemViewCacheSize(8)
        viewBinding.rvSpecialize.layoutManager = GridLayoutManager(requireContext(), 4)

        doctorAdapter =
            DoctorRecyclerViewAdapter(arrayListOf(), this::listenerDoctor)
        viewBinding.rvDoctor.adapter = doctorAdapter
//        viewBinding.rvSpecialize.setHasFixedSize(true)
//        viewBinding.rvSpecialize.setItemViewCacheSize(8)
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            initToolBar(viewBinding.layoutToolbar.toolbar, false)
            viewBinding.layoutToolbar.toolbar.tvTitleToolbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_health, 0, 0, 0)
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.health_care),
                messageQueue = {
                    when (it.id) {
                        R.id.ivRight, R.id.tvCount -> {
                            navController.navigateAnimation(
                                R.id.nav_qrcode,
                                isBotToTop = true
                            )
                        }
                        R.id.ivLeft -> {
                            navController.navigateAnimation(R.id.nav_search)
                        }
                    }
                }
            )
        }
    }

    private fun listenerSpecialize(specialize: Specialize?) {
        navController.navigateAnimation(
            R.id.doctorBySpecialFragment, bundle =
            bundleOf(KEY_ARGUMENT to specialize)
        )
    }

    private fun listenerDoctor(doctor: Doctor?) {
        navController.navigateAnimation(
            R.id.nav_doctor, bundle =
            bundleOf(KEY_ARGUMENT to doctor)
        )
    }
}