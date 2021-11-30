package com.ptithcm.healthcare.view.card

import android.view.View
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentConfirmCheckoutBinding
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.view.MainActivity

class ConfirmCheckoutFragment: BaseFragment<FragmentConfirmCheckoutBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_confirm_checkout

    override fun bindEvent() {
        viewBinding.fragment = this

        (activity as? MainActivity)?.apply {
            val toolBar = viewBinding.layoutToolbar.toolbar
            initToolBar(
                toolBar,
                hasBack = false,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(toolBar, getString(R.string.confirmation), isBackPress = false)
        }
    }

    fun onClick(v: View?){
        when(v?.id){
            R.id.btnBackToFeed -> {
                navController.popBackStack()
            }
        }
    }
}