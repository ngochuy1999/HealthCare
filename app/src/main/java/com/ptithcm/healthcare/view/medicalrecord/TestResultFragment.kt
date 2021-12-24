package com.ptithcm.healthcare.view.medicalrecord

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.TestResult
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentTestResultBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.medicalrecord.adapter.TestResultPagedAdapter
import com.ptithcm.healthcare.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TestResultFragment : BaseFragment<FragmentTestResultBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_test_result

    private val userViewModel: UserViewModel by viewModel()
    private lateinit var testResultAdapter: TestResultPagedAdapter

    private val mActivity: BaseActivity<*> by lazy {
        requireActivity() as BaseActivity<*>
    }

    companion object {
        fun newInstance(medicalId: Int): TestResultFragment =   TestResultFragment().apply {
            arguments = Bundle().apply {
                putInt("medicalId", medicalId)
            }
        }
    }

    private var medicalTitle: String? = ""
    private var medicalStatusId: Int? = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            medicalStatusId = it.getInt("medicalId", 0)
            userViewModel.getPagingInvoices(statusId = medicalStatusId ?: 0)
        }
        userViewModel.getTestResult(CoreApplication.instance.account?.accountId)
    }

    override fun bindEvent() {
        super.bindEvent()
        viewBinding.fragment = this
        testResultAdapter = TestResultPagedAdapter(arrayListOf(), this::listenerTestResult)
        viewBinding.rvTestResult.adapter = testResultAdapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mActivity.isShowLoading(true)
        viewBinding.size = 1
        setupToolbar()
        initAdapter()

    }

    override fun onResume() {
        super.onResume()
        mActivity.btnNav?.visibility = View.GONE
        medicalTitle = when (medicalStatusId) {
            0 -> getString(R.string.medical_records)
            1 -> getString(R.string.test_results)
            else -> return
        }
        setupToolbar()
    }

    private fun initAdapter() {
        //viewBinding.rvInvoices.adapter = adapter
        viewBinding.swlRefresh.setOnRefreshListener {
            userViewModel.getTestResult(CoreApplication.instance.account?.accountId)
        }
    }

    override fun bindViewModel() {
        userViewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })
        userViewModel.testResultLiveData.observe(this, Observer {
            viewBinding.swlRefresh.isRefreshing = false
            if (it != null) {
                viewBinding.size = it.size
                testResultAdapter.setListTestResult(it)
            }
        })
        userViewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
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
                medicalTitle ?: ""
            )
        }
    }

    private fun listenerTestResult(testResult: TestResult?) {
        navController.navigateAnimation(
            R.id.testResultDetailFragment, bundle =
            bundleOf(KEY_ARGUMENT to testResult)
        )
    }
}