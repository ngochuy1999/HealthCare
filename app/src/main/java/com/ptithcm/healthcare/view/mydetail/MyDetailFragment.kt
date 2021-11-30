package com.ptithcm.healthcare.view.mydetail

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.RadioGroup
import androidx.lifecycle.Observer
import com.google.android.material.textfield.TextInputEditText
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.param.EditAccountParam
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.ERROR_CODE_404
import com.ptithcm.healthcare.databinding.FragmentMyDetailBinding
import com.ptithcm.healthcare.ext.initToolBar
import com.ptithcm.healthcare.ext.isShowErrorNetwork
import com.ptithcm.healthcare.ext.setupToolbar
import com.ptithcm.healthcare.util.*
import com.ptithcm.healthcare.util.DateUtils.DEFAULT_DATE_FORMAT
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_my_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class MyDetailFragment : BaseFragment<FragmentMyDetailBinding>() {
    private val userViewModel: UserViewModel by viewModel()
    private var messageQueue: ShowMessageHandler? = null
    private var gender : Int? = 0
    override val layoutId: Int
        get() = R.layout.fragment_my_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.btnNav?.visibility = View.GONE
        observeViewModel()
        setupToolbar()

    }

    private fun observeViewModel() {
        userViewModel.updateDetailLiveData.observe(this, Observer {
            changeStatusButton(false)
            if (it != null && it.status == true) {
                it.data?.let { it1 -> CoreApplication.instance.saveUser(it1) }
                messageQueue?.runMessageHandler(getString(R.string.update_detail_success))
            } else {
                changeStatusButton(false)
                messageQueue?.runMessageErrorHandler(it.message?:"")
            }
        })
        userViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                changeStatusButton(false)
                messageQueue?.runMessageErrorHandler(it.first)
            }
        })
    }

    override fun bindEvent() {
        super.bindEvent()
        messageQueue = ShowMessageHandler(requireActivity())
        viewBinding.profile = CoreApplication.instance.profile
        viewBinding.fragment = this
    }

    override fun onDestroyView() {
        messageQueue?.removeHandler()
        super.onDestroyView()
    }

    @SuppressLint("SimpleDateFormat")
    fun onClick(v: View?) {
        when (v?.id) {
            R.id.edt_birthday ->{
                viewBinding.edtBirthday.setText(SimpleDateFormat(DEFAULT_DATE_FORMAT).format(System.currentTimeMillis()))

                val cal = Calendar.getInstance()

                val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    cal.set(Calendar.YEAR, year)
                    cal.set(Calendar.MONTH, monthOfYear)
                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale("vi"))
                    viewBinding.edtBirthday.setText(sdf.format(cal.time))
                }
                val dialog = DatePickerDialog(context!!, dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH))
                dialog.datePicker.maxDate = getCurrentDateInMills()
                dialog.show()
            }
            R.id.btn_update -> {
                changeStatusButton(true)
                if (checkValidUpdate()) {
                    if (viewBinding.radioMale.isChecked) gender = 0 else gender = 1
                    val account = CoreApplication.instance.account
                    val param = EditAccountParam(
                        userid = account?.accountId,
                        name = viewBinding.edtName.text.toString(),
                        phone = viewBinding.edtPhone.text.toString(),
                        identityCard = viewBinding.edtCMND.text.toString(),
                        gender = null,
                        birthday = viewBinding.edtBirthday.text.toString(),
                        address = viewBinding.edtAddress.text.toString()
                    )
                    userViewModel.updateProfile(param)
                } else {
                    changeStatusButton(false)
                }
            }
        }
    }

    private fun checkValidUpdate(): Boolean {
        return when {
            !checkPhonevalid(viewBinding.edtPhone.text.toString())-> {
                messageQueue?.runMessageErrorHandler(getString(R.string.invalid_phone))
                false
            }
            !checkEmpty(viewBinding.edtName) -> {
                messageQueue?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.name)
                    )
                )
                false
            }
            !checkEmpty(viewBinding.edtPhone) -> {
                messageQueue?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.phone)
                    )
                )
                false
            }
            !checkEmpty(viewBinding.edtCMND) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.identity_card)
                    )
                )
                false
            }

            !checkEmptyRadio(viewBinding.edtGender) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.gender)
                    )
                )
                false
            }

            !checkEmpty(viewBinding.edtBirthday) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.birthday)
                    )
                )
                false
            }
            !checkEmpty(viewBinding.edtAddress) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.address)
                    )
                )
                false
            }
            !checkIdentityValid(viewBinding.edtCMND.text.toString()) -> {
                messageHandler?.runMessageErrorHandler(getString(R.string.invalid_identity))
                false
            }
            else -> true
        }
    }

    private fun checkEmpty(view: TextInputEditText): Boolean {
        if (view.text.toString().isBlank()) {
            return false
        }
        return true
    }

    private fun checkEmptyRadio(view: RadioGroup): Boolean {
        if (view.checkedRadioButtonId == -1) {
            return false
        }
        return true
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            initToolBar(
                viewBinding.layoutToolbar.toolbar, true,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            viewBinding.layoutToolbar.ivBack.setImageResource(R.drawable.ic_back)
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.my_details))
        }
    }

    private fun changeStatusButton(isLoading: Boolean) {
        viewBinding.btnUpdate.isLoading = isLoading

    }

}