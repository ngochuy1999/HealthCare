package com.ptithcm.healthcare.view.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.bumptech.glide.Glide
import com.ptithcm.core.BuildConfig
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.core.model.ShopInfo
import com.ptithcm.core.param.UpdateDetailParam
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.*
import com.ptithcm.healthcare.databinding.FragmentProfileBinding
import com.ptithcm.healthcare.databinding.LayoutBottomSheetBinding
import com.ptithcm.healthcare.databinding.LayoutPopUpBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.util.FileUtil
import com.ptithcm.healthcare.util.PopUp
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import com.ptithcm.healthcare.viewmodel.AuthenticateViewModel
import com.ptithcm.healthcare.viewmodel.UserViewModel
import org.jetbrains.anko.support.v4.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pub.devrel.easypermissions.AfterPermissionGranted
import java.io.File

class ProfileFragment : BaseFragment<FragmentProfileBinding>(), View.OnClickListener {

    override val layoutId: Int = R.layout.fragment_profile

    private val authViewModel: AuthenticateViewModel by viewModel()
    private val userViewModel: UserViewModel by viewModel()
    private var captureFile: File? = null
    private val transferUtility: TransferUtility by inject()
    private val s3Client: AmazonS3Client by inject()
    private var photoStoragePath: String? = null
    private var photoType: PhotoType? = null
    private var currentProfile: Profile? = null
    private var currentAccount: Account? = null
    private var shopInfo: ShopInfo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            isShowLoading(false)
        }

        val glideApp = Glide.with(context!!)
        glideApp.load(currentProfile?.account?.cover)
            .placeholder(R.color.white)
            .centerCrop()
            .error(R.drawable.bg_login)
            .into(viewBinding.profileCustomer.cover)

        glideApp.load(currentProfile?.account?.photo)
            .placeholder(R.color.white)
            .error(R.drawable.bg_login)
            .into(viewBinding.profileCustomer.avatar)

        setupToolbar()
        setShopInfo()

    }

    private fun setShopInfo() {
        shopInfo = CoreApplication.instance.shopInfo
        viewBinding.profileCustomer.tvAppVer.text = getString(R.string.appVer, shopInfo?.versionApp)
        viewBinding.profileCustomer.btnPhoneNumber.text = getString(R.string.phoneShop, shopInfo?.phoneNumber)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (CoreApplication.instance.profile != null) {
            userViewModel.getProfile()
        }
        observeViewModel()
    }

    override fun bindEvent() {
        currentAccount = CoreApplication.instance.account
        viewBinding.fragment = this
        viewBinding.isLogin = currentAccount != null
        viewBinding.account = currentAccount
    }

    private fun observeViewModel() {
        authViewModel.logOutLiveData.observe(this, {
            if (it != null) {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
                CoreApplication.instance.clearAccount()
                viewBinding.isLogin = false
                setupToolbar()
                (activity as? MainActivity)?.updateUIBottomNav()
            }

        })
//        userViewModel.getProfileLiveData.observe(this, {
//            CoreApplication.instance.profile?.account = it
//            currentProfile = CoreApplication.instance.profile
//            userViewModel.getProfileLiveData.removeObservers(this)
//        })
        userViewModel.updateDetailLiveData.observe(this, Observer {
            CoreApplication.instance.saveUser(currentProfile ?: return@Observer)
        })
        userViewModel.error.observe(this, {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                messageHandler?.runMessageErrorHandler(it.first)
            }
        })
        authViewModel.error.observe(activity!!, {
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                Toast.makeText(requireContext(), it.first, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.login_btn -> {
                navController.navigate(R.id.loginFragment)
            }
            R.id.register_btn -> {
                navController.navigate(R.id.registerFragment)
            }
            R.id.log_out_btn -> {
                (requireActivity() as? BaseActivity<*>)?.showPopup(
                    PopUp(
                        R.layout.layout_pop_up,
                        messageQueue = this::popEvent
                    )
                )
            }
            R.id.details_btn -> {
                navController.navigate(R.id.myDetailFragment)
            }
            R.id.change_pass_btn -> {
                navController.navigate(R.id.changePasswordFragment)
            }
            R.id.biometric_btn -> {
                navController.navigate(R.id.biometricFragment)
            }
            R.id.customer_service_btn -> {
                navController.navigate(
                    R.id.documentFragment,
                    bundleOf(KEY_ARGUMENT to resources.getString(R.string.customer_service))
                )
            }
            R.id.about_us_btn -> {
                navController.navigate(
                    R.id.documentFragment,
                    bundleOf(KEY_ARGUMENT to resources.getString(R.string.about_us))
                )
            }

            R.id.btnOk, R.id.tvCancel -> {
                (requireActivity() as? BaseActivity<*>)?.closePopup()
            }
            R.id.btnCancel -> {
                authViewModel.logOut()
                CoreApplication.instance.clearAccount()
                CoreApplication.instance.clearUser()
                requireActivity().finish()
                startActivity<AuthenticationActivity>()
            }
            R.id.payment_method_btn -> {
                navController.navigate(R.id.nav_payment_methods)
            }
            R.id.invoice -> {
                val bundle = Bundle()
                bundle.putInt("invoiceId", 0)
                navController.navigate(R.id.nav_invoices, bundle)
            }

            R.id.invoiceShipped -> {
                val bundle = Bundle()
                bundle.putInt("invoiceId", 3)
                navController.navigate(R.id.nav_invoices, bundle)
            }
            R.id.invoiceCancel -> {
                val bundle = Bundle()
                bundle.putInt("invoiceId", 4)
                navController.navigate(R.id.nav_invoices, bundle)
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
            }  R.id.btnPhoneNumber -> {
                callShop()
            }
        }
    }

    private fun callShop() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CALL_PHONE)
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE), targetRequestCode
                )

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + shopInfo?.phoneNumber))
            startActivity(intent)
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.layoutToolbar.toolbar.visible()
            initToolBar(
                viewBinding.layoutToolbar.toolbar,
                false,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.profile))
        }
    }

    private fun popEvent(popupBinding: ViewDataBinding?) {
        (popupBinding as? LayoutPopUpBinding)?.apply {
            title = getString(R.string.continue_logout)
            left = getString(R.string.cancel)
            right = getString(R.string.logout_me_out)
            btnCancel.setOnClickListener(this@ProfileFragment)
            btnOk.setOnClickListener(this@ProfileFragment)
        }
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
                        this.tvCancel.setOnClickListener(this@ProfileFragment)
                        this.tvOption1.setOnClickListener(this@ProfileFragment)
                        this.tvOption2.setOnClickListener(this@ProfileFragment)
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
                                    .into(viewBinding.profileCustomer.avatar)
                            }
                            PhotoType.COVER_PHOTO -> {
                                Glide.with(requireContext())
                                    .load(FileUtil().getUriFromFile(requireContext(), it))
                                    .placeholder(R.color.white)
                                    .centerCrop()
                                    .into(viewBinding.profileCustomer.cover)
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
                                    .into(viewBinding.profileCustomer.avatar)
                            }
                            PhotoType.COVER_PHOTO -> {
                                Glide.with(requireContext())
                                    .load(FileUtil().getUriFromFile(requireContext(), it))
                                    .placeholder(R.color.white)
                                    .centerCrop()
                                    .into(viewBinding.profileCustomer.cover)
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
                            currentProfile?.account?.photo = "$url"
                        }
                        PhotoType.COVER_PHOTO -> {
                            currentProfile?.account?.cover = "$url"
                        }
                    }
                    updateProfile()
                }
            }

            override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {}

            override fun onError(id: Int, ex: Exception) {
                ex.printStackTrace()
            }
        })
    }

    private fun updateProfile() {
//        UpdateDetailParam(
//            first_name = currentProfile?.account?.first_name ?: "",
//            last_name = currentProfile?.account?.last_name ?: "",
//            email = currentProfile?.account?.email ?: "",
//            cover = currentProfile?.account?.cover ?: "",
//            photo = currentProfile?.account?.photo ?: ""
//        )
//        userViewModel.updateProfile(param)
    }
}