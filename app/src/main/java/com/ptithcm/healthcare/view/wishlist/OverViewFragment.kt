package com.ptithcm.healthcare.view.wishlist

import android.view.View
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentOverViewBinding
import com.ptithcm.healthcare.ext.gone
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.wishlist.overview.ImageZoomPagerAdapter

class OverViewFragment : BaseFragment<FragmentOverViewBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_over_view

    override fun bindEvent() {
        setUpViewpager()

        viewBinding.fragment = this

        (activity as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.gone()
        }
    }

    private fun setUpViewpager() {
        val list = arguments?.getStringArrayList("list")
        val pos = arguments?.getInt("pos", 0) ?: 0
        val fromRating = arguments?.getBoolean("fromRating", false) ?: false
        viewBinding.vpImage.adapter =
            ImageZoomPagerAdapter(childFragmentManager, list ?: arrayListOf())
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