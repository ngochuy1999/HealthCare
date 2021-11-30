package com.ptithcm.healthcare.view.wishlist.overview

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class ImageZoomPagerAdapter(
    fm: FragmentManager,
    private val promotionBanners: List<String>,
    private val listener: ((list: List<String>, pos: Int) -> Unit)? = null
) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): androidx.fragment.app.Fragment {
        val promotionBanner = promotionBanners[position]
        return ImageZoomFragment.newInstance(promotionBanner) {
            listener?.invoke(promotionBanners, position)
        }
    }

    override fun getCount(): Int = promotionBanners.size
}