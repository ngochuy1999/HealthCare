package com.ptithcm.healthcare.view.healthrecord

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentHealthRecordsPagerBinding
import com.ptithcm.healthcare.databinding.FragmentMedicalHistoryPagerBinding
import kotlinx.android.synthetic.main.activity_main.*

class HealthRecordsPagerFragment : BaseFragment<FragmentHealthRecordsPagerBinding>(),
    ViewPager.OnPageChangeListener {
    override val layoutId: Int
        get() = R.layout.fragment_health_records_pager

    private lateinit var adapter: HealthRecordsPagerAdapter

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
        adapter = HealthRecordsPagerAdapter(childFragmentManager, arrayListOf())
        adapter.addFragment(HealthRecordsBookFragment.newInstance(0))
        adapter.addFragment(HealthRecordsAnamnesisFragment.newInstance(1))
        viewBinding.viewPager.offscreenPageLimit =  adapter.count
        viewBinding.viewPager.adapter = adapter
        viewBinding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)
        viewBinding.tabLayout.getTabAt(0)?.text = getString(R.string.overview)
        viewBinding.tabLayout.getTabAt(1)?.text = getString(R.string.anamnesis)
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