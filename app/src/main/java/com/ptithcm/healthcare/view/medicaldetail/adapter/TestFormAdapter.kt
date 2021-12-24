package com.ptithcm.healthcare.view.medicaldetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.TestForm
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemTestFormBinding
import java.util.*

class TestFormAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val testForms = arrayListOf<TestForm>()

    private val testFormDetailAdapter: TestFormDetailAdapter = TestFormDetailAdapter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTestFormBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_test_form,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = testForms.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(testForms[position])
    }

    fun addToList(arr: ArrayList<TestForm>) {
        this.testForms.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(val binding: ItemTestFormBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TestForm) {
            binding.item = item
            binding.rvTestFormDetail.adapter = testFormDetailAdapter
            binding.isPay = item.isPay == 1
            binding.executePendingBindings()
        }
    }
}