package com.ptithcm.healthcare.view.document

import android.os.Build
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.ShopInfo
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentDocumentBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import kotlinx.android.synthetic.main.activity_main.*

class DocumentFragment : BaseFragment<FragmentDocumentBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_document

    private var shopInfo: ShopInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shopInfo = CoreApplication.instance.shopInfo
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.btnNav?.visibility = View.GONE
        when (arguments?.getString(KEY_ARGUMENT)) {
            getString(R.string.about_us) -> viewBinding.webView.loadUrl("file:///android_asset/about_us.html")
            getString(R.string.hospital) -> viewBinding.webView.loadUrl("file:///android_asset/hospital_info.html")

            getString(R.string.terms_and_conditions) -> viewBinding.webView.loadUrl("file:///android_asset/terms_conditions.html")
            getString(R.string.customer_service) -> viewBinding.webView.loadUrl("file:///android_asset/customer_service.html")
            getString(R.string.delivery_returns) -> viewBinding.webView.loadUrl("file:///android_asset/delivery_return.html")
        }

        setupToolbar()
    }

    private fun setTextAboutUs() {
        viewBinding.webView.gone()
        viewBinding.tvContent.visible()
        viewBinding.tvContent.text = shopInfo?.description?:""
    }

    private fun setupToolbar() {
        (requireActivity() as? BaseActivity<*>)?.apply {
            initToolbar(
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar( arguments?.getString(KEY_ARGUMENT) ?: "",true, messageQueue ={
                navController.popBackStack()
            } )
            when (this){
                is MainActivity -> viewBinding.layoutToolbar.ivBack.setImageResource(R.drawable.ic_back)
            }
        }
    }
}