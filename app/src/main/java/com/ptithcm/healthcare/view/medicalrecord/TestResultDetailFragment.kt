package com.ptithcm.healthcare.view.medicalrecord

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.lifecycle.Observer
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils.runOnUiThread
import com.bumptech.glide.Glide
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.TestResult
import com.ptithcm.core.model.TestResultDetail
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentTestResultDetailBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.medicalrecord.adapter.FileResultAdapter
import com.ptithcm.healthcare.view.medicalrecord.adapter.ImageResultAdapter
import com.ptithcm.healthcare.viewmodel.MedicalBillViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class TestResultDetailFragment : BaseFragment<FragmentTestResultDetailBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_test_result_detail

    private lateinit var fileAdapter: FileResultAdapter
    private lateinit var imageAdapter: ImageResultAdapter

    private val viewModel: MedicalBillViewModel by viewModel()

    private var testResult: TestResult? = null
    private var pid: Int? = null
    private var isCallApi = false
    private var file : File? = null

    private var downloadManager: DownloadManager? = null
    private var downloadImageId: Long = -1
    private var trackingStatusThread: Thread? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pid = CoreApplication.instance.account?.accountId
        (arguments?.getParcelable(KEY_ARGUMENT) as? TestResult)?.let {
            testResult = it
        }
        activity?.btnNav?.visibility = View.GONE
        (activity as? BaseActivity<*>)?.isShowLoading(false)
        viewModel.getImageResult(testResult?.resultId)
        setupToolbar()
    }


    override fun bindEvent() {
        viewBinding.fragment = this
        bindResult()

        fileAdapter =
            FileResultAdapter(arrayListOf(), this::listenerFile)
        viewBinding.rvFile.adapter = fileAdapter

        imageAdapter = ImageResultAdapter()
        viewBinding.rvImg.adapter = imageAdapter
    }


    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
    }


    @SuppressLint("SetJavaScriptEnabled")
    private fun bindResult() {
        viewBinding.medicalInfo.medicalBill = testResult?.testForm?.medicalBill

        testResult?.let {
            viewBinding.doctorInfo.doctor = it.doctor
            val glideApp = Glide.with(requireContext())
            glideApp.load(it.doctor?.imageUrl)
                .centerCrop()
                .error(R.drawable.doctor)
                .into(viewBinding.doctorInfo.avatar)
        }
        viewBinding.patientInfo.profile = testResult?.testForm?.medicalBill?.patient

//        testResult?.let {
//            val glideApp = Glide.with(requireContext())
//            glideApp.load(it.imageUrl)
//                .centerCrop()
//                .error(R.drawable.no_avatar_placeholder)
//                .into(viewBinding.ivResult)
//        }
//
//        viewBinding.webView.getSettings().setJavaScriptEnabled(true)
//        viewBinding.webView.getSettings().setAllowFileAccess(true)
//        viewBinding.webView.loadUrl(testResult?.fileUrl);
    }

    override fun bindViewModel() {
        viewModel.networkState.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowLoading(it)
        })

        viewModel.testResultDetail.observe(this, Observer {
            imageAdapter.addToList(it)
            fileAdapter.setListFile(it)
        })


        viewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
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
            setupToolbar(viewBinding.layoutToolbar.toolbar, getString(R.string.test_result_detail),isBackPress = true,)
            viewBinding.layoutToolbar.ivRight.setImageResource(R.drawable.ic_baseline_qr_code_scanner_24)
            viewBinding.layoutToolbar.ivRight.setOnClickListener {
                navController.navigateAnimation(R.id.nav_qrcode, isBotToTop = true)
            }
        }

    }

    private fun listenerFile(file: TestResultDetail?) {
        startDownload(Uri.parse(file?.fileUrl))
    }

    @Volatile
    private var isDownloadCompleted = false


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        }
    }


    private fun startDownload(uri: Uri) {
        isDownloadCompleted = false

        val request = DownloadManager.Request(uri)

        // Setting title of request
        request.setTitle("Android Download Manager")

        // Setting description of request
        request.setDescription("Android download using DownloadManager")

        // Set the local destination for the downloaded file to a path
        // within the application's external files directory
        request.setDestinationInExternalFilesDir(
            requireContext(),
            Environment.DIRECTORY_DOWNLOADS,
            "file"
        )
        // Enqueue download and save into referenceId
        downloadImageId = downloadManager?.enqueue(request) ?: -1
        startDownloadStatusTracking(downloadImageId)

    }

    private fun startDownloadStatusTracking(downloadImageId: Long) {
        trackingStatusThread = Thread {
            while (!isDownloadCompleted) {
                runOnUiThread {
                }
                Thread.sleep(TRACKING_STATUS_DELAY)
            }
        }
        trackingStatusThread?.start()
    }

    private fun getStatusMessage(downloadId: Long): String {

        val query = DownloadManager.Query()
        // set the query filter to our previously Enqueued download
        query.setFilterById(downloadId)

        // Query the download manager about downloads that have been requested.
        val cursor = downloadManager?.query(query)
        if (cursor?.moveToFirst() == true) {
            return downloadStatus(cursor)
        }
        return "NO_STATUS_INFO"
    }

    private fun downloadStatus(cursor: Cursor): String {

        // column for download  status
        val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
        val status = cursor.getInt(columnIndex)
        // column for reason code if the download failed or paused
        val columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
        val reason = cursor.getInt(columnReason)

        var statusText = ""
        var reasonText = ""

        when (status) {
            DownloadManager.STATUS_FAILED -> {
                statusText = "STATUS_FAILED"
                reasonText = when (reason) {
                    DownloadManager.ERROR_CANNOT_RESUME -> "ERROR_CANNOT_RESUME"
                    DownloadManager.ERROR_DEVICE_NOT_FOUND -> "ERROR_DEVICE_NOT_FOUND"
                    DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "ERROR_FILE_ALREADY_EXISTS"
                    DownloadManager.ERROR_FILE_ERROR -> "ERROR_FILE_ERROR"
                    DownloadManager.ERROR_HTTP_DATA_ERROR -> "ERROR_HTTP_DATA_ERROR"
                    DownloadManager.ERROR_INSUFFICIENT_SPACE -> "ERROR_INSUFFICIENT_SPACE"
                    DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "ERROR_TOO_MANY_REDIRECTS"
                    DownloadManager.ERROR_UNHANDLED_HTTP_CODE -> "ERROR_UNHANDLED_HTTP_CODE"
                    DownloadManager.ERROR_UNKNOWN -> "ERROR_UNKNOWN"
                    else -> ""
                }
            }
            DownloadManager.STATUS_PAUSED -> {
                statusText = "STATUS_PAUSED"
                reasonText = when (reason) {
                    DownloadManager.PAUSED_QUEUED_FOR_WIFI -> "PAUSED_QUEUED_FOR_WIFI"
                    DownloadManager.PAUSED_UNKNOWN -> "PAUSED_UNKNOWN"
                    DownloadManager.PAUSED_WAITING_FOR_NETWORK -> "PAUSED_WAITING_FOR_NETWORK"
                    DownloadManager.PAUSED_WAITING_TO_RETRY -> "PAUSED_WAITING_TO_RETRY"
                    else -> ""
                }
            }
            DownloadManager.STATUS_PENDING -> statusText = "STATUS_PENDING"
            DownloadManager.STATUS_RUNNING -> statusText = "STATUS_RUNNING"
            DownloadManager.STATUS_SUCCESSFUL -> statusText = "STATUS_SUCCESSFUL"
        }

        return "Status: $statusText, $reasonText"
    }

    companion object {
        private const val TRACKING_STATUS_DELAY = 500L
    }

}