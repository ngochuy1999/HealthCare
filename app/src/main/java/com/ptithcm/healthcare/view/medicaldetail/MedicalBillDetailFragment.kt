package com.ptithcm.healthcare.view.medicaldetail

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.core.model.TestForm
import com.ptithcm.core.model.TestFormDetail
import com.ptithcm.core.param.RequestCheckoutParam
import com.ptithcm.core.util.ObjectHandler
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.constant.KEY_ARGUMENT_BOOLEAN
import com.ptithcm.healthcare.databinding.FragmentMedicalBillDetailBinding
import com.ptithcm.healthcare.databinding.LayoutPopUpBinding
import com.ptithcm.healthcare.databinding.LayoutPopUpCancelBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.util.PopUp
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import com.ptithcm.healthcare.view.medicaldetail.adapter.TestFormAdapter
import com.ptithcm.healthcare.viewmodel.CheckoutViewModel
import com.ptithcm.healthcare.viewmodel.ListenerViewModel
import com.ptithcm.healthcare.viewmodel.MedicalBillViewModel
import com.stripe.android.model.Card
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MedicalBillDetailFragment : BaseFragment<FragmentMedicalBillDetailBinding>() , View.OnClickListener {

    override val layoutId: Int
        get() = R.layout.fragment_medical_bill_detail

    private val testFormAdapter: TestFormAdapter = TestFormAdapter()

    private val checkoutViewModel: CheckoutViewModel by viewModel()
    private val basketViewModel: MedicalBillViewModel by viewModel()

    private val listenerViewModel: ListenerViewModel by sharedViewModel()

    private var medicalBill: MedicalBill? = null
    private var pid: Int? = null
    private var isCallApi = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pid = CoreApplication.instance.account?.accountId
        (arguments?.getParcelable(KEY_ARGUMENT) as? MedicalBill)?.let {
            medicalBill = it
        }
            activity?.btnNav?.visibility = View.GONE
        (activity as? BaseActivity<*>)?.isShowLoading(false)
        setupToolbar()
        medicalBill?.billId?.let { basketViewModel.getTestForm(it) }
    }


    override fun bindEvent() {
        viewBinding.fragment = this
        viewBinding.rvTestForm.adapter = testFormAdapter
        if(medicalBill?.medicalBillStatus?.statusId == 1){
            viewBinding.btnCancelBill.isVisible = true
        }else{
            viewBinding.btnCancelBill.isGone = true
        }
        bindProduct()
    }


    override fun onResume() {
        super.onResume()
        medicalBill?.billId?.let { basketViewModel.getTestForm(it) }
        activity?.btnNav?.visibility = View.GONE
    }


    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.ivBack -> {
                navController.popBackStack()
            }

            R.id.btnCancel-> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
            }
            R.id.btnOk -> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
                val cart = CoreApplication.instance.cart
                viewBinding.btnCheckOut.isLoading = true
                val checkoutParam = medicalBill?.billId?.let {
                    cart?.creditCard?.tokenCard?.let { it1 ->
                        RequestCheckoutParam(
                            billId = it,
                            amount = total,
                            currency = RequestCheckoutParam.Currency.USD,
                            description = "Thanh toán hóa đơn xét nghiệm điện tử",
                            stripeToken = it1
                        )
                    }
                }

                if (checkoutParam != null) {
                    checkoutViewModel.requestCheckout(checkoutParam)
                }
            }

            R.id.btnCancelCancel-> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
            }
            R.id.btnOkCancel -> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
