package com.ptithcm.healthcare.view.file


import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.*
import com.ptithcm.healthcare.databinding.FragmentFileBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.AuthenticateViewModel
import com.ptithcm.healthcare.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FileFragment : BaseFragment<FragmentFileBinding>(), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_file

    private val authViewModel: AuthenticateViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private var currentProfile: Profile? = null
    private var currentAccount: Account? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            isShowLoading(false)
        }

        setupToolbar()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CoreApplication.instance.profile != null) {
            userViewModel.getProfile()
        }
        observeViewModel()
    }

    override fun bindEvent() {
        currentAccount = CoreApplication.instance.account
    }

    private fun observeViewModel() {
        authViewModel.logOutLiveData.observe(this, Observer {
            if (it != null) {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
                CoreApplication.instance.clearAccount()
                setupToolbar()
                (activity as? MainActivity)?.updateUIBottomNav()
            }

        })
        userViewModel.updateDetailLiveData.observe(this, Observer {
            CoreApplication.instance.saveUser(currentProfile ?: return@Observer)
        })
        userViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                messageHandler?.runMessageErrorHandler(it.first)
            }
        })
        authViewModel.error.observe(activity!!, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                Toast.makeText(requireContext(), it.first, Toast.LENGTH_SHORT).show()
            }
        })
    }




    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar,
                false,
                hasBackRight = false
            )
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.file))
            viewBinding.layoutToolbar.ivLeft.setOnClickListener {
                navController.navigate(R.id.nav_search)
            }
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_shopping_card, isBotToTop = true)
            }
        }

    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }


}