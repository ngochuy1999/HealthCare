package com.ptithcm.healthcare.view.medicalrecord

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.MedicalRecord
import com.ptithcm.core.util.ObjectHandler
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentMedicalRecordDetailBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.CheckoutViewModel
import com.ptithcm.healthcare.viewmodel.ListenerViewModel
import com.ptithcm.healthcare.viewmodel.MedicalBillViewModel
import com.ptithcm.healthcare.viewmodel.UserViewModel
import com.stripe.android.model.Card
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MedicalRecordDetailFramgment : BaseFragment<FragmentMedicalRecordDetailBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_medical_record_detail

    private val userViewModel: UserViewModel by viewModel()

    private val listenerViewModel: ListenerViewModel by sharedViewModel()

    private var medicalRecord: MedicalRecord? = null
    private var pid: Int? = null
    private var isCallApi = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pid = CoreApplication.instance.account?.accountId
        (arguments?.getParcelable(KEY_ARGUMENT) as? MedicalRecord)?.let {
            medicalRecord = it
        }
        activity?.btnNav?.visibility = View.GONE
        (activity as? BaseActivity<*>)?.isShowLoading(false)
        setupToolbar()
        userViewModel.getTreatmentRegiment(medicalRecord?.recordId)
        //medicalRecord?.billId?.let { basketViewModel.getTestForm(it) }
    }


    override fun bindEvent() {
        viewBinding.fragment = this
        //viewBinding.rvTestForm.adapter = testFormAdapter

        bindProduct()
    }


    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
    }



    private fun bindProduct() {
        viewBinding.medicalInfo.medicalBill = medicalRecord?.medicalBill

        medicalRecord?.let {
            viewBinding.doctorInfo.doctor = it.medicalBill?.doctor
            val glideApp = Glide.with(requireContext())
            glideApp.load(it.medicalBill?.doctor?.imageUrl)
                .centerCrop()
                .error(R.drawable.doctor)
                .into(viewBinding.doctorInfo.avatar)
        }
        viewBinding.patientInfo.profile = medicalRecord?.medicalBill?.patient
        viewBinding.tvTitleName.text = medicalRecord?.diagnostic


    }

    override fun bindViewModelOnce() {
        userViewModel.treatmentLiveData.observe(this, Observer {
            viewBinding.rvPrescription.item = it
            viewBinding.reExamDate = it.reExaminationDate
        })
//        basketViewModel.testFormResult.observe(this, Observer {
//            setTaxCheckout(it)
//            testFormAdapter.addToList(it)
//            if(it.isNotEmpty()) {
//                when (it[0].isPay) {
//                    0 -> {
//                        viewBinding.btnCheckOut.isVisible = true
//                        viewBinding.btnCheckOut.isLoading = false
//                        viewBinding.sttDone.isGone= true
//                    }
//                    1 -> {
//                        viewBinding.btnCheckOut.isGone = true
//                        viewBinding.sttDone.isVisible = true
//                    }
//                }
//            }else{
//                viewBinding.btnCheckOut.isGone = true
//                viewBinding.sttDone.isGone = true
//            }
//        })
//
//        checkoutViewModel.resultRequestCheckout.observe(this, Observer {
//            if(it.status == true){
//                viewBinding.btnCheckOut.isGone = true
//                viewBinding.sttDone.isVisible = true
//                medicalBill?.billId?.let { basketViewModel.getTestForm(it) }
//                messageHandler?.runMessageHandler(it.message ?: "")
//            } else{
//                messageHandler?.runMessageErrorHandler(it.message ?: "")
//                viewBinding.btnCheckOut.isLoading = false
//            }
//        })

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
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.medical_record_detail),isBackPress = true,)
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }

}