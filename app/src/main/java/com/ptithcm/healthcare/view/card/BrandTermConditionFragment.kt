package com.ptithcm.healthcare.view.card

import androidx.lifecycle.Observer
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.ERROR_CODE_404
import com.ptithcm.healthcare.databinding.FragmentBrandTermConditionBinding
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.ext.isShowErrorNetwork
import com.ptithcm.healthcare.ext.isShowLoading
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.MedicalBillViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class BrandTermConditionFragment : BaseFragment<FragmentBrandTermConditionBinding>(){

    override val layoutId: Int
        get() = R.layout.fragment_brand_term_condition

    private val medicalBillViewModel: MedicalBillViewModel by viewModel()

    override fun bindEvent() {
        setUpToolBar()
    }

    override fun bindViewModel() {
        medicalBillViewModel.isLoading.observe(this, Observer {
            (activity as? MainActivity)?.isShowLoading(it)
        })

        medicalBillViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404){
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            }
        })
    }

    private fun setUpToolBar(){
        (activity as? MainActivity)?.apply {
            initToolBar(viewBinding.layoutToolbar.toolbar,
                hasBack = true,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false)

            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.terms_and_conditions))
        }
    }
}