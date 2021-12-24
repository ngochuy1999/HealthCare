package com.ptithcm.healthcare.view.rating

import android.view.View
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentImageVideoBinding
import com.ptithcm.healthcare.ext.gone
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class ImageVideoFragment : BaseFragment<FragmentImageVideoBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_image_video

    override fun bindEvent() {
        setUpViewpager()

        viewBinding.fragment = this

        (activity as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.gone()
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
    }

    private fun setUpViewpager() {
        val list = arguments?.getStringArrayList("list")
        val pos = arguments?.getInt("pos", 0) ?: 0
        viewBinding.vpImage.currentItem = pos
        viewBinding.indicator.setupWithViewPager(viewBinding.vpImage)
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivClose -> {
                navController.popBackStack()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
        }
    }
}