package com.ptithcm.healthcare.view.medicalhistory

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentMedicalHistoryPagerBinding
import kotlinx.android.synthetic.main.activity_main.*

class MedicalHistoryPagerFragment : BaseFragment<FragmentMedicalHistoryPagerBinding>(),
    ViewPager.OnPageChangeListener {
    override val layoutId: Int
        get() = R.layout.fragment_medical_history_pager

    private lateinit var adapter: MedicalHistoryPagerAdapter

    private var pageSelected = 0
    override fun bindEvent() {
        super.bindEvent()

        val bundle = this.arguments
        if (bundle != null) {
            pageSelected = bundle.getInt("medicalId", 0)
        }
        initViewPager()
    }

    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
    }

    private fun initViewPager() {
        adapter = MedicalHistoryPagerAdapter(childFragmentManager, arrayListOf())
        adapter.addFragment(MedicalHistoryBookFragment.newInstance(0))
        adapter.addFragment(MedicalHistoryBookFragment.newInstance(1))
        adapter.addFragment(MedicalHistoryBookFragment.newInstance(2))
        viewBinding.viewPager.offscreenPageLimit =  adapter.count
        viewBinding.viewPager.adapter = adapter
        viewBinding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)
        viewBinding.tabLayout.getTabAt(0)?.text = getString(R.string.all_medical)
        viewBinding.tabLayout.getTabAt(1)?.text = getString(R.string.complete)
        viewBinding.tabLayout.getTabAt(2)?.text = getString(R.string.canceled)
        viewBinding.viewPager.currentItem = pageSelected
        viewBinding.viewPager.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        adapter.getItem(position)
    }

    override fun onPageScrollStateChanged(state: Int) {

    }
}