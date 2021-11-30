package com.ptithcm.healthcare.view.authentication.login

import android.os.Bundle
import android.view.View
import com.google.android.material.textfield.TextInputEditText
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.param.EditAccountParam
import com.ptithcm.core.util.CHECK_FINGER
import com.ptithcm.core.util.getBooleanPref
import com.ptithcm.core.util.setBooleanPref
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.ERROR_CODE_404
import com.ptithcm.healthcare.databinding.FragmentBiometricBinding
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.ext.isShowErrorNetwork
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.util.ShowMessageHandler
import com.ptithcm.healthcare.util.checkPhonevalid
import com.ptithcm.healthcare.util.checkValidEmail
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BiometricFragment : BaseFragment<FragmentBiometricBinding>() {
    private var messageQueue: ShowMessageHandler? = null
    override val layoutId: Int
        get() = R.layout.fragment_biometric

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.btnNav?.visibility = View.GONE
        setupToolbar()

    }

    override fun bindEvent() {
        super.bindEvent()
        messageQueue = ShowMessageHandler(requireActivity())
        viewBinding.swTouch.isChecked = CoreApplication.instance.getBooleanPref(CHECK_FINGER)
        viewBinding.swTouch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                CoreApplication.instance.setBooleanPref(CHECK_FINGER,true)
                CoreApplication.instance.account?.let { CoreApplication.instance.saveAccountBio(it) }
            } else {
                CoreApplication.instance.setBooleanPref(CHECK_FINGER,false)
                CoreApplication.instance.clearAccountBio()
            }
        }
    }

    override fun onDestroyView() {
        messageQueue?.removeHandler()
        super.onDestroyView()
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            initToolBar(
                viewBinding.layoutToolbar.toolbar, true,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            viewBinding.layoutToolbar.ivBack.setImageResource(R.drawable.ic_back)
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.biometric))
        }
    }


}