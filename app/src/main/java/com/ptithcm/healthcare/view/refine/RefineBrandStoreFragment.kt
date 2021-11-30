package com.ptithcm.healthcare.view.refine

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import com.jay.widget.StickyHeadersLinearLayoutManager
import com.ptithcm.core.model.Designer
import com.ptithcm.core.param.BrandsRefine
import com.ptithcm.core.param.RefineParam
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentRefineBrandsStoriesBinding
import com.ptithcm.healthcare.ext.initToolbar
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.refine.adapter.BrandRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.BrandsViewModel
import com.ptithcm.healthcare.viewmodel.RefineViewModel
import com.ptithcm.healthcare.widget.fastscroll.FastScrollRecyclerViewItemDecoration
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RefineBrandStoreFragment : BaseFragment<FragmentRefineBrandsStoriesBinding>(),
    View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_refine_brands_stories

    private val viewModel: BrandsViewModel by sharedViewModel(from = { requireActivity() })
    private val refineViewModel: RefineViewModel by sharedViewModel(from = { requireActivity() })

    private lateinit var adapter: BrandRecyclerViewAdapter
    val numberSelected = ObservableField(0)
    private var refineParam: RefineParam? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refineParam = arguments?.getParcelable(KEY_ARGUMENT)
        viewModel.requestStoriesRefine(refineParam?.storiesRefine?.storiesId)
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
        viewModel.brandsStoriesRefineLiveData.observe(this, Observer {
            initChoose(it, refineParam)
            adapter.brands = it
            adapter.mapIndex = viewModel.calculateIndexesForName(it)
            adapter.notifyDataSetChanged()
        })
    }


    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tvClear -> {
                clearBrandSelected()
            }
            R.id.btnApply -> {
                brandsSelected()
                navController.popBackStack()
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? BaseActivity<*>)?.apply {
            initToolbar(hasBackRight = false, hasLeft = false, hasRight = false)
            setupToolbar(getString(R.string.brands))
            when (this) {
                is MainActivity -> {
                    viewBinding.layoutToolbar.tvClear.apply {
                        visible()
                        setOnClickListener(this@RefineBrandStoreFragment)
                    }
                }
            }
        }
    }

    private fun initAdapter() {
        adapter = BrandRecyclerViewAdapter(arrayListOf(), hashMapOf(), this::listener)
        val decoration = FastScrollRecyclerViewItemDecoration()
        viewBinding.rvBrands.layoutManager =
            StickyHeadersLinearLayoutManager<BrandRecyclerViewAdapter>(requireContext())
        viewBinding.rvBrands.addItemDecoration(decoration)
        viewBinding.rvBrands.adapter = adapter
    }

    private fun initEvent() {
        viewBinding.btnApply.setOnClickListener(this)
    }

    private fun listener(designer: Designer?, position: Int) {
        adapter.brands?.get(position)?.isChoose = !(designer?.isChoose ?: false)
        adapter.notifyItemChanged(position)
        numberSelected.set(adapter.brands?.filter { it.isChoose }?.size)
    }

    private fun initChoose(designers: ArrayList<Designer>, refineParam: RefineParam?) {
        designers.map {
            if (!it.isSection) {
                it.isChoose = refineParam?.brands?.contains(
                    BrandsRefine(
                        brandId = it.brandId,
                        storeId = it.storeId,
                        name = it.name
                    )
                ) == true
            }
        }
        numberSelected.set(refineParam?.brands?.size)
    }


    private fun brandsSelected() {
        refineParam?.brands = arrayListOf()
        adapter.brands?.forEach {
            if (it.isChoose) {
                refineParam?.brands?.add(
                    BrandsRefine(
                        brandId = it.brandId,
                        storeId = it.storeId,
                        name = it.name
                    )
                )
            }
        }
    }

    private fun clearBrandSelected() {
        adapter.brands?.mapIndexed { index, designer ->
            if (designer.isChoose) {
                designer.isChoose = false
                adapter.notifyItemChanged(index)
            }
        }
        numberSelected.set(0)
    }

}