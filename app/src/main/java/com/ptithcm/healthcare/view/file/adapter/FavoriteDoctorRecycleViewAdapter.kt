package com.ptithcm.healthcare.view.doctor.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ptithcm.core.model.Doctor
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemFavotiteDoctorBinding
import org.jetbrains.anko.windowManager

class FavoriteDoctorRecycleViewAdapter (
    private var doctor: ArrayList<Doctor>?,
    private val listener : (doctor : Doctor?) -> Unit
) : RecyclerView.Adapter<FavoriteDoctorRecycleViewAdapter.FavoriteDoctorViewHolder>() {


    fun setListDoctor(doctor: ArrayList<Doctor>) {
        this.doctor = doctor
        notifyDataSetChanged()
    }

    fun getConsult() : ArrayList<Doctor>? = this.doctor


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteDoctorViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemFavotiteDoctorBinding>(LayoutInflater.from(parent.context), R.layout.item_favotite_doctor, parent, false)
        return FavoriteDoctorViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: FavoriteDoctorViewHolder, position: Int) {

        val doctor = doctor?.get(position)
        (holder as? FavoriteDoctorViewHolder)?.bind(doctor)
        holder.itemView.setOnClickListener {
            listener.invoke(doctor)
        }
    }

    override fun getItemCount(): Int = doctor?.size ?: 0


    inner class FavoriteDoctorViewHolder(private val viewBinding: ItemFavotiteDoctorBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(doctor: Doctor?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.doctor = doctor
        }
    }

}