package com.ptithcm.healthcare.view.notification.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.ShoppingAddress
import com.ptithcm.healthcare.databinding.ViewHighlightMessageOtherHolderBinding

class MessageOtherHolder (val binding: ViewHighlightMessageOtherHolderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    val context: Context = binding.root.context

    fun bind(item: ShoppingAddress) {

        binding.executePendingBindings()
    }
}