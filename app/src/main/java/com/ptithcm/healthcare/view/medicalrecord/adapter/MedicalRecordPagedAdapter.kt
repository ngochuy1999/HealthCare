package com.ptithcm.healthcare.view.medicalrecord.adapter


import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ptithcm.core.model.MedicalRecord
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemMedicalRecordBinding
import org.jetbrains.anko.windowManager
import kotlin.collections.ArrayList

class MedicalRecordPagedAdapter (
    private var medicalRecord: ArrayList<MedicalRecord>?,
    private val listener : (medicalRecord: MedicalRecord?) -> Unit
) : RecyclerView.Adapter<MedicalRecordPagedAdapter.MedicalRecordViewHolder>() {


    fun setListMedicalRecord(medicalRecord: ArrayList<MedicalRecord>) {
        this.medicalRecord = medicalRecord
        notifyDataSetChanged()
    }

    fun getConsult() : ArrayList<MedicalRecord>? = this.medicalRecord


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalRecordViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemMedicalRecordBinding>(LayoutInflater.from(parent.context), R.layout.item_medical_record, parent, false)
        return MedicalRecordViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: MedicalRecordViewHolder, position: Int) {

        val medicalRecord = medicalRecord?.get(position)
        (holder as? MedicalRecordViewHolder)?.bind(medicalRecord)
        holder.itemView.setOnClickListener {
            listener.invoke(medicalRecord)
        }
    }

    override fun getItemCount(): Int = medicalRecord?.size ?: 0


    inner class MedicalRecordViewHolder(private val viewBinding: ItemMedicalRecordBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(medicalRecord: MedicalRecord?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.item = medicalRecord

        }
    }

}