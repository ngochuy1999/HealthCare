package com.ptithcm.healthcare.view.document

import android.os.Bundle
import android.view.View
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.ShopInfo
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentDocumentBinding
import com.ptithcm.healthcare.ext.gone
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_brand_term_condition.*

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
            "About Us" -> setTextAboutUs() //viewBinding.webView.loadUrl("file:///android_asset/about_us.html")
            "Terms & Conditions" -> viewBinding.webView.loadUrl("file:///android_asset/terms_conditions.html")
            "Customer Service" -> viewBinding.webView.loadUrl("file:///android_asset/customer_service.html")
            "Delivery & Returns" -> viewBinding.webView.loadUrl("file:///android_asset/delivery_return.html")
        }

        setupToolbar()
    }

    private fun setTextAboutUs() {
        viewBinding.webView.gone()
        viewBinding.tvContent.visible()
        viewBinding.tvContent.text = shopInfo?.description?:""
    }

    private fun setupToolbar() {
        (requireActivity() as? AuthenticationActivity)?.apply {
            initToolBar(
                viewBinding.layoutToolbar.toolbar, true,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(
                viewBinding.layoutToolbar.toolbar,
                (arguments?.getString(KEY_ARGUMENT) ?: "").capitalize()
            )
        }
    }
}