package com.ptithcm.healthcare.view.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.Notification
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemNotificationBinding
import java.util.ArrayList

class NotifiactionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val notification = arrayListOf<Notification>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemNotificationBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_notification,
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = notification.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? ItemViewHolder)?.bind(notification[position])
    }

    fun addToList(arr: ArrayList<Notification>) {
        this.notification.apply {
            clear()
            addAll(arr)
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Notification) {
            binding.item = item
            binding.executePendingBindings()
        }
    }
}