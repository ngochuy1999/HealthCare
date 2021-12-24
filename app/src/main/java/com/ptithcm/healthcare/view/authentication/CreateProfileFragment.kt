package com.ptithcm.healthcare.view.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioGroup
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.ptithcm.core.BuildConfig
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.core.param.ProfileParam
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.*
import com.ptithcm.healthcare.databinding.FragmentCreateProfileBinding
import com.ptithcm.healthcare.databinding.LayoutBottomSheetBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.util.FileUtil
import com.ptithcm.healthcare.util.PopUp
import com.ptithcm.healthcare.util.checkIdentityValid
import com.ptithcm.healthcare.util.getCurrentDateInMills
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.profile.ProfileCropImageActivity
import com.ptithcm.healthcare.viewmodel.AuthenticateViewModel
import com.ptithcm.healthcare.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_create_profile.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateProfileFragment : BaseFragment<FragmentCreateProfileBinding>() , View.OnClickListener {

    override val layoutId: Int
        get() = R.layout.fragment_create_profile

    private var currentProfile: Profile? = null
    private var captureFile: File? = null
    private val transferUtility: TransferUtility by inject()
    private val s3Client: AmazonS3Client by inject()
    private var photoStoragePath: String? = null
    private var photoType: PhotoType? = null
    private val authViewModel: AuthenticateViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private var currentAccount: Account? = null
    private var typeface: Typeface? = null
    private var gender : Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.btnNav?.visibility = View.GONE
        typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_regular)
        observeViewModel()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentAccount = CoreApplication.instance.account
        val glideApp = Glide.with(context!!)
        glideApp.load(currentAccount?.cover)
            .placeholder(R.color.white)
            .centerCrop()
            .error(R.drawable.bg_login)
            .into(viewBinding.cover)

        glideApp.load(currentAccount?.avatar)
            .placeholder(R.color.white)
            .error(R.drawable.bg_login)
            .into(viewBinding.avatar)
        setupToolbar()

        viewBinding.edtBirthday.setOnClickListener {
            viewBinding.edtBirthday.setText(SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis()))

            val cal = Calendar.getInstance()

            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val myFormat = "dd/MM/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                viewBinding.edtBirthday.setText(sdf.format(cal.time))
            }
            val dialog = DatePickerDialog(context!!, dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.maxDate = getCurrentDateInMills()
            dialog.show()
        }

    }


    private fun observeViewModel() {

        userViewModel.updateCoverLiveData.observe(this, {
            it.data?.let { it1 -> CoreApplication.instance.saveAccount(it1) }
//            currentProfile = CoreApplication.instance.profile
//            userViewModel.getProfileLiveData.removeObservers(this)
        })
        userViewModel.updateImageLiveData.observe(this, {
            it.data?.let { it1 -> CoreApplication.instance.saveAccount(it1) }
//            currentProfile = CoreApplication.instance.profile
//            userViewModel.getProfileLiveData.removeObservers(this)
        })
        authViewModel.profileLiveData.observe(this, Observer {
            if (it != null) {
                if (it.status == true) {
                    it.data?.let { it1 -> CoreApplication.instance.saveUser(it1) }
                    messageHandler?.runMessageHandler(getString(R.string.sign_up_success))
                    viewBinding.btnUpdate.isLoading = false
                    finishCreate()
                }else{
                    messageHandler?.runMessageErrorHandler(it.message?:"")
                    viewBinding.btnUpdate.isLoading = false
                }
            }
        })

        authViewModel.error.observe(this, Observer {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                messageHandler?.runMessageErrorHandler(it.first)
                viewBinding.btnUpdate.isLoading = false
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun bindEvent() {
        super.bindEvent()
        setBackPressEvent()
        viewBinding.parentFragment = this
        viewBinding.isValid = false
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update -> {
                if (checkValidUpdate()) {
                    if (viewBinding.radioMale.isChecked) gender = 0 else gender = 1
                    viewBinding.btnUpdate.isLoading = true
                    authViewModel.createProfile(
                        ProfileParam(
                            pid = CoreApplication.instance.account?.accountId,
                            name = viewBinding.edtName.text.toString(),
                            avatar = null,
                            cover = null,
                            identityCard = viewBinding.edtCMND.text.toString(),
                            birthday = viewBinding.edtBirthday.text.toString(),
                            gender = gender,
                            address = viewBinding.edtAddress.text.toString(),
                            active = 1
                        )
                    )
                }
            }

            R.id.btnOk, R.id.tvCancel -> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
            }
            R.id.btnCancel -> {
                authViewModel.logOut()
                CoreApplication.instance.clearAccount()
                requireActivity().finish()
                startActivity<AuthenticationActivity>()
            }

            R.id.avatar -> {
                photoType = PhotoType.PROFILE_PHOTO
                showBottomSheet()
            }
            R.id.cover -> {
                photoType = PhotoType.COVER_PHOTO
                showBottomSheet()
            }
            R.id.tvOption1 -> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
                openCamera()
            }
            R.id.tvOption2 -> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
                openGallery()
            }
        }
    }



    private fun setupToolbar() {
        (requireActivity() as? CreateProfileActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar,
                false,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.create_profile))
        }
    }

    private fun finishCreate() {
        activity?.finish()
        startActivity<MainActivity>()
    }

    private fun checkValidUpdate(): Boolean {
        return when {
            !checkEmpty(viewBinding.edtName) -> {
                messageHandler?.runMessageErrorHandler(
                    getString(
                        R.string.error_empty,
                        getString(R.string.name)
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


    private fun setBackPressEvent() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener { _, keyCode, _ ->
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                if (!fromLogin) activity?.btnNav?.visibility = View.VISIBLE
//            }
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

    @AfterPermissionGranted(PERMISSION_GALLERY)
    private fun openGallery() {
        if (context?.hasReadStoragePermission() == false) {
            requestReadAndWriteStoragePermission(PERMISSION_GALLERY)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_PICK
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), GALLERY_REQUEST)
        }
    }

    @AfterPermissionGranted(PERMISSION_CAMERA)
    private fun openCamera() {
        if (context?.hasCameraPermission() == false)
            requestCameraPermission(PERMISSION_CAMERA)
        else {
            captureFile = FileUtil().createFile(context ?: return)
            val capturedImgUri = FileProvider.getUriForFile(
                context ?: return,
                getString(R.string.file_provider_authority),
                captureFile ?: return
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, capturedImgUri)
            startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    private fun showBottomSheet() {
        (requireActivity() as? BaseActivity<*>)?.showPopup(
            PopUp(
                R.layout.layout_bottom_sheet,
                isBottomSheet = true,
                isCancelable = true,
                messageQueue = { binding: ViewDataBinding? ->
                    (binding as? LayoutBottomSheetBinding)?.apply {
                        title =
                            if (photoType == PhotoType.PROFILE_PHOTO) getString(R.string.update_avatar)
                            else getString(R.string.update_cover)
                        option1 = getString(R.string.take_photo)
                        option2 = getString(R.string.choose_photo)
                        this.tvCancel.setOnClickListener(this@CreateProfileFragment)
                        this.tvOption1.setOnClickListener(this@CreateProfileFragment)
                        this.tvOption2.setOnClickListener(this@CreateProfileFragment)
                    }
                }
            )
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST -> {
                    data?.data?.let {
                        toCropImage(it)
                    }
                }
                CAMERA_REQUEST -> {
                    captureFile?.let {
                        when (photoType) {
                            PhotoType.PROFILE_PHOTO -> {
                                Glide.with(requireContext())
                                    .load(FileUtil().getUriFromFile(requireContext(), it))
                                    .placeholder(R.color.white)
                                    .into(viewBinding.avatar)
                            }
                            PhotoType.COVER_PHOTO -> {
                                Glide.with(requireContext())
                                    .load(FileUtil().getUriFromFile(requireContext(), it))
                                    .placeholder(R.color.white)
                                    .centerCrop()
                                    .into(viewBinding.cover)
                            }
                        }
                        uploadFileToS3(it)
                    }
                }
                CROP_IMAGE_REQUEST -> {
                    val file = data?.getSerializableExtra(KEY_FILE) as? File
                    file?.let {
                        when (photoType) {
                            PhotoType.PROFILE_PHOTO -> {
                                Glide.with(requireContext())
                                    .load(FileUtil().getUriFromFile(requireContext(), it))
                                    .placeholder(R.color.white)
                                    .into(viewBinding.avatar)
                            }
                            PhotoType.COVER_PHOTO -> {
                                Glide.with(requireContext())
                                    .load(FileUtil().getUriFromFile(requireContext(), it))
                                    .placeholder(R.color.white)
                                    .centerCrop()
                                    .into(viewBinding.cover)
                            }
                        }
                        uploadFileToS3(it)
                    }
                }
            }
        }
    }

    private fun toCropImage(uri: Uri) {
        val intent = Intent(activity, ProfileCropImageActivity::class.java)
        intent.putExtra(KEY_ARGUMENT_OBJECT, uri)
        startActivityForResult(intent, CROP_IMAGE_REQUEST)
    }

    private fun uploadFileToS3(file: File) {
        photoStoragePath = "$PROFILE_PHOTO_STORAGE${System.currentTimeMillis()}"
        val transferObserver = transferUtility.upload(
            BuildConfig.AWS_BUCKET,
            photoStoragePath,
            file
        )
        transferObserverListener(transferObserver)
    }

    private fun transferObserverListener(transferObserver: TransferObserver) {
        transferObserver.setTransferListener(object : TransferListener {
            override fun onStateChanged(id: Int, state: TransferState) {
                if (state == TransferState.COMPLETED) {
                    val url = s3Client.getUrl(BuildConfig.AWS_BUCKET, photoStoragePath)
                    when (photoType) {
                        PhotoType.PROFILE_PHOTO -> {
                            currentProfile?.account?.avatar = "$url"
                            updateImage("$url")

                        }
                        PhotoType.COVER_PHOTO -> {
                            currentProfile?.account?.cover = "$url"
                            updateCover("$url")
                        }
                    }
                    //updateProfile()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
            }
        })
    }

    private fun updateImage(url: String) {
        userViewModel.updateImage(url)
    }

    private fun updateCover(url: String) {
        userViewModel.updateCover(url)
    }

}