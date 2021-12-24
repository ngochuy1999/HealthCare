package com.ptithcm.healthcare.view.authentication.login

import android.graphics.Typeface
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.param.LogInParam
import com.ptithcm.core.util.*
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.*
import com.ptithcm.healthcare.databinding.FragmentLoginBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import com.ptithcm.healthcare.view.authentication.CreateProfileActivity
import com.ptithcm.healthcare.viewmodel.AuthenticateViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.support.v4.startActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.Executor

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_login

    private var fromRegister = false
    private var fromShoppingBag = false
    private val authViewModel: AuthenticateViewModel by viewModel()
    private var typeface: Typeface? = null
    private var fromQuestionFragment = false
    private var email: String? = null
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fromQuestionFragment = arguments?.get("fromQuestionFragment") as Boolean? ?:false
        (requireActivity() as? BaseActivity<*>)?.apply {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            transparentStatusBar(isFull = false, isBlack = true)
        }

        val bundle = this.arguments
        if (bundle != null) {
            fromRegister = bundle.getBoolean("fromRegister", false)
            fromShoppingBag = bundle.getBoolean(FROM_SHOPPING_BAG, false)
            email = bundle.getString("email",null)
        }
        activity?.btnNav?.gone()
        observeViewModel()
        typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            task.result?.let { requireContext().setStringPref(TOKEN_FCM, it) }
        } )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        viewBinding.edtEmail.setText(email)
        executor = ContextCompat.getMainExecutor(requireContext())

        setPrompt()

        if (isBiometricHardWareAvailable(requireContext())) {

            //Enable the button if the device has biometric hardware available
            viewBinding.fingerprintIv.isEnabled = true

            initBiometricPrompt(
                BIOMETRIC_AUTHENTICATION,
                BIOMETRIC_AUTHENTICATION_SUBTITLE,
                BIOMETRIC_AUTHENTICATION_DESCRIPTION,
                false
            )
        } else {
            viewBinding.fingerprintIv.isEnabled = false

            //Fallback, use device password/pin
            if (deviceHasPasswordPinLock(requireContext())) {
                viewBinding.fingerprintIv.isEnabled = true

                initBiometricPrompt(
                    PASSWORD_PIN_AUTHENTICATION,
                    PASSWORD_PIN_AUTHENTICATION_SUBTITLE,
                    PASSWORD_PIN_AUTHENTICATION_DESCRIPTION,
                    true
                )
            }
        }
    }

    private fun setPrompt() {
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    messageHandler?.runMessageErrorHandler(errString.toString())
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    messageHandler?.runMessageHandler(getString(R.string.login_success))
                    CoreApplication.instance.accountBio?.let {
                        CoreApplication.instance.saveAccount(it)
                        authViewModel.getProfile(it.accountId)
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    messageHandler?.runMessageErrorHandler(getString(R.string.wrong_fingerprint))
                }
            })
    }

    private fun initBiometricPrompt(
        title: String,
        subtitle: String,
        description: String,
        setDeviceCred: Boolean
    ) {
        if (setDeviceCred) {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setDeviceCredentialAllowed(true)
                .build()
        } else {
            promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(title)
                .setSubtitle(subtitle)
                .setDescription(description)
                .setNegativeButtonText(CANCEL)
                .build()
        }


    }


    private fun observeViewModel() {
        authViewModel.logInLiveData.observe(this, Observer {

            if (it.data == null && it.status == false) {
                messageHandler?.runMessageErrorHandler(it.message?:"")
                viewBinding.btnSignIn.isLoading = false

            }else{
                messageHandler?.runMessageHandler(it.message?:"Đăng nhập thành công")
                it.data?.let { it1 -> CoreApplication.instance.saveAccount(it1) }

                authViewModel.changeFCMToken(it.data?.accountId,token)

                it.data?.let { it1 -> authViewModel.getProfile(it1.accountId) }

            }
        })

        authViewModel.profileLiveData.observe(this, Observer {
            if(it.status == true && it.data != null ){
                CoreApplication.instance.saveUser(it.data!!)
                startActivity<MainActivity>()
                requireActivity().finish()
            }else{
                startActivity<CreateProfileActivity>()
                requireActivity().finish()
            }
        })

        authViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? BaseActivity<*>)?.isShowErrorNetwork(true)
            } else {
                viewBinding.btnSignIn.isLoading = false
                messageHandler?.runMessageErrorHandler(it.first)
            }
        })
    }

    override fun bindEvent() {
        super.bindEvent()
        setBackPressEvent()
        viewBinding.fragment = this
        viewBinding.isShowPassword = false
        viewBinding.isValid = false
        viewBinding.edtEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewBinding.isValid = viewBinding.edtEmail.text.toString().trim().isValidEmail()
            }
        }
        viewBinding.tvDontAccount.setOnClickListener {
            if (!fromRegister) {
                val bundle = Bundle()
                bundle.putBoolean("fromLogin", true)
                bundle.putBoolean(FROM_SHOPPING_BAG, fromShoppingBag)
                navController.navigate(R.id.registerFragment, bundle)
            } else {
                navController.popBackStack()
            }
        }

    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_sign_in -> {
                if (isValidInput()) {

                    token = requireContext().getStringPref(TOKEN_FCM)
                    viewBinding.btnSignIn.isLoading = true
                    authViewModel.logIn(
                        LogInParam(
                            viewBinding.edtEmail.text.toString(),
                            viewBinding.edtPassword.text.toString()
                        )
                    )
                }

            }
            R.id.toggle_password -> {
                viewBinding.isShowPassword = !viewBinding.isShowPassword!!
                viewBinding.edtPassword.togglePassword(!viewBinding.isShowPassword!!, typeface)
            }
            R.id.btn_forgot_pass -> {
                navController.navigate(
                    R.id.fragmentForgotPassword,
                    bundleOf("email" to viewBinding.edtEmail.text.toString())
                )
            }
        }
    }


    private fun setBackPressEvent() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            }
            false
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? BaseActivity<*>)?.apply {
            initToolbar(
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar( getString(R.string.login),true, messageQueue ={
                navController.popBackStack()
                if (fromRegister)
                    navController.popBackStack()
            } )
            when (this){
                is AuthenticationActivity -> viewBinding.layoutToolbar.ivBack.setImageResource(R.drawable.ic_black_close)
            }
        }
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        (requireActivity() as? MainActivity)?.apply {
//            viewBinding.layoutToolbar.toolbar.visible()
//        }
//        (requireActivity() as? StoryDetailActivity)?.apply {
//            viewBinding.layoutToolbar.toolbar.visible()
//        }
//        (requireActivity() as? AuthenticationActivity)?.apply {
//            viewBinding.layoutToolbar.toolbar.visible()
//        }
//    }

    private fun isValidInput(): Boolean {
        val email = viewBinding.edtEmail.text.toString().trim()
        val password = viewBinding.edtPassword.text.toString().trim()
        return when {
            (email.isEmpty()) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty, getString(
                            R.string.email
                        )
                    )
                )
                false
            }
            (password.isEmpty()) -> {
                messageHandler?.runMessageErrorHandler(getString(
                    R.string.error_empty, getString(
                        R.string.password
                    )
                ))
                false
            }
            !(email.isValidEmail()) -> {
                messageHandler?.runMessageErrorHandler(getString(R.string.invalid_email))
                false
            }
            else -> true
        }
    }

    fun onClickFigger(){
        if (requireContext().getBooleanPref(CHECK_FINGER)){
            biometricPrompt.authenticate(promptInfo)
        }else{
            messageHandler?.runMessageErrorHandler(getString(R.string.set_touchId))
        }

    }
}