//                if (checkoutParam != null) {
//                    checkoutViewModel.requestCheckout(checkoutParam)
//                }
                checkoutViewModel.cancelBill(medicalBill?.billId)
                navController.popBackStack()
            }

            R.id.btnCheckOut -> {
                if (ObjectHandler.cart?.creditCard == null || ObjectHandler.cart?.creditCard?.tokenCard.isNullOrEmpty())
                    messageHandler?.runMessageErrorHandler(getString(R.string.select_payment_method_credit_card))
                else {
                    (requireActivity() as? BaseActivity<*>)?.showPopup(
                        PopUp(
                            R.layout.layout_pop_up,
                            messageQueue = this::popEvent
                        )
                    )
                }
            }

            R.id.btnCancelBill -> {
                (requireActivity() as? BaseActivity<*>)?.showPopup(
                    PopUp(
                        R.layout.layout_pop_up_cancel,
                        messageQueue = this::popEventCancel
                    )
                )
            }


            R.id.includePayment -> {

                navController.navigateAnimation(
                    R.id.creditCardDetailFragment,
                    bundle = bundleOf(
                        KEY_ARGUMENT_BOOLEAN to true,
                        "card" to ObjectHandler.cart?.creditCard
                    )
                )
            }
        }
    }

    private fun bindProduct() {
        viewBinding.medicalInfo.medicalBill = medicalBill
        viewBinding.doctorInfo.doctor = medicalBill?.doctor
        viewBinding.includePrice.examinationFee = (medicalBill?.examinationFee?.toLong() ?: Long) as Long?
        //setTaxCheckout(listTestForm)

        medicalBill?.doctor?.let {
            viewBinding.doctorInfo.doctor = it
            val glideApp = Glide.with(requireContext())
            glideApp.load(it.imageUrl)
                .centerCrop()
                .error(R.drawable.doctor)
                .into(viewBinding.doctorInfo.avatar)
        }
        viewBinding.patientInfo.profile = medicalBill?.patient


        viewBinding.includePayment.data = ObjectHandler.cart?.creditCard
        viewBinding.includePayment.ivCreditCard.setImageResource(Card.getBrandIcon(ObjectHandler.cart?.creditCard?.brand))

        viewBinding.includePayment.root.setOnClickListener(this)

        viewBinding.swRefreshInvoiceDetail.setOnRefreshListener {
            medicalBill?.billId?.let { basketViewModel.getTestForm(it) }
        }

    }

    override fun bindViewModelOnce() {
        basketViewModel.testFormResult.observe(this, Observer {
            viewBinding.swRefreshInvoiceDetail.isRefreshing = false
            setTaxCheckout(it)
            testFormAdapter.addToList(it)
            if(it.isNotEmpty()) {
                when (it[0].isPay) {
                    0 -> {
                        viewBinding.btnCheckOut.isVisible = true
                        viewBinding.btnCheckOut.isLoading = false
                        viewBinding.sttDone.isGone= true
                    }
                    1 -> {
                        viewBinding.btnCheckOut.isGone = true
                        viewBinding.sttDone.isVisible = true
                    }
                }
            }else{
                viewBinding.btnCheckOut.isGone = true
                viewBinding.sttDone.isGone = true
            }
        })

        checkoutViewModel.resultRequestCheckout.observe(this, Observer {
            if(it.status == true){
                viewBinding.btnCheckOut.isGone = true
                viewBinding.sttDone.isVisible = true
                medicalBill?.billId?.let { basketViewModel.getTestForm(it) }
                messageHandler?.runMessageHandler(it.message ?: "")
            } else{
                messageHandler?.runMessageErrorHandler(it.message ?: "")
                viewBinding.btnCheckOut.isLoading = false
            }
        })

        listenerViewModel.changePayment.observe(this, Observer { card ->
            ObjectHandler.cart?.creditCard = card
            viewBinding.includePayment.data = card
            viewBinding.includePayment.ivCreditCard.setImageResource(Card.getBrandIcon(card?.brand))
            if (card?.tokenCard.isNullOrEmpty())
                viewBinding.includePayment.data = null
        })
    }


    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar,
                true,
                hasBackRight = false,
                hasLeft = false,
                hasCount = false,
            )
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.medical_bill_detail),isBackPress = true,)
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }

    private fun popEvent(popupBinding: ViewDataBinding?) {
        (popupBinding as? LayoutPopUpBinding)?.apply {
            title = getString(R.string.continue_payment)
            left = getString(R.string.agree)
            right = getString(R.string.cancel_pay)
            btnCancel.setOnClickListener(this@MedicalBillDetailFragment)
            btnOk.setOnClickListener(this@MedicalBillDetailFragment)
        }
    }

    private fun popEventCancel(popupBinding: ViewDataBinding?) {
        (popupBinding as? LayoutPopUpCancelBinding)?.apply {
            title = getString(R.string.continue_cancel)
            left = getString(R.string.agree)
            right = getString(R.string.cancel_pay)
            btnCancelCancel.setOnClickListener(this@MedicalBillDetailFragment)
            btnOkCancel.setOnClickListener(this@MedicalBillDetailFragment)
        }
    }

    var total = 0
    private fun setTaxCheckout(listTestForm: ArrayList<TestForm>) {
        var sum = 0.0
        for(testForm : TestForm in listTestForm) {
            for (testFormDetail: TestFormDetail in testForm.testFormDetail) {
                sum += testFormDetail.subclinical.price
            }

        }
        total = medicalBill?.examinationFee?.let { sum.plus(it.toDouble()).toInt() }!!
        viewBinding.includePrice.subtotal = sum
        viewBinding.includePrice.total = total
    }

}