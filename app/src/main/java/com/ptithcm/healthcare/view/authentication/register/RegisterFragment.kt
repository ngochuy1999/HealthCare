package com.ptithcm.healthcare.view.authentication.register

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.messaging.FirebaseMessaging
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.param.AccountParam
import com.ptithcm.core.util.TOKEN_FCM
import com.ptithcm.core.util.getStringPref
import com.ptithcm.core.util.setStringPref
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.ERROR_CODE_404
import com.ptithcm.healthcare.constant.FROM_SHOPPING_BAG
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentRegisterBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.util.checkPhonevalid
import com.ptithcm.healthcare.util.checkValidEmail
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import com.ptithcm.healthcare.viewmodel.AuthenticateViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_register
    private var fromLogin = false
    private var fromShoppingBag = false
    private val authViewModel: AuthenticateViewModel by viewModel()
    private var typeface: Typeface? = null
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = this.arguments
        if (bundle != null) {
            fromLogin = bundle.getBoolean("fromLogin", false)
            fromShoppingBag = bundle.getBoolean(FROM_SHOPPING_BAG, false)
        }
        activity?.btnNav?.visibility = View.GONE
        typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        observeViewModel()
        viewBinding.tvHaveAccount.setOnClickListener {
            if (!fromLogin) {
                val bundle = Bundle()
                bundle.putBoolean("fromRegister", true)
                navController.navigate(R.id.loginFragment, bundle)

            } else {
                navController.popBackStack()
            }
        }

        viewBinding.btnTermsCons.makeLinks(
            Pair("Terms and Conditions", View.OnClickListener {
                navController.navigate(
                    R.id.documentFragment,
                    bundleOf(KEY_ARGUMENT to resources.getString(R.string.terms_and_conditions))
                )
            })
        )

    }


    private fun observeViewModel() {
        authViewModel.signUpLiveData.observe(this, Observer {
            if (it != null) {
                if (it.status == true && it.data != null) {
                    it.data?.let { it1 -> CoreApplication.instance.saveAccount(it1) }
                    messageHandler?.runMessageHandler(getString(R.string.sign_up_success))
                    viewBinding.btnRegister.isLoading = false
                    finishRegister()
                }else{
                    messageHandler?.runMessageErrorHandler(it.message?:"")
                    viewBinding.btnRegister.isLoading = false
                }
            }
        })

        authViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                messageHandler?.runMessageErrorHandler(it.first)
                viewBinding.btnRegister.isLoading = false
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bindEvent() {
        super.bindEvent()
        setBackPressEvent()
        viewBinding.fragment = this
        viewBinding.isShowPassword = false
        viewBinding.isValid = false


        viewBinding.edtUserName.onFocusChange { v, _ ->
            viewBinding.isFirstNameValid = checkEmpty(v as TextInputEditText)
        }

        viewBinding.edtPhone.onFocusChange { v, _ ->
            viewBinding.isLastNameValid = checkValidPhone(viewBinding.edtPhone.text.toString())
        }

        viewBinding.edtEmail.onFocusChange { _, _ ->
            viewBinding.isValid = checkValidEmail(viewBinding.edtEmail.text.toString())
        }

        viewBinding.edtPassword.onFocusChange { _, _ ->
            viewBinding.isPasswordValid = isValidPassword(viewBinding.edtPassword.text.toString())
        }

        viewBinding.scrollView.setOnTouchListener { _, event ->
            if (event != null && event.action == MotionEvent.ACTION_MOVE) {
                hideKeyboard()
            }
            false
        }
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_register -> {
                if (checkValidUpdate()) {
                    viewBinding.btnRegister.isLoading = true
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        task.result?.let { requireContext().setStringPref(TOKEN_FCM, it) }
                    } )
                    token = requireContext().getStringPref(TOKEN_FCM)
                    authViewModel.signUp(
                        AccountParam(
                            phone = viewBinding.edtPhone.text.toString(),
                            email = viewBinding.edtEmail.text.toString(),
                            password = viewBinding.edtPassword.text.toString(),
                            userName = viewBinding.edtUserName.text.toString(),
                            token = context?.getStringPref(TOKEN_FCM),
                            active = 1
                        )
                    )
                }
            }
            R.id.toggle_password -> {
                viewBinding.isShowPassword = !viewBinding.isShowPassword!!
                viewBinding.edtPassword.togglePassword(!viewBinding.isShowPassword!!, typeface)
            }
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? BaseActivity<*>)?.apply {
            initToolbar(
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar( getString(R.string.register),false, messageQueue ={
                navController.popBackStack()
                if (fromLogin)
                    navController.popBackStack()
            } )
            when (this){
                is AuthenticationActivity -> viewBinding.layoutToolbar.ivBack.setImageResource(R.drawable.ic_black_close)
            }
        }
    }

    private fun finishRegister() {
//        activity?.finish()
//        startActivity<MainActivity>()
        val bundle = Bundle()
        bundle.putString("email", viewBinding.edtEmail.text.toString())
        navController.navigate(R.id.loginFragment, bundle)
    }

    private fun checkValidUpdate(): Boolean {
        return when {
            !checkEmpty(viewBinding.edtPhone) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.phone)
                    )
                )
                false
            }
            !checkEmpty(viewBinding.edtUserName) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.username_text)
                    )
                )
                false
            }
            !checkEmpty(viewBinding.edtEmail) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.email)
                    )
                )
                false
            }
            !checkEmpty(viewBinding.edtPassword) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.password)
                    )
                )
                false
            }
            !checkPhonevalid(viewBinding.edtPhone.text.toString()) -> {
                messageHandler?.runMessageErrorHandler(getString(R.string.invalid_phone))
                false
            }
            !checkValidEmail(viewBinding.edtEmail.text.toString()) -> {
                messageHandler?.runMessageErrorHandler(getString(R.string.invalid_email))
                false
            }
            !checkSizePassword(viewBinding.edtPassword.text.toString()) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.password_size,
                        "Password"
                    )
                )
                false
            }
            !checkUpperCaseLetter(viewBinding.edtPassword.text.toString()) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.password_upper_case,
                        "Password"
                    )
                )
                false
            }
            !checkDigit(viewBinding.edtPassword.text.toString()) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.password_digit,
                        "Password"
                    )
                )
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

    private fun checkSizePassword(password: String?) = password?.length ?: 0 >= 8

    private fun checkUpperCaseLetter(password: String?) =
        Regex("^(?=.*[A-Z])").find(password ?: "") != null

    private fun checkDigit(password: String?) = Regex("^(?=.*\\d)").find(password ?: "") != null

    private fun isValidPassword(password: String?): Boolean {
        password?.let {
            val passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    private fun checkValidPhone(phone: String): Boolean {
        return android.util.Patterns.PHONE.matcher(phone).matches()
    }


    private fun setBackPressEvent() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!fromLogin) activity?.btnNav?.visibility = View.VISIBLE
            }
            false
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(
            Activity.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            requireActivity().currentFocus?.windowToken, 0
        )
    }
}