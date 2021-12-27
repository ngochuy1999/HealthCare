package com.ptithcm.healthcare.view.medicalhistory

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.ptithcm.core.model.Invoice
import com.ptithcm.core.vo.Result
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentMedicalHistoryBookBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MedicalHistoryBookFragment : BaseFragment<FragmentMedicalHistoryBookBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_medical_history_book

    private val userViewModel: UserViewModel by viewModel()

    private val mActivity: BaseActivity<*> by lazy {
        requireActivity() as BaseActivity<*>
    }

    companion object {
        fun newInstance(medicalId: Int): MedicalHistoryBookFragment =   MedicalHistoryBookFragment().apply {
            arguments = Bundle().apply {
                putInt("medicalId", medicalId)
            }
        }
    }

    private var medicalTitle: String? = ""
    private var medicalStatusId: Int? = 0

    private val adapter = MedicalHistoryPagedAdapter { it: Invoice?, _: Int? ->
        navController.navigateAnimation(
            R.id.mediacalBillDetailFragment,
            bundle = bundleOf(
                KEY_ARGUMENT to it?.id
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       arguments?.let {
           medicalStatusId = it.getInt("medicalId", 0)
           userViewModel.getPagingInvoices(statusId = medicalStatusId ?: 0)
       }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity.isShowLoading(true)
        setupToolbar()
        initAdapter()
    }

    override fun onResume() {
        super.onResume()
        mActivity.btnNav?.visibility = View.GONE
        medicalTitle = when (medicalStatusId) {
            0 -> getString(R.string.all_medical)
            1 -> getString(R.string.complete)
            2 -> getString(R.string.canceled)
            else -> return
        }
        setupToolbar()
    }

    private fun initAdapter() {
        viewBinding.rvInvoices.adapter = adapter
        viewBinding.swlRefresh.setOnRefreshListener {
            userViewModel.getPagingInvoices(statusId = medicalStatusId ?: 0)
        }
    }

    override fun bindViewModel() {
        userViewModel.invoicesLiveData.observe(this, Observer {
            viewBinding.swlRefresh.isRefreshing = false
            mActivity.isShowLoading(false)
            if (it != null && it.size != 0) {
                viewBinding.tvEmpty.gone()
                adapter.submitList(it)
            } else viewBinding.tvEmpty.visible()
        })

        userViewModel.invoiceLoadStatusX.observe(this, Observer {
            adapter.setNetworkState(it)
            when (it) {
                is Result.Error -> {
                    if (adapter.currentList?.isEmpty() == true) {
                        (requireActivity() as? BaseActivity<*>)?.isShowErrorNetwork(true)
                    }
                    mActivity.isShowLoading(false)
                }
                is Result.Loading -> mActivity.isShowLoading(true)
                else -> mActivity.isShowLoading(false)
            }
        })
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            initToolBar(
                viewBinding.layoutToolbar.toolbar, true,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(
                viewBinding.layoutToolbar.toolbar,
//                getString(R.string.invoice_book).capitalize()
                medicalTitle ?: ""
            )
        }
    }
}