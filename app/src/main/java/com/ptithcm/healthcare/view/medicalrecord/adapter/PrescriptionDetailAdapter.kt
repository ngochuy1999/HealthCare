package com.ptithcm.healthcare.view.medicalrecord.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.Prescription
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemPrescriptionDetailBinding
import java.util.ArrayList

class PrescriptionDetailAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val prescriptionDetail = arrayListOf<Prescription>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemPrescriptionDetailBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_prescription_detail,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = prescriptionDetail.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(prescriptionDetail[position])
    }

    fun addToList(arr: ArrayList<Prescription>) {
        this.prescriptionDetail.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(val binding: ItemPrescriptionDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Prescription) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}