package com.ptithcm.healthcare.view.refine

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.Size
import com.ptithcm.core.param.RefineParam
import com.ptithcm.core.param.SizeRefine
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentSizesBinding
import com.ptithcm.healthcare.ext.initToolbar
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.refine.adapter.SizeRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.ProductFilterViewModel
import com.ptithcm.healthcare.viewmodel.RefineViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SizesFragment : BaseFragment<FragmentSizesBinding>(), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_sizes

    val numberSelected = ObservableField(0)

    private val viewModel: ProductFilterViewModel by sharedViewModel(from = { requireActivity() })
    private val refineViewModel: RefineViewModel by sharedViewModel(from = { requireActivity() })

    private lateinit var adapter: SizeRecyclerViewAdapter
    private var refineParam: RefineParam? = null
    private var sizeType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refineParam = arguments?.getParcelable(KEY_ARGUMENT)
    }

    override fun bindEvent() {
        super.bindEvent()
        viewBinding.data = this
        setupToolbar()
        initAdapter()
        initEvent()
    }

    override fun bindViewModel() {
        super.bindViewModel()
        viewModel.productFilterLiveData.observe(this, Observer {
            val sizesArray = if (sizeType == 0) it.size
            else it.size?.filter { item -> item.size_type == sizeType } as? ArrayList<Size>
            initChooseSizes(sizesArray, refineParam)
            adapter.sizes = sizesArray
            adapter.notifyDataSetChanged()
        })
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvClear -> {
                clearSizesSelected()
            }

            R.id.btnApply -> {
                sizesSelected()
                navController.popBackStack()
            }
        }
    }

    private fun initEvent() {
        viewBinding.btnApply.setOnClickListener(this)
    }

    private fun setupToolbar() {
        (requireActivity() as? BaseActivity<*>)?.apply {
            initToolbar(  hasBackRight = false, hasLeft = false, hasRight = false)
            setupToolbar(getString(R.string.sizes))
            when(this){
                is MainActivity -> {
                    viewBinding.layoutToolbar.tvClear.visible()
                    viewBinding.layoutToolbar.tvClear.setOnClickListener(this@SizesFragment)
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = SizeRecyclerViewAdapter(arrayListOf(), this::listener)
        viewBinding.rvSizes.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewBinding.rvSizes.adapter = adapter
    }

    private fun listener(size: Size?, position: Int) {
        adapter.sizes?.get(position)?.isChoose = !(size?.isChoose ?: false)
        adapter.notifyItemChanged(position)
        numberSelected.set(adapter.sizes?.filter { it.isChoose }?.size ?: 0)
    }

    private fun initChooseSizes(sizes: ArrayList<Size>?, refineParam: RefineParam?) {
        sizes?.map {
            it.isChoose = refineParam?.sizes?.contains(SizeRefine(it.value, it.text)) == true
        }
        numberSelected.set(sizes?.filter { it.isChoose }?.size ?: 0)
    }

    private fun sizesSelected() {
        refineParam?.sizes = arrayListOf()
        adapter.sizes?.forEach {
            if (it.isChoose) {
                refineParam?.sizes?.add(SizeRefine(it.value, it.text))
            }
        }
    }

    private fun clearSizesSelected() {
        adapter.sizes?.map {
            it.isChoose = false
        }
        adapter.notifyDataSetChanged()
        numberSelected.set(0)
    }

}