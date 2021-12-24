package com.ptithcm.healthcare.util

import android.content.res.ColorStateList
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ptithcm.core.model.Color
import com.ptithcm.core.model.Prescription
import com.ptithcm.core.model.PromotionType
import com.ptithcm.core.model.TestFormDetail
import com.ptithcm.core.util.PriceFormat
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.ext.roundPrice
import com.ptithcm.healthcare.view.medicaldetail.adapter.TestFormDetailAdapter
import com.ptithcm.healthcare.view.medicalrecord.adapter.PrescriptionDetailAdapter
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList

object BindingAdapterText {

    @JvmStatic
    @BindingAdapter(value = ["curPage", "pageSize"], requireAll = true)
    fun setTextPager(textView: AppCompatTextView, curPage: String?, pageSize: String?) {
        val strString = textView.context.getString(R.string.text_count_pager, curPage, pageSize)
        textView.text = HtmlCompat.fromHtml(strString, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    @JvmStatic
    @BindingAdapter(value = ["textCount"], requireAll = true)
    fun setTextCount(textView: AppCompatTextView, count: Int?) {
        if (count == null || count <= 0) {
            textView.text = textView.context.getString(R.string.no_item_found)
        } else {
            textView.text = textView.context.getString(R.string.count_item_found, count)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["textPrice", "locale"], requireAll = true)
    fun setTextPrice(textView: AppCompatTextView, price: String?, locale: Locale?) {
        if (price.isNullOrEmpty()) {
            textView.text = textView.context.getString(R.string.no_price)
        } else {
            textView.text = price.roundPrice(locale ?: Locale.UK)
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["setTestFormDetail"])
    fun RecyclerView.setTestFormDetail(testFormDetail: ArrayList<TestFormDetail>?) {
        if (testFormDetail != null) {
            val testFormDetailAdapter = TestFormDetailAdapter()
            testFormDetailAdapter.addToList(testFormDetail)

            adapter = testFormDetailAdapter
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["setPrescription"])
    fun RecyclerView.setPrescription(prescription: ArrayList<Prescription>?) {
        if (prescription != null) {
            val prescriptionDetailAdapter = PrescriptionDetailAdapter()
            prescriptionDetailAdapter.addToList(prescription)

            adapter = prescriptionDetailAdapter
        }
    }

    @JvmStatic
    @BindingAdapter("txtPriceDiscount")
    fun bindPriceDiscount(view: AppCompatTextView, price: Long?) {
        val localeUS = Locale.US
        val currencyUS = NumberFormat.getCurrencyInstance(localeUS)
        view.text = currencyUS.format(price?:0)
        //view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    @JvmStatic
    @BindingAdapter("txtPrice")
    fun bindPrice(view: AppCompatTextView, price: Int) {
        val localeUS = Locale.US
        val currencyUS = NumberFormat.getCurrencyInstance(localeUS)
        view.text = currencyUS.format(price?:0)
        //view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    @JvmStatic
    @BindingAdapter("txtSubPrice")
    fun bindSubPrice(view: AppCompatTextView, price: Double) {
        val localeUS = Locale.US
        val currencyUS = NumberFormat.getCurrencyInstance(localeUS)
        view.text = currencyUS.format(price?:0)
        //view.paintFlags = view.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }

    @JvmStatic
    @BindingAdapter("price", "promo", "promoType", requireAll = false)
    fun setTextPrice(
        textView: AppCompatTextView,
        price: Double?,
        promo: Double?,
        promoType: PromotionType?
    ) {
        if (price == null) {
            textView.text = textView.context.getString(R.string.no_price)
        } else {
            textView.text =
                when (promoType) {
                    PromotionType.ABSOLUTE -> textView.context.getString(
                        R.string.price_format,
                        PriceFormat.currencyFormat(price - (promo ?: 0.0))
                    )
                    PromotionType.PERCENT -> textView.context.getString(
                        R.string.price_format,
                        PriceFormat.currencyFormat(price * (1 - (promo ?: 0.0)))
                    )
                    else -> textView.context.getString(
                        R.string.price_format,
                        PriceFormat.currencyFormat(price)
                    )
                }
        }
    }


    @JvmStatic
    @BindingAdapter("android:layout_marginStart")
    fun setMarginStart(view: AppCompatSpinner, space: Float) {
        val layoutParam = view.layoutParams as? ViewGroup.MarginLayoutParams
        layoutParam?.marginStart = space.toInt()
    }

    @JvmStatic
    @BindingAdapter("capText", requireAll = false)
    fun setTextCap(textView: AppCompatTextView, text: String) {
        textView.text = text.toLowerCase().split(" ").joinToString(" ") { it.capitalize() }
    }

    @JvmStatic
    @BindingAdapter("setTextHtml")
    fun setTextHtml(view: TextView, text: String?) {
        text ?: return

        if (text.isNullOrEmpty())
            return

        view.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }
    @JvmStatic
    @BindingAdapter("setPaintFlagsStrike")
    fun setPaintFlagsStrike(view: TextView, boolean: Boolean) {
        if (boolean)
            view.paintFlags = (Paint.STRIKE_THRU_TEXT_FLAG)
    }

    @JvmStatic
    @BindingAdapter("hasChangedPrice", "hasChangeQuantity")
    fun setTextErrorInCart(view: TextView, hasChangedPrice: Boolean?, hasChangeQuantity: Boolean?) {
        val stringRes = when {
            hasChangedPrice == true && hasChangeQuantity == true -> R.string.error_price_quantity_has_been_changed
            hasChangedPrice == true -> R.string.error_price_has_been_changed
            hasChangeQuantity == true -> R.string.error_quantity_has_been_changed
            else -> -1
        }
        if (stringRes == -1)
            return

        view.text = HtmlCompat.fromHtml(
            view.context.getString(stringRes),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }

    @JvmStatic
    @BindingAdapter("setColorProduct")
    fun setColorProduct(view: View, color: Color?) {
        try {
            view.backgroundTintList =
                ColorStateList.valueOf(android.graphics.Color.parseColor(color?.colorHex))
        } catch (e: Exception) {
            view.backgroundTintList =
                ColorStateList.valueOf(android.graphics.Color.parseColor("#000000"))
        }
    }

    @JvmStatic
    @BindingAdapter("setColorProduct")
    fun setColorProduct(view: View, color: String?) {
        try {
            view.backgroundTintList =
                ColorStateList.valueOf(android.graphics.Color.parseColor(color))
        } catch (e: Exception) {
            view.backgroundTintList =
                ColorStateList.valueOf(android.graphics.Color.parseColor("#000000"))
        }
    }

    @JvmStatic
    @BindingAdapter("isVisible")
    fun isVisible(view: View, isVisible: Boolean?) {
        view.isVisible = isVisible ?: false
    }

    @JvmStatic
    @BindingAdapter("isVisibleClear")
    fun isVisibleClear(view: View, imageId: AppCompatImageView) {
        imageId.drawable!= ContextCompat.getDrawable(view.context, R.drawable.ic_place_holder)
    }
}