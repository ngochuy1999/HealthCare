package com.ptithcm.healthcare.view.specialize

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.ptithcm.core.model.Specialize
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentListSpecializeBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.home.adapter.SpecializeRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ListSpecializeFragment : BaseFragment<FragmentListSpecializeBinding>() {

    override val layoutId: Int = R.layout.fragment_list_specialize

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var specializeAdapter: SpecializeRecyclerViewAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            isShowLoading(false)
        }
        viewModel.getAllSpecialize()
        setupToolbar()

    }

    override fun bindEvent() {
        specializeAdapter =
            SpecializeRecyclerViewAdapter(arrayListOf(), this::listenerSpecialize)
        viewBinding.rvListSpecialize.adapter = specializeAdapter
        viewBinding.rvListSpecialize.layoutManager = GridLayoutManager(requireContext(), 4)

        viewBinding.swipeRf.setOnRefreshListener {
            viewModel.getAllSpecialize()
        }

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

        viewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })

        viewModel.allSpecializeLiveData.observe(this, Observer {
            viewBinding.swipeRf.setRefreshing(false)
            viewBinding.size = it.size
            if (it != null) {
                specializeAdapter.setSpecialize(it)
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
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.list_specialize))
            viewBinding.layoutToolbar.ivLeft.setOnClickListener {
                navController.navigate(R.id.nav_search)
            }
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }


    private fun listenerSpecialize(specialize: Specialize?) {
        navController.navigateAnimation(
            R.id.doctorBySpecialFragment, bundle =
            bundleOf(KEY_ARGUMENT to specialize)
        )
    }
    fun eventSearch(strSearch: String) {
//        if(strSearch == ""){
//            productAdapter.setProductList(productViewModel.listProducts.value?: arrayListOf())
//        }
        specializeAdapter.search(strSearch, onNothingFound={
            specializeAdapter.removeAllData()
            viewBinding.size = 0
        }, onSearchResult={
            specializeAdapter.addDataSearch(it)
            viewBinding.size = 1
        })
    }

}