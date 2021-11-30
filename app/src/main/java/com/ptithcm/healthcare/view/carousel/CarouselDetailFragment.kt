package com.ptithcm.healthcare.view.carousel

import android.os.Bundle
import android.view.View
import com.ptithcm.core.model.Provider
import com.ptithcm.core.util.ObjectHandler
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentCarouselDetailBinding
import com.ptithcm.healthcare.ext.gone
import com.ptithcm.healthcare.ext.navigateAnimation
import com.ptithcm.healthcare.ext.transparentStatusBar
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.carousel.adapter.CarouselViewPagerAdapter
import com.ptithcm.healthcare.viewmodel.ProvidersViewModel
import kotlinx.android.synthetic.main.fragment_carousel_detail.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class CarouselDetailFragment : BaseFragment<FragmentCarouselDetailBinding>(), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_carousel_detail

    private val providersViewModel: ProvidersViewModel by viewModel()
    private var provider: Provider? = null
    private var adapter: CarouselViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        provider = arguments?.getParcelable(KEY_ARGUMENT)
        providersViewModel.provider = provider
    }

    override fun bindEvent() {
        super.bindEvent()

        (requireActivity() as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.gone()
            transparentStatusBar(true)
        }

        viewBinding.provider = provider
        providersViewModel.getDetailProvider(providerId = provider?.id)
        setupToolbar()
        initViewPager()
        initTabLayout()
        initEvent()
    }

    override fun bindViewModelOnce() {
        providersViewModel.providerDetailResult.observe(this, androidx.lifecycle.Observer {})
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivBack -> {
                navController.popBackStack()
            }
            R.id.ivLeft -> {
                navController.navigateAnimation(R.id.nav_search)
            }
            R.id.ivRight -> {
                navController.navigateAnimation(R.id.nav_shopping_card, isBotToTop = true)
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.viewBinding?.btnNav?.gone()

        viewBinding.collapsingToolbarLayout.title = provider?.brandName?.toUpperCase(Locale.ROOT)

        val countBags = ObjectHandler.getNumberItem()
        viewBinding.toolbar.tvCount?.apply {
            when {
                countBags > 0 -> text = countBags.toString()
                else -> this.gone()
            }
        }
    }

    private fun initEvent() {
        viewBinding.ivBack.setOnClickListener(this)
        viewBinding.ivLeft.visible()
        viewBinding.ivRight.visible()
        viewBinding.ivLeft.setOnClickListener(this)
        viewBinding.ivRight.setOnClickListener(this)
    }

    private fun initViewPager() {
        adapter = CarouselViewPagerAdapter(childFragmentManager)
        viewBinding.viewPager.offscreenPageLimit = CarouselViewPagerAdapter.PAGE_ALL_NUMBER
        viewBinding.viewPager.adapter = adapter
    }

    private fun initTabLayout() {
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager)
        viewBinding.tabLayout.getTabAt(0)?.text = getString(R.string.product)
        viewBinding.tabLayout.getTabAt(1)?.text = getString(R.string.about)
    }
}