package com.ptithcm.healthcare.view.refine

import android.os.Bundle
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.GenderFilter
import com.ptithcm.core.param.RefineParam
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentGenderBinding
import com.ptithcm.healthcare.ext.initToolbar
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.ext.switchGender
import com.ptithcm.healthcare.ext.visible
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.refine.adapter.GenderRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.ProductFilterViewModel
import com.ptithcm.healthcare.viewmodel.RefineViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class GenderFragment : BaseFragment<FragmentGenderBinding>(), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_gender

    private val viewModel : ProductFilterViewModel by sharedViewModel(from = {requireActivity()})
    private val refineViewModel : RefineViewModel by sharedViewModel(from = {requireActivity()})

    val numberSelected = ObservableField(0)
    private lateinit var adapter : GenderRecyclerViewAdapter
    private var refineParam : RefineParam? = null

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
            initChooseGender(it.gender, refineParam)
            adapter.genders = it.gender
            adapter.notifyDataSetChanged()
        })
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.tvClear -> {
                clearGendersSelected()
            }
            R.id.btnApply -> {
                gendersSelected()
                navController.popBackStack()
            }
        }
    }

    private fun setupToolbar(){
        (requireActivity() as? BaseActivity<*>)?.apply {
            initToolbar( hasBackRight = false, hasLeft = false, hasRight = false)
            setupToolbar(getString(R.string.genders), true)
            when(this){
                is MainActivity -> {
                    viewBinding.layoutToolbar.tvClear.visible()
                    viewBinding.layoutToolbar.tvClear.setOnClickListener(this@GenderFragment)
                }
            }
        }
    }

    private fun initAdapter(){
        adapter = GenderRecyclerViewAdapter(arrayListOf(), this::listener)
        viewBinding.rvGenders.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        viewBinding.rvGenders.adapter = adapter
    }

    private fun initEvent(){
        viewBinding.btnApply.setOnClickListener(this)
    }

    private fun initChooseGender(genders : ArrayList<GenderFilter>?, refineParam: RefineParam?){
        genders?.map {
            it.isChoose = refineParam?.gender?.contains(switchGender(it.value)) == true
        }
        numberSelected.set(genders?.filter { it.isChoose }?.size ?: 0)
    }

    private fun listener(gender : GenderFilter?, position : Int){
        adapter.genders?.get(position)?.isChoose = !(gender?.isChoose ?: false)
        adapter.notifyItemChanged(position)
        numberSelected.set(adapter.genders?.filter { it.isChoose }?.size ?: 0)
    }

    private fun gendersSelected(){
        refineParam?.gender = arrayListOf()
        adapter.genders?.forEach {
            if (it.isChoose) {
                refineParam?.gender?.add(switchGender(it.value))
            }
        }
    }

    private fun clearGendersSelected(){
        adapter.genders?.map {
            it.isChoose = false
        }
        adapter.notifyDataSetChanged()
        numberSelected.set(0)
    }
}