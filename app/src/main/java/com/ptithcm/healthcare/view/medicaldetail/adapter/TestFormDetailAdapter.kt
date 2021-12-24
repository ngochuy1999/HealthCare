package com.ptithcm.healthcare.view.medicaldetail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.TestFormDetail
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemTestFormDetailBinding
import java.util.*

class TestFormDetailAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val testFormDetails = arrayListOf<TestFormDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemTestFormDetailBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_test_form_detail,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = testFormDetails.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(testFormDetails[position])
    }

    fun addToList(arr: ArrayList<TestFormDetail>) {
        this.testFormDetails.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(val binding: ItemTestFormDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TestFormDetail) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}