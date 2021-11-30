package com.ptithcm.healthcare.view.uploadproduct

import android.os.Bundle
import android.os.Handler
import androidx.core.view.isVisible
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.databinding.ActivityUploadProductBinding
import com.ptithcm.healthcare.ext.gone
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.viewmodel.ShareDataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UploadProductActivity : BaseActivity<ActivityUploadProductBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_upload_product

    private val shareDataViewmodel by viewModel<ShareDataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareDataViewmodel.getAllTags()
    }

    fun isShowLoading(isShow : Boolean){
        if (isShow){
            viewBinding.layoutLoading.visible()
        }else {
            viewBinding.layoutLoading.gone()
        }
    }

    fun showError(msg: String) {
        if (!viewBinding.tvError.isVisible) {
            viewBinding.tvError.apply {
                text = msg
                visible()
            }
            Handler().postDelayed({
                viewBinding.tvError.gone()
            }, 2500)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
