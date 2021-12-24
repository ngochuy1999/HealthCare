package com.ptithcm.healthcare.view.medicalrecord.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ptithcm.core.model.TestResultDetail
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemImageResultBinding
import org.jetbrains.anko.windowManager
import java.util.ArrayList

class ImageResultAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val imageList = arrayListOf<TestResultDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemImageResultBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_image_result,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = imageList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(imageList[position])
    }

    fun addToList(arr: ArrayList<TestResultDetail>) {
        this.imageList.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(val viewBinding: ItemImageResultBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(item: TestResultDetail) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val glideApp = Glide.with(itemView.context)
            glideApp.load(item.imageUrl)
                .centerCrop()
                .error(R.drawable.ic_place_holder)
                .into(viewBinding.ivResult)
        }
    }
}