package com.ptithcm.healthcare.view.home.adapter

import android.app.Activity
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ptithcm.core.model.Doctor
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemsDoctorBinding
import org.jetbrains.anko.windowManager

class DoctorRecyclerViewAdapter (
    private var doctor: ArrayList<Doctor>?,
    private val listener : (doctor : Doctor?) -> Unit
) : RecyclerView.Adapter<DoctorRecyclerViewAdapter.DoctorViewHolder>() {


    fun setListDoctor(doctor: ArrayList<Doctor>) {
        this.doctor = doctor
        notifyDataSetChanged()
    }

    fun getDoctor() : ArrayList<Doctor>? = this.doctor


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemsDoctorBinding>(LayoutInflater.from(parent.context), R.layout.items_doctor, parent, false)
        return DoctorViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {

        val doctor = doctor?.get(position)
        (holder as? DoctorViewHolder)?.bind(doctor)
        holder.itemView.setOnClickListener {
            listener.invoke(doctor)
        }
    }

    override fun getItemCount(): Int = doctor?.size ?: 0


    inner class DoctorViewHolder(private val viewBinding: ItemsDoctorBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(doctor: Doctor?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.doctor = doctor
            val glideApp = Glide.with(itemView.context)
            glideApp.load(doctor?.imageUrl)
                .centerCrop()
                .error(R.drawable.doctor)
                .into(viewBinding.avatar)
        }
    }

}