package com.ptithcm.healthcare.view.doctor.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemMedicalBillBinding
import org.jetbrains.anko.windowManager

class ConsultationRecyclerViewAdapter (
    private var medicalBill: ArrayList<MedicalBill>?,
    private val listener : (medicalBill : MedicalBill?) -> Unit
) : RecyclerView.Adapter<ConsultationRecyclerViewAdapter.ConsultViewHolder>() {


    fun setListConsult(medicalBill: ArrayList<MedicalBill>) {
        this.medicalBill = medicalBill
        notifyDataSetChanged()
    }

    fun getConsult() : ArrayList<MedicalBill>? = this.medicalBill


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemMedicalBillBinding>(LayoutInflater.from(parent.context), R.layout.item_medical_bill, parent, false)
        return ConsultViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ConsultViewHolder, position: Int) {

        val medicalBill = medicalBill?.get(position)
        (holder as? ConsultViewHolder)?.bind(medicalBill)
        holder.itemView.setOnClickListener {
            listener.invoke(medicalBill)
        }
    }

    override fun getItemCount(): Int = medicalBill?.size ?: 0


    inner class ConsultViewHolder(private val viewBinding: ItemMedicalBillBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(medicalBill: MedicalBill?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.item = medicalBill
        }
    }

}