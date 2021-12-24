package com.ptithcm.healthcare.view.home.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.marginRight
import androidx.core.view.marginStart
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.data.remote.DynamicSearchAdapter
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.Specialize
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemsSpecializeBinding
import org.jetbrains.anko.windowManager

class SpecializeRecyclerViewAdapter(
    private var specialize: MutableList<Specialize> = arrayListOf(),
    private val listener : (specialize : Specialize?) -> Unit
) : DynamicSearchAdapter<Specialize>(specialize) {


    fun setSpecialize(specialize: ArrayList<Specialize>) {
        this.specialize.apply {
            clear()
            addAll(specialize)
            updateData(specialize)
            notifyDataSetChanged()

        }
    }

    fun addDataSearch(arr: MutableList<Specialize>) {
        this.specialize.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    fun removeAllData() {
        this.specialize.apply {
            clear()
            notifyDataSetChanged()
        }
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecializeViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemsSpecializeBinding>(LayoutInflater.from(parent.context), R.layout.items_specialize, parent, false)
        return SpecializeViewHolder(dataBinding)
    }


    override fun getItemCount(): Int = specialize.size


    inner class SpecializeViewHolder(private val viewBinding: ItemsSpecializeBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(specialize: Specialize?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val width = displayMetrics.widthPixels
            viewBinding.ivImage.layoutParams.width = (width - viewBinding.ivImage.marginStart - viewBinding.ivImage.marginRight) / 3
            viewBinding.ivImage.layoutParams.height = (viewBinding.ivImage.layoutParams.width * 0.5).toInt()
            viewBinding.specialize = specialize
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val specialize = specialize.get(position)
        (holder as? SpecializeViewHolder)?.bind(specialize)
        holder.itemView.setOnClickListener {
            listener.invoke(specialize)
        }
    }
}