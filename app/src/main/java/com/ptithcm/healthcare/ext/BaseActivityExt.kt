package com.ptithcm.healthcare.ext

import android.app.Activity
import android.view.View
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.databinding.ActivityAuthenticationBinding
import com.ptithcm.healthcare.databinding.ActivityMainBinding
import com.ptithcm.healthcare.view.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

fun BaseActivity<*>.initToolbar(
    hasBack: Boolean = true, hasBackRight: Boolean = true,
    hasLeft: Boolean = true, hasRight: Boolean = true, hasTextLeft: Boolean = false,
    hasTextRight: Boolean = false, hasCloseButton: Boolean = false, hasCount: Boolean = true,
    isProductPage: Boolean = false
) {
    (viewBinding as? ActivityMainBinding)?.apply {
        initToolBar(
            this.layoutToolbar.toolbar, hasBack, hasBackRight, hasLeft, hasRight, hasTextLeft,
            hasTextRight, hasCloseButton, hasCount, isProductPage
        )
    }
    (viewBinding as? ActivityAuthenticationBinding)?.apply {
        initToolBar(
            this.layoutToolbar.toolbar, hasBack, hasBackRight, hasLeft, hasRight, hasTextLeft,
            hasTextRight, hasCloseButton, hasCount, isProductPage
        )
    }
}

fun BaseActivity<*>.setupToolbar(
    title: String = getString(R.string.app_name), isBackPress: Boolean = true,
    messageQueue: ((view: View) -> Unit)? = null
) {
    (viewBinding as? ActivityMainBinding)?.apply {
        setupToolbar(this.layoutToolbar.toolbar, title, isBackPress, messageQueue)
    }
    (viewBinding as? ActivityAuthenticationBinding)?.apply {
        setupToolbar(this.layoutToolbar.toolbar, title, isBackPress, messageQueue)
    }
}

fun BaseActivity<*>.isShowErrorNetwork(isShow: Boolean) {
    (viewBinding as? ActivityMainBinding)?.apply {
        if (isShow) {
            this.layoutError.visible()
        } else {
            this.layoutError.gone()
        }
    }
    (viewBinding as? ActivityAuthenticationBinding)?.apply {
        if (isShow) {
            this.layoutError.visible()
        } else {
            this.layoutError.gone()
        }
    }
}

fun BaseActivity<*>.isShowLoading(isShow: Boolean) {
    (viewBinding as? ActivityMainBinding)?.apply {
        if (isShow) {
            this.layoutLoading.visible()
        } else {
            this.layoutLoading.gone()
        }
    }
}

fun BaseActivity<*>.isShowLoadingPayment(isShow: Boolean) {
    (viewBinding as? ActivityMainBinding)?.apply {
        if (isShow) {
            this.layoutLoadingPayment.visible()
        } else {
            this.layoutLoadingPayment.gone()
        }
    }
}

fun BaseActivity<*>.goToShop() {
    (this as? MainActivity)?.viewBinding?.btnNav?.selectedItemId = R.id.nav_home
}

fun MainActivity.goToShopFromWishList() {
    btnNav.selectedItemId = R.id.nav_home
}