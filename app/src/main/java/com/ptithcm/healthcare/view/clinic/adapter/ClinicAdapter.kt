package com.ptithcm.healthcare.view.clinic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.Clinic
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemClinicBinding
import java.util.ArrayList

class ClinicAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val clinic = arrayListOf<Clinic>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemClinicBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_clinic,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = clinic.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(clinic[position])
    }

    fun addToList(arr: ArrayList<Clinic>) {
        this.clinic.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(val binding: ItemClinicBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Clinic) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}