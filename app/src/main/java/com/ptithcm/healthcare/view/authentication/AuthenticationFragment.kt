package com.ptithcm.healthcare.view.authentication

import android.os.Bundle
import android.view.View
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentAuthenticationBinding
import com.ptithcm.healthcare.ext.*


class AuthenticationFragment : BaseFragment<FragmentAuthenticationBinding>(), View.OnClickListener{

    override val layoutId = R.layout.fragment_authentication


    override fun bindEvent() {
        super.bindEvent()
        viewBinding.loginBtn.setOnClickListener(this)
        viewBinding.registerBtn.setOnClickListener(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    private fun setupToolbar() {
        (requireActivity() as? AuthenticationActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.gone()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login_btn -> {
                navController.navigate(R.id.loginFragment)
            }
            R.id.register_btn -> {
                navController.navigate(R.id.registerFragment)
            }
        }
    }

}