package com.ptithcm.healthcare.view.qrcode

import android.Manifest
import android.os.Bundle
import com.ptithcm.healthcare.R
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.param.MedicalBillParam
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentQrcodeBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.MedicalBillViewModel
import kotlinx.android.synthetic.main.activity_main.*
import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.jetbrains.anko.support.v4.toast
import org.koin.androidx.viewmodel.ext.android.viewModel


class QRCodeFragment : BaseFragment<FragmentQrcodeBinding>(), ZBarScannerView.ResultHandler {

    override val layoutId: Int
        get() = R.layout.fragment_qrcode

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 102
        fun newInstance(): QRCodeFragment {
            return QRCodeFragment()
        }
    }

    private  val viewModel : MedicalBillViewModel by viewModel()

    lateinit var scannerView: ZBarScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.btnNav?.gone()
        Handler().postDelayed({
            checkForPermission()
        }, 2000)
    }

    private fun checkForPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else if (isPermanentlyDenied()) {
                showGoToAppSettingsDialog()
            } else
                requestPermission()
        }
    }

    private fun isPermanentlyDenied(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA).not()
        } else {
            return false
        }
    }


    private fun showGoToAppSettingsDialog() {
        AlertDialog.Builder(requireContext(), R.style.CustomAlertDialog)
            .setTitle(getString(R.string.grant_permissions))
            .setMessage(getString(R.string.we_need_permission))
            .setPositiveButton(getString(R.string.grant)) { _, _ ->
                goToAppSettings()
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
                run {
                    this.onStop()
                }
            }.show()
    }

    private fun goToAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initViews()

    }

    override fun bindEvent() {
        super.bindEvent()
        viewBinding.fragment = this
        viewBinding.flashToggle.setOnClickListener {
            if (viewBinding.flashToggle.isSelected) {
                offFlashLight()
            } else {
                onFlashLight()
            }
        }

    }

    private fun initViews() {
        initializeQRCamera()
    }

    private fun initializeQRCamera() {
        scannerView = ZBarScannerView(context)
        scannerView.setResultHandler(this)
        scannerView.setBackgroundColor(ContextCompat.getColor(context!!, R.color.colorTranslucent))
        scannerView.setBorderColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        scannerView.setLaserColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        scannerView.setBorderStrokeWidth(10)
        scannerView.setSquareViewFinder(true)
        scannerView.setupScanner()
        scannerView.setAutoFocus(true)
        startQRCamera()
        viewBinding.containerScanner.addView(scannerView)
    }



    override fun handleResult(rawResult: Result?) {
        onQrResult(rawResult?.contents)
    }

    private fun onQrResult(contents: String?) {
        if (contents.isNullOrEmpty()){
            messageHandler?.runMessageErrorHandler(getString(R.string.not_add_bill))
            toast("Empty QR Result")
        }
        else {
            CoreApplication.instance.account?.accountId?.let {
                MedicalBillParam(contents.toInt(),
                    it
                )
            }?.let { viewModel.addMedicalBill(it) }
        }
        //scannerView.resumeCameraPreview(this)
    }



    private fun startQRCamera() {
        scannerView.startCamera()
    }

    private fun resetPreview() {
        scannerView.stopCamera()
        scannerView.startCamera()
        scannerView.stopCameraPreview()
        scannerView.resumeCameraPreview(this)
    }


    private fun onFlashLight() {
        viewBinding.flashToggle.isSelected = true
        scannerView.flash = true
    }

    private fun offFlashLight() {
        viewBinding.flashToggle.isSelected = false
        scannerView.flash = false
    }

    override fun onResume() {
        super.onResume()
        scannerView.setResultHandler(this)
        scannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        scannerView.stopCamera()
    }

    override fun bindViewModel() {
        super.bindViewModel()
        viewModel.networkState.observe(this, Observer {
            viewBinding.pbLoadQr.visible()
        })

        viewModel.addBillResult.observe(this, Observer {
            viewBinding.pbLoadQr.gone()
            if(it.status ==true) {
                messageHandler?.runMessageHandler(it.message ?: getString(R.string.add_bill))
                navController.popBackStack()
                activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                (activity as? MainActivity)?.apply {
                    viewBinding.btnNav.selectedItemId = R.id.nav_calendar
                }
            }else messageHandler?.runMessageErrorHandler(it.message ?: getString(R.string.not_add_bill))
        })

        viewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            viewBinding.pbLoadQr.gone()
        })
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
            setupToolbar(viewBinding.layoutToolbar.toolbar, "QR CODE")
        }
    }

}