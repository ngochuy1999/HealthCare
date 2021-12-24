package com.ptithcm.healthcare.view.medicalrecord

import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentMedicalRecordPagerBinding
import com.ptithcm.healthcare.view.medicalrecord.adapter.MedicalRecordPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MedicalRecordPagerFragment : BaseFragment<FragmentMedicalRecordPagerBinding>(),
    ViewPager.OnPageChangeListener {
    override val layoutId: Int
        get() = R.layout.fragment_medical_record_pager

    private lateinit var adapter: MedicalRecordPagerAdapter

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
        adapter = MedicalRecordPagerAdapter(childFragmentManager, arrayListOf())
        adapter.addFragment(MedicalRecordBookFragment.newInstance(0))
        adapter.addFragment(TestResultFragment.newInstance(1))
        viewBinding.viewPager.offscreenPageLimit =  adapter.count
        viewBinding.viewPager.adapter = adapter
        viewBinding.tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)

        viewBinding.tabLayout.getTabAt(0)?.text = getString(R.string.medical_records)
        viewBinding.tabLayout.getTabAt(1)?.text = getString(R.string.test_results)
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