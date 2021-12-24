package com.ptithcm.healthcare.view.doctor.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.isGone
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ptithcm.core.data.remote.DynamicSearchAdapter
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemMedicalBillBinding
import com.ptithcm.healthcare.util.DateUtils.DATE_FORMAT_SERVER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.backgroundColorResource
import org.jetbrains.anko.textResource
import org.jetbrains.anko.windowManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ConsultationRecyclerViewAdapter (
    private var medicalBill: MutableList<MedicalBill> = arrayListOf(),
    private val listener : (medicalBill : MedicalBill?) -> Unit
) : DynamicSearchAdapter<MedicalBill>(medicalBill) {



    fun setListConsult(medicalBill: ArrayList<MedicalBill>) {
        this.medicalBill.apply {
            clear()
            addAll(medicalBill)
            updateData(medicalBill)
            notifyDataSetChanged()

        }
    }

    fun addDataSearch(arr: MutableList<MedicalBill>) {
        this.medicalBill.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        this.medicalBill.apply {
            clear()
            notifyDataSetChanged()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemMedicalBillBinding>(LayoutInflater.from(parent.context), R.layout.item_medical_bill, parent, false)
        return ConsultViewHolder(dataBinding)
    }


    override fun getItemCount(): Int = medicalBill.size


    inner class ConsultViewHolder(private val viewBinding: ItemMedicalBillBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(medicalBill: MedicalBill?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.item = medicalBill
            if (medicalBill != null) {

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                val sdf_sv = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.getDefault())
                val dataNow = sdf.parse(sdf.format(Calendar.getInstance().time))?.time
                val presiteFromDate = sdf_sv.parse(medicalBill.timePrediction)?.time

                val waitTime = dataNow?.let { presiteFromDate?.minus(it) }

                val diffSeconds = waitTime?.div(1000)
//                val diffMinutes = waitTime?.div((60 * 1000))?.mod(60)
//                val diffHours = waitTime?.div((60 * 60 * 1000))

                if (medicalBill.medicalBillStatus.statusId == 2) {
                    viewBinding.vTag.setBackgroundResource(R.color.blue)
                    viewBinding.tvProcess.setText(R.string.doing)
                    viewBinding.waitTime.isGone = true
                    viewBinding.txtWaitTime.isGone = true
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        if (diffSeconds != null) {
                            for (i in diffSeconds downTo 0) {
                                val hour = i / 3600
                                val padHour = "$hour".padStart (2, '0')
                                val minute = (i - hour * 3600) / 60
                                val padMinute = "$minute".padStart (2, '0')
                                val sec = i - hour * 3600 - minute * 60
                                val padSec = "$sec".padStart (2, '0')

                                viewBinding.waitTime.text = "$padHour : $padMinute : $padSec"
                                delay(1000)
                            }
                            viewBinding.txtWaitTime.text = "Đến giờ khám"
                        }
                    }
                    viewBinding.icOnline.setColorFilter(R.color.blue_cyan)

                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val medicalBill = medicalBill.get(position)
        (holder as? ConsultViewHolder)?.bind(medicalBill)
        holder.itemView.setOnClickListener {
            listener.invoke(medicalBill)
        }
    }
}