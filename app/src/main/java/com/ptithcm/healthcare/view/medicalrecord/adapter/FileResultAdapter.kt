package com.ptithcm.healthcare.view.medicalrecord.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.TestResultDetail
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemFileResultBinding
import org.jetbrains.anko.windowManager
import java.util.ArrayList

class FileResultAdapter (
    private var fileResult: ArrayList<TestResultDetail>?,
    private val listener : (fileResult: TestResultDetail?) -> Unit
) : RecyclerView.Adapter<FileResultAdapter.FileResultViewHolder>() {


    fun setListFile(file: ArrayList<TestResultDetail>) {
        this.fileResult= file
        notifyDataSetChanged()
    }

    fun getConsult() : ArrayList<TestResultDetail>? = this.fileResult


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileResultViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemFileResultBinding>(LayoutInflater.from(parent.context), R.layout.item_file_result, parent, false)
        return FileResultViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: FileResultViewHolder, position: Int) {

        val file = fileResult?.get(position)
        if (file != null) {
            (holder as? FileResultViewHolder)?.bind(file)
        }
        holder.itemView.setOnClickListener {
            listener.invoke(file)
        }
    }

    override fun getItemCount(): Int = fileResult?.size ?: 0


    inner class FileResultViewHolder(private val viewBinding: ItemFileResultBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(fileResult: TestResultDetail) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.item = fileResult

        }
    }

}