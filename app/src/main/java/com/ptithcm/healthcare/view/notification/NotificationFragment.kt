package com.ptithcm.healthcare.view.notification

import android.os.Bundle
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentNotificationBinding
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.ext.isShowErrorNetwork
import com.ptithcm.healthcare.ext.isShowLoading
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.notification.adapter.NotifiactionAdapter
import com.ptithcm.healthcare.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {
    override val layoutId: Int
        get() = R.layout.fragment_notification


    private val homeViewModel: HomeViewModel by viewModel()

    private lateinit var notifiactionAdapter: NotifiactionAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel.getNotification(CoreApplication.instance.account?.accountId)
    }

    override fun bindEvent() {
        viewBinding.size = 1
        notifiactionAdapter = NotifiactionAdapter()
        viewBinding.rvInvoices.adapter = notifiactionAdapter
        viewBinding.swlRefresh.setOnRefreshListener {
            homeViewModel.getNotification(CoreApplication.instance.account?.accountId)
        }
        super.bindEvent()
        setupToolbar()
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            initToolBar(
                viewBinding.layoutToolbar.toolbar, false,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(
                viewBinding.layoutToolbar.toolbar,
                getString(R.string.notification)
            )
        }
    }

    override fun bindViewModel() {
        homeViewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })
        homeViewModel.notificationLiveData.observe(this, Observer {
            viewBinding.swlRefresh.isRefreshing = false
            if (it != null) {
                viewBinding.size = it.size
                notifiactionAdapter.addToList(it)
            }
        })
        homeViewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })

    }

    //
//    private fun setupRecyclerview() {
//
//        layoutManager = LinearLayoutManager(requireContext())
//        layoutManager.reverseLayout = true
//        viewBinding.rvMessages.layoutManager = layoutManager
//        viewBinding.rvMessages.setHasFixedSize(true)
//        viewBinding.rvMessages.setItemViewCacheSize(20)
////        viewBinding.rvMessages.adapter = adapter
//        viewBinding.rvMessages.addOnScrollListener(object :
//            RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(
//                recyclerView: RecyclerView,
//                newState: Int
//            ) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    if (layoutManager.findFirstVisibleItemPosition() <= 0) {
//                        messageCollection?.fetchSucceededMessages(
//                            MessageCollection.Direction.NEXT,
//                            null
//                        )
//                    }
////                    if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1) {
////                        messageCollection?.fetchSucceededMessages(
////                            MessageCollection.Direction.PREVIOUS,
////                            null
////                        )
////                    }
//                }
//            }
//        })
//    }
//
//
//    private fun fetchNewMessage() {
//        if (messageCollection != null) {
//            if (layoutManager.findFirstVisibleItemPosition() <= 0) {
//                messageCollection?.fetchSucceededMessages(
//                    MessageCollection.Direction.NEXT,
//                    null
//                )
//            }
////            if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1) {
////                messageCollection?.fetchSucceededMessages(
////                    MessageCollection.Direction.PREVIOUS,
////                    null
////                )
////            }
//        }
//    }*/
}