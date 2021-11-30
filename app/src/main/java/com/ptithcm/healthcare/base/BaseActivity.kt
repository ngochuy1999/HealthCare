package com.ptithcm.healthcare.base

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import com.ptithcm.core.util.AppEvent
import com.ptithcm.core.util.NetworkListener
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.LayoutNoInternetBinding
import com.ptithcm.healthcare.ext.hideKeyboard
import com.ptithcm.healthcare.ext.isShowLoading
import com.ptithcm.healthcare.util.BottomPopupDialog
import com.ptithcm.healthcare.util.PopUp
import com.ptithcm.healthcare.util.PopupDialog
import com.ptithcm.healthcare.view.MainActivity
import java.util.*

abstract class
BaseActivity<ViewBinding : ViewDataBinding> : AppCompatActivity(), NetworkListener {

    lateinit var viewBinding: ViewBinding

    private var listOfPopupDialogFragment: ArrayList<DialogFragment?>? = arrayListOf()

    @get:LayoutRes
    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppEvent.registerNetworkListener(this)
        viewBinding = DataBindingUtil.setContentView(this, layoutId)
        viewBinding.lifecycleOwner = this
        bindView()
        bindViewModel()
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideKeyboard()
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    open fun bindView() {}

    open fun bindViewModel() {}

    override fun onDestroy() {
        super.onDestroy()
        AppEvent.unRegisterNetworkListener(this)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        (this as? MainActivity)?.isShowLoading(false)
    }

    fun showPopup(popup: PopUp?) {
        closePopup()
        val popupDialogFragment =
            if (popup?.isBottomSheet == false) PopupDialog.newInstance(popup) else BottomPopupDialog.newInstance(
                popup
            )
        popupDialogFragment.show(supportFragmentManager, PopupDialog().tag)
        listOfPopupDialogFragment?.add(popupDialogFragment)
    }

    fun closePopup() {
        for (item in listOfPopupDialogFragment ?: arrayListOf()) {
            item?.dismissAllowingStateLoss()
        }
        listOfPopupDialogFragment?.clear()
    }

    override fun onNoInternet() {
        showPopup(PopUp(R.layout.layout_no_internet, messageQueue = this::popUpErrorEvent))
    }

    private fun popUpErrorEvent(popupBinding: ViewDataBinding?) {
        (popupBinding as? LayoutNoInternetBinding)?.apply {
            btnCancel.setOnClickListener {
                closePopup()
            }
        }
    }
}