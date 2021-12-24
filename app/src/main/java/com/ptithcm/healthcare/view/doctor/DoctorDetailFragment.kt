package com.ptithcm.healthcare.view.doctor

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Doctor
import com.ptithcm.core.model.MedicalBill
import com.ptithcm.core.model.ProductClothesDetail
import com.ptithcm.core.model.RatingProduct
import com.ptithcm.core.util.*
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.KEY_ARGUMENT
import com.ptithcm.healthcare.databinding.FragmentDoctorDetailBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.doctor.adapter.ConsultationRecyclerViewAdapter
import com.ptithcm.healthcare.viewmodel.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_doctor_detail.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class DoctorDetailFragment : BaseFragment<FragmentDoctorDetailBinding>() {

    override val layoutId: Int
        get() = R.layout.fragment_doctor_detail

    private val wishListViewModel: WishListViewModel by viewModel()
    private val medicalBillViewModel: MedicalBillViewModel by viewModel()
    private val questionsViewModel: QuestionsViewModel by viewModel()
    private val ratingViewModel: RatingViewModel by viewModel()

    private lateinit var consultationAdapter: ConsultationRecyclerViewAdapter
    private var doctor: Doctor? = null
    private var pid: Int? = null
    private var productDetail: ProductClothesDetail? = null
    private val isLogin = ObjectHandler.isLogin()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pid = CoreApplication.instance.account?.accountId
        (arguments?.getParcelable(KEY_ARGUMENT) as? Doctor)?.let {
            doctor = it
            it.doctorId?.let { it1 -> medicalBillViewModel.getAllConsult(it1)
            medicalBillViewModel.checkIsLike(pid,it1)}
        }

        activity?.btnNav?.visibility = View.GONE
        (activity as? BaseActivity<*>)?.isShowLoading(false)
        doctor?.doctorId?.let { questionsViewModel.getQuestionCount(it) }
        doctor?.doctorId?.let { ratingViewModel.getRatingProduct(it) }
        bindingViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpToolBar()

    }

    override fun bindEvent() {
        viewBinding.fragment = this
        viewBinding.isNotEmpty = true
        bindProduct()
        consultationAdapter =
            ConsultationRecyclerViewAdapter(arrayListOf(), this::listenerConsult)
        viewBinding.rvConsult.adapter = consultationAdapter

    }

    fun bindingViewModel() {

        medicalBillViewModel.isLikeResult.observe(this, Observer {
            (requireActivity() as? MainActivity)?.apply {
                viewBinding.layoutToolbar.isLike = it.status
            }
        })

        medicalBillViewModel.consultationResult.observe(this, Observer {
            if (it != null) {
                viewBinding.isNotEmpty = it.size > 0
                consultationAdapter.setListConsult(it)
            }
        })
        wishListViewModel.addAndRemoveResult.observe(this, Observer {})

        medicalBillViewModel.error.observe(this, Observer {
            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
        })
//        ratingViewModel.error.observe(this, Observer {
//            (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
//        })
        ratingViewModel.ratingAverage.observe(this, Observer {
            setDataRating(it.data ?: return@Observer)
        })

        questionsViewModel.questionCount.observe(this, Observer {
            if (it.data ?: 0 != 0) {
                viewBinding.btnQuestions.text = getString(R.string.commentCount, it.data)
            } else
                viewBinding.btnQuestions.text = getString(R.string.comment)
        })

    }

    private fun setDataRating(rating: RatingProduct) {
        viewBinding.tvRatingCount.text =
            when {
                (rating.ratingCount?.toInt() ?: 0) > 1 -> getString(
                    R.string.ratingInt,
                    rating.ratingCount?.toInt()
                )
                (rating.ratingCount?.toInt() ?: 0) == 1 -> getString(R.string.rating1)
                else -> getString(R.string.no_rating)
            }
        viewBinding.rbAverage.rating = rating.rating?.toFloat() ?: 0F
    }

    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
    }

    fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                navController.popBackStack()
            }
            R.id.btnConsult, R.id.layout_l2 -> {
                toggleDrawable(viewBinding.btnConsult, viewBinding.layoutL2.expandOrCollapse())
            }
            R.id.rbAverage -> {
                navController.navigateAnimation(
                    R.id.ratingFragment,
                    bundle = bundleOf("productDetail" to productDetail)
                )
            }
            R.id.btnQuestions -> {
                navController.navigateAnimation(
                    R.id.questionFragment,
                    bundle = bundleOf("productId" to productDetail?.id)
                )
            }
            R.id.tvRatingCount, R.id.btnRating -> {
                navController.navigateAnimation(
                    R.id.ratingFragment,
                    bundle = bundleOf("productDetail" to productDetail)
                )
            }

            R.id.ivRight -> {
                v.apply {
                    if (isLogin) {
                        isSelected = isSelected.not()
                        wishListViewModel.addAndRemoveToWishList(productDetail?.id)
                        wishListViewModel.productWishListChange.value = Pair(
                            wishListViewModel.productWishListChange.value?.first ?: 0,
                            isSelected
                        )
                    } else {
                        messageHandler?.runMessageErrorHandler(getString(R.string.login_to_add_wish_list))
                    }
                }
            }
        }
    }

    private fun bindProduct() {
        doctor?.let {
            viewBinding.item = it
            val glideApp = Glide.with(requireContext())
            glideApp.load(it.imageUrl)
                .centerCrop()
                .error(R.drawable.doctor)
                .into(viewBinding.avatar)
        }
    }


    private fun setUpToolBar() {
        (requireActivity() as? BaseActivity<*>)?.apply {
            val toolbar = when (this) {
                is MainActivity -> viewBinding.layoutToolbar.toolbar
                else -> null
            }
            initToolbar(
                hasBackRight = false,
                hasLeft = false,
                hasCount = false,
                isProductPage = true
            )
            toolbar?.isSelected = false
            setupToolbar(
                "DOCTOR",
                isBackPress = false,
                messageQueue = {
                    onClick(it)
                })
            toolbar?.findViewById<AppCompatImageButton>(R.id.ivRight)?.apply {
                setImageDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_star_active)
                )
            }
        }
    }

    private fun listenerConsult(medicalBill: MedicalBill?) {
    }

}