package com.ptithcm.healthcare.view.rating

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.InvoiceProductDetail
import com.ptithcm.core.model.Rating
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.databinding.FragmentCreateReviewBinding
import com.ptithcm.healthcare.databinding.LayoutPopUpBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.util.BitmapResolver
import com.ptithcm.healthcare.util.PopUp
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.viewmodel.RatingViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException


class CreateReviewFragment : BaseFragment<FragmentCreateReviewBinding>() {

    companion object {
        private val KEY_DATA_RESULT: String? = "data"
        private const val REQUEST_ID_IMAGE_CAPTURE = 999
        private const val REQUEST_ID_IMAGE_GALLERY = 888
    }

    override val layoutId: Int
        get() = R.layout.fragment_create_review


    private val viewModel: RatingViewModel by viewModel()

    private var productInvoice: InvoiceProductDetail? = null
    private var posImage = 1
    private var ratingEdit: Rating? = null
    private var uri1: Uri? = null
    private var uri2: Uri? = null
    private var uriVideo: Uri? = null
    private var bitmap1: Bitmap? = null
    private var bitmap2: Bitmap? = null
    private var bitmapVideo: Bitmap? = null
    private var rating: Rating? = null
    private var isViewRating: Boolean? = false

    var count = 0

    override fun bindEvent() {
        super.bindEvent()
        productInvoice = arguments?.get("product") as InvoiceProductDetail?
        viewModel.checkRating(
            productInvoice?.id ?: 0,
            CoreApplication.instance.account?.accountId ?: 0,
            productInvoice?.colorId ?: 0,
            productInvoice?.sizeId ?: 0,
            productInvoice?.invoiceId ?: 0
        )
        ratingEdit = arguments?.get("rating") as Rating?
        isViewRating = arguments?.get("isViewRating") as Boolean?

        viewBinding.btnDeleteReview.visibility =
            if (ratingEdit?.ratingID != null) View.VISIBLE else View.GONE

        if (ratingEdit != null)
            setDataRating()
        else if (productInvoice?.statusRating != 0) {
            viewModel.getRating(productInvoice?.statusRating ?: return)
        }
        viewBinding.item = productInvoice
        viewBinding.fragment = this
        setupToolbar()
    }

