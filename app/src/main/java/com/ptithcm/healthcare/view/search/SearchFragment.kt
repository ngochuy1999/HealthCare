package com.ptithcm.healthcare.view.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_SEARCH
import com.ptithcm.healthcare.databinding.FragmentSearchBinding
import com.ptithcm.healthcare.ext.gone
import com.ptithcm.healthcare.ext.isShowLoading
import com.ptithcm.healthcare.ext.transparentStatusBar
import com.ptithcm.healthcare.view.MainActivity

class SearchFragment : BaseFragment<FragmentSearchBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_search

    private var searchKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? BaseActivity<*>)?.isShowLoading(false)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
    }

    override fun bindEvent() {
        super.bindEvent()
        viewBinding.fragment = this
        (activity as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.gone()
            transparentStatusBar(false)
        }

        eventSearch()
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> navController.popBackStack()
            R.id.ivSearch -> {
                navController.navigate(
                    R.id.fragment_search_result,
                    bundleOf(KEY_SEARCH to viewBinding.edtSearchInput.text.toString())
                )
            }
            R.id.ivCancel -> {
                viewBinding.edtSearchInput.setText("")
            }
        }
    }

    private fun eventSearch() {
        viewBinding.edtSearchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(key: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(key: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (key.toString().isBlank()) {
                    viewBinding.ivCancel.visibility = View.GONE
                } else {
                    viewBinding.ivCancel.visibility = View.VISIBLE
                    if (key.toString().length >= 2) {
                        searchKey = key.toString()
                    }
                }
            }
        })

        viewBinding.edtSearchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                navController.navigate(
                    R.id.fragment_search_result,
                    bundleOf(KEY_SEARCH to viewBinding.edtSearchInput.text.toString())
                )
            }
            true
        }
    }
}