package com.ptithcm.healthcare.view.search

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Filter
import com.ptithcm.core.model.ProductClothes
import com.ptithcm.core.model.SearchParams
import com.ptithcm.core.vo.Result
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.constant.KEY_IS_SHOW_FILTER_BY
import com.ptithcm.healthcare.constant.KEY_SEARCH
import com.ptithcm.healthcare.databinding.FragmentSearchResultBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.CarouselDetailViewModel
import com.ptithcm.healthcare.viewmodel.RefineViewModel
import com.ptithcm.healthcare.viewmodel.WishListViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchResultFragment : BaseFragment<FragmentSearchResultBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_search_result


    private val viewModelProduct: CarouselDetailViewModel by viewModel()
    private val viewModelRefine: RefineViewModel by sharedViewModel(from = { requireActivity() })
    private val wishListViewModel: WishListViewModel by viewModel()

    private var key: String? = null

    private var filterParam: Filter? = Filter()

    private lateinit var scrollListener: RecyclerView.OnScrollListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        key = arguments?.getString(KEY_SEARCH)
        initRequest(keySearch = key)
    }

    override fun bindEvent() {
        super.bindEvent()
        (activity as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
        }
        viewBinding.fragment = this
        setupToolbar()
        initScroll()
    }

    override fun bindViewModel() {
        super.bindViewModel()
//        viewModelProduct.refineProductLiveData.observe(this, Observer {
//            adapter.submitList(it)
//        })
//
//        viewModelProduct.productLoadStatusX.observe(this, Observer {
//            adapter.setNetworkState(it)
//            when (it) {
//                is Result.Error -> {
//                    if (adapter.currentList?.isEmpty() == true) {
//                        (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
//                    }
//                }
//            }
//        })

        viewModelProduct.networkStateRefine.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })

        if (!viewModelRefine.filterLiveData.hasObservers())
            viewModelRefine.filterLiveData.observe(this, Observer {
                it?.let {
                    if (it.second) {
                        filterParam = it.first
                        initRequest(keySearch = key, filter = filterParam)
                        viewModelRefine.filterLiveData.value = Pair(filterParam, false)
                    }
                }
            })

        wishListViewModel.addAndRemoveResult.observe(this, Observer {})
        wishListViewModel.error.observe(this, Observer {
            (requireActivity() as? BaseActivity<*>)?.isShowErrorNetwork(true)
        })
    }

    fun onClick(view: View?) {
        when (view?.id) {
            R.id.btnFab -> {
                viewBinding.rvProducts.smoothScrollToPosition(0)
                viewBinding.btnFab.runAnimationAlpha(false)
            }
        }
    }

    private fun initScroll() {
        var isRunning = false
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val position =
                    (viewBinding.rvProducts.layoutManager as? GridLayoutManager)?.findFirstVisibleItemPosition()
                        ?: 0
                if (position > 0) {
                    if (!isRunning) {
                        viewBinding.btnFab.runAnimationAlpha(true)
                        isRunning = !isRunning
                    }
                } else {
                    if (isRunning) {
                        viewBinding.btnFab.runAnimationAlpha(false)
                        isRunning = !isRunning
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        (activity as? MainActivity)?.apply {
            val toolbar = viewBinding.layoutToolbar.toolbar
            toolbar.visible()
            initToolBar(toolbar, hasBackRight = false)
            setupToolbar(toolbar, key ?: "All", messageQueue = {
                when (it.id) {
                    R.id.ivRight, R.id.tvCount -> {
                        navController.navigateAnimation(
                            R.id.nav_qrcode,
                            isBotToTop = true
                        )
                    }
                    R.id.ivLeft -> {
                        navController.navigateAnimation(R.id.fragment_search)
                    }
                }
            })
        }
    }

    private fun initRequest(keySearch: String? = "", filter: Filter? = Filter()) {
        val request = SearchParams(
            keySearch = keySearch,
            accountId = CoreApplication.instance.account?.accountId,
            filter = filter
        )
        viewModelProduct.searchPagingProducts(request)
    }
}