    private fun setDataRating() {
        viewBinding.btnDeleteReview.visibility =
            if (ratingEdit?.ratingID != null) View.VISIBLE else View.GONE
        viewBinding.ratingBar.rating = ratingEdit?.rating?.toFloat() ?: 0F
        viewBinding.edtComment.setText(ratingEdit?.comment ?: "")

        if (ratingEdit != null) {
            viewBinding.btnAddReview.text = getString(R.string.edit)
        } else {
            viewBinding.btnAddReview.text = getString(R.string.post)
        }

        if (ratingEdit?.imageUrl1.isNullOrBlank()) {
            viewBinding.ivClearImg1.gone()
        } else {
            Glide.with(requireContext())
                .load(ratingEdit?.imageUrl1)
                .into(viewBinding.ivImage1)
            viewBinding.ivClearImg1.visible()
        }

        if (ratingEdit?.imageUrl2.isNullOrBlank()) {
            viewBinding.ivClearImg2.gone()
        } else {
            Glide.with(requireContext())
                .load(ratingEdit?.imageUrl2)
                .into(viewBinding.ivImage2)
            viewBinding.ivClearImg2.visible()
        }

        if (ratingEdit?.videoUrl.isNullOrBlank()) {
            viewBinding.ivClearVideo.gone()
        } else {
            viewBinding.ivClearVideo.visible()
            Glide
                .with(requireContext())
                .load(File(ratingEdit?.videoUrl ?: return))
                .thumbnail(Glide.with(requireContext()).load(ratingEdit?.videoUrl))
                .placeholder(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_place_holder
                    )
                )
                .into(viewBinding.ivImageVideo)
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            viewBinding.btnNav.visible()
            initToolBar(viewBinding.layoutToolbar.toolbar, hasBackRight = false)
            setupToolbar(
                viewBinding.layoutToolbar.toolbar,
                getString(R.string.write_reviews)
            )
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()
        viewModel.addRatingResult.observe(this, Observer {
            if (it.data ?: 0 > 0) {
                messageHandler?.runMessageHandler(getString(R.string.addSuccess))
                requireActivity().onBackPressed()
            } else {
                messageHandler?.runMessageErrorHandler(getString(R.string.addFail))
            }
        })
        viewModel.ratingResponse.observe(this, Observer {
            if (it != null) {
                ratingEdit = it
                setDataRating()
            }
        })
        viewModel.updateRatingResult.observe(this, Observer {
            if (it.data ?: 0 > 0) {
                messageHandler?.runMessageHandler(getString(R.string.addSuccess))
                requireActivity().onBackPressed()
            } else {
                messageHandler?.runMessageErrorHandler(getString(R.string.addFail))
            }
        })
        viewModel.networkState.observe(this, Observer {
            if (it == true) viewBinding.layoutLoading.visible()
            else viewBinding.layoutLoading.gone()
        })
        viewModel.error.observe(this, Observer {
            if (!it.first.isNullOrBlank())
                messageHandler?.runMessageErrorHandler(it.first)
        })

        viewModel.urlImage1.observe(this, Observer {
            count++
            if (count == 3) {
                if (ratingEdit == null)
                    viewModel.addRating(rating)
                else {
                    viewModel.updateRating(rating)
                }
            }
        })
        viewModel.urlImage2.observe(this, Observer {
            count++
            if (count == 3) {
                if (ratingEdit == null)
                    viewModel.addRating(rating)
                else {
                    viewModel.updateRating(rating)
                }
            }
        })
        viewModel.urlVideo.observe(this, Observer {
            count++
            if (count == 3) {
                if (ratingEdit == null)
                    viewModel.addRating(rating)
                else {
                    viewModel.updateRating(rating)
                }
            }
        })
        viewModel.checkRating.observe(this, Observer {
            if (it.data ?: return@Observer > 0) {
                viewModel.getRatingDetail(it.data!!)
            }
        })
        viewModel.ratingDetail.observe(this, Observer {
            ratingEdit = it.data
            if (ratingEdit != null) setDataRating()
        })
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivProduct -> {
                navController.navigateAnimation(
                    R.id.fragment_product_detail,
                    bundle = bundleOf(
                        "productId" to productInvoice?.id
                    )
                )
            }
            R.id.ivImage1 -> {
                if (checkNoImage(viewBinding.ivClearImg1))
                    openGallery(1, 1)
                else {
                    showImageDetail(1)
                }
            }
            R.id.ivImage2 -> {
                if (checkNoImage(viewBinding.ivClearImg2))
                    openGallery(2, 1)
                else {
                    showImageDetail(2)
                }
            }
            R.id.ivImageVideo -> {
                if (checkNoImage(viewBinding.ivClearVideo))
                    openGallery(3, 2)
                else {
                    showImageDetail(3)
                }
            }
            R.id.ivClearImg1 -> {
                setVisibleImage(viewBinding.ivImage1, viewBinding.ivClearImg1)
            }
            R.id.ivClearImg2 -> {
                setVisibleImage(viewBinding.ivImage2, viewBinding.ivClearImg2)
            }
            R.id.ivClearVideo -> {
                setVisibleImage(viewBinding.ivImageVideo, viewBinding.ivClearVideo)
            }
            R.id.btnAddReview -> {
                checkData()
            }
            R.id.btnDeleteReview -> {
                delReview()
            }
        }
    }

    private fun delReview() {
        (requireActivity() as? BaseActivity<*>)?.showPopup(
            PopUp(R.layout.layout_pop_up, messageQueue = { popupBinding ->
                (popupBinding as? LayoutPopUpBinding)?.apply {
                    title = getString(R.string.confirmDelQuestion)
                    left = getString(R.string.ok)
                    right = getString(R.string.cancel)
                    btnOk.setOnClickListener {
                        ratingEdit?.ratingID?.let { ratingID ->
                            viewModel.delRating(ratingID)
                        }
                        (requireActivity() as? BaseActivity<*>)?.closePopup()
                        (requireActivity() as? BaseActivity<*>)?.onBackPressed()
                    }
                    btnCancel.setOnClickListener {
                        (requireActivity() as? BaseActivity<*>)?.closePopup()
                    }
                }

            })
        )

    }

    private fun checkNoImage(imageView: AppCompatImageView) = imageView.visibility == View.GONE

    private fun showImageDetail(pos: Int) {
        when (pos) {
            1, 2 -> {

            }
            3 -> {

            }
        }
    }

    private fun checkData() {
        when {
            viewBinding.ratingBar.rating == 0F -> {
                messageHandler?.runMessageErrorHandler(getString(R.string.errRating))
            }
            viewBinding.edtComment.getTextTrim() == "" -> {
                messageHandler?.runMessageErrorHandler(getString(R.string.errCmt))
            }
            else -> {
                if (ratingEdit != null) {
                    count = 0
                    rating = Rating().apply {
                        ratingID = ratingEdit?.ratingID
                        comment = viewBinding.edtComment.getTextTrim()
                        rating = viewBinding.ratingBar.rating.toInt()
                    }

                } else {
                    count = 0
                    rating = Rating().apply {
                        comment = viewBinding.edtComment.getTextTrim()
                        rating = viewBinding.ratingBar.rating.toInt()
                        accountID = CoreApplication.instance.account?.accountId
                        productID = productInvoice?.id
                        orderId = productInvoice?.invoiceId
                        colorId = productInvoice?.colorId
                        sizeId = productInvoice?.sizeId
                    }

                }
                viewModel.uploadFirebase(uri1, 1, ratingEdit?.imageUrl1)
                viewModel.uploadFirebase(uri2, 2, ratingEdit?.imageUrl2)
                viewModel.uploadFirebase(uriVideo, 3, ratingEdit?.videoUrl)
            }
        }
    }

    private fun setVisibleImage(image: AppCompatImageView, ivClear: AppCompatImageView) {
        ivClear.gone()
        image.setImageDrawable(
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_place_holder)
        )
    }

    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
        if (bitmap1 != null) {
            viewBinding.ivClearImg1.visible()
            viewBinding.ivImage1.setImageBitmap(bitmap1)
        }
        if (bitmap2 != null) {
            viewBinding.ivClearImg2.visible()
            viewBinding.ivImage2.setImageBitmap(bitmap2)
        }
        if (bitmapVideo != null) {
            viewBinding.ivClearVideo.visible()
            viewBinding.ivImageVideo.setImageBitmap(bitmapVideo)
        }
    }

    private fun showCamera(position: Int) {
        posImage = position
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_ID_IMAGE_CAPTURE
            )
        } else {
            if (requireActivity().packageManager
                    .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
            ) {
                try {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_ID_IMAGE_CAPTURE)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun openGallery(position: Int, type: Int) {
        posImage = position
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_ID_IMAGE_GALLERY
            )
        } else {
            val photoPickerIntent = Intent(Intent.ACTION_PICK) //ACTION_GET_CONTENT
            val imageFile =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val data = Uri.parse(imageFile.path)
            photoPickerIntent.setDataAndType(data, if (type == 1) "image/*" else "video/*")
            photoPickerIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, true)
            startActivityForResult(
                photoPickerIntent,
                REQUEST_ID_IMAGE_GALLERY
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        CoroutineScope(Dispatchers.Main + Job()).launch {
            if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_ID_IMAGE_GALLERY) {
                if (data != null) {
                    try {
                        when (posImage) {
                            1 -> {
                                uri1 = data.data
                                bitmap1 =
                                    BitmapResolver.getBitmap(
                                        requireContext().contentResolver,
                                        uri1
                                    )
                                viewBinding.ivImage1.setImageBitmap(bitmap1)
                                viewBinding.ivClearImg1.visible()
                            }
                            2 -> {
                                uri2 = data.data
                                bitmap2 =
                                    BitmapResolver.getBitmap(
                                        requireContext().contentResolver,
                                        uri2
                                    )
                                viewBinding.ivImage2.setImageBitmap(bitmap2)
                                viewBinding.ivClearImg2.visible()
                            }
                            3 -> {
                                uriVideo = data.data

                                try {
                                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                                    val cursor = uriVideo?.let {
                                        requireContext().contentResolver.query(
                                            it, filePathColumn, null, null, null
                                        )
                                    }
                                    cursor?.moveToFirst()

                                    val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                                    val picturePath = columnIndex?.let { cursor.getString(it) }
                                    cursor?.close()

                                    bitmapVideo = ThumbnailUtils.createVideoThumbnail(
                                        picturePath,
                                        MediaStore.Video.Thumbnails.MICRO_KIND
                                    )

/*
                                    Glide
                                        .with(requireContext())
                                        .load(uriVideo)
                                        .thumbnail(
                                            Glide.with(requireActivity()).load(uriVideo)
                                        )
                                        .placeholder(
                                            ContextCompat.getDrawable(
                                                requireContext(),
                                                R.drawable.ic_place_holder
                                            )
                                        )
                                        .into(viewBinding.ivImageVideo)
*/
                                    viewBinding.ivImageVideo.setImageBitmap(bitmapVideo)
                                    viewBinding.ivClearVideo.visible()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        Toast.makeText(requireContext(), "An error occured!", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }
    }

}