package com.ptithcm.healthcare.view.medicalrecord.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MedicalRecordPagerAdapter(fm: FragmentManager, private val arrFragment: ArrayList<Fragment>) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
       return arrFragment[position]
    }

    fun addFragment(fragment: Fragment){
        arrFragment.add(fragment)
    }

    override fun getCount(): Int = arrFragment.size
}