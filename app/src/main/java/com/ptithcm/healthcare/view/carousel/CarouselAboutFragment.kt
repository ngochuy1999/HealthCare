package com.ptithcm.healthcare.view.carousel

import androidx.lifecycle.Observer
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentCarouselAboutBinding
import com.ptithcm.healthcare.viewmodel.ProvidersViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CarouselAboutFragment : BaseFragment<FragmentCarouselAboutBinding>() {

    override val layoutId: Int = R.layout.fragment_carousel_about

    private val providersViewModel: ProvidersViewModel by sharedViewModel(from = { requireParentFragment() })

    override fun bindViewModel() {
        super.bindViewModel()

        providersViewModel.providerDetailResult.observe(this, Observer {
            viewBinding.information = it.infomation
        })
    }
}