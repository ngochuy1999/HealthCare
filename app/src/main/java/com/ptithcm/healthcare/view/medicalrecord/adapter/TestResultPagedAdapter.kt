package com.ptithcm.healthcare.view.medicalrecord.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.TestResult
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.databinding.ItemTestResultBinding
import org.jetbrains.anko.windowManager

class TestResultPagedAdapter(
    private var testResult: ArrayList<TestResult>?,
    private val listener : (testResult: TestResult?) -> Unit
) : RecyclerView.Adapter<TestResultPagedAdapter.TestResultViewHolder>() {


    fun setListTestResult(testResult: ArrayList<TestResult>) {
        this.testResult= testResult
        notifyDataSetChanged()
    }

    fun getConsult() : ArrayList<TestResult>? = this.testResult


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestResultViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemTestResultBinding>(LayoutInflater.from(parent.context), R.layout.item_test_result, parent, false)
        return TestResultViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: TestResultViewHolder, position: Int) {

        val testResult = testResult?.get(position)
        (holder as? TestResultViewHolder)?.bind(testResult)
        holder.itemView.setOnClickListener {
            listener.invoke(testResult)
        }
    }

    override fun getItemCount(): Int = testResult?.size ?: 0


    inner class TestResultViewHolder(private val viewBinding: ItemTestResultBinding) :
        RecyclerView.ViewHolder(viewBinding.root) {

        fun bind(testResult: TestResult?) {
            val displayMetrics = DisplayMetrics()
            viewBinding.root.context.windowManager.defaultDisplay.getMetrics(displayMetrics)
            viewBinding.item = testResult

        }
    }

}