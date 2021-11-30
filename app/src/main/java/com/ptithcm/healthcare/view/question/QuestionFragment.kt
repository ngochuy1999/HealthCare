package com.ptithcm.healthcare.view.question

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Question
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.base.BaseFragment
import com.ptithcm.healthcare.constant.ERROR_CODE_404
import com.ptithcm.healthcare.databinding.FragmentQuestionBinding
import com.ptithcm.healthcare.databinding.LayoutPopUpBinding
import com.ptithcm.healthcare.ext.*
import com.ptithcm.healthcare.util.PopUp
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.question.adapter.ITEM_DEL
import com.ptithcm.healthcare.view.question.adapter.ITEM_EDIT
import com.ptithcm.healthcare.view.question.adapter.ITEM_REPLY
import com.ptithcm.healthcare.view.question.adapter.QuestionAdapter
import com.ptithcm.healthcare.viewmodel.QuestionsViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuestionFragment : BaseFragment<FragmentQuestionBinding>() {

    override val layoutId: Int = R.layout.fragment_question

    private val viewModel: QuestionsViewModel by viewModel()

    private var productId: Int? = null
    private var question: Question? = null
    private var posAddSubQuestion: Int? = null
    private var posUpdateQuestion: Int? = null
    private var _parentQuestionID: Int? = null
    private var editQuestion: Question? = null

      private val adapter: QuestionAdapter by lazy {
        QuestionAdapter(this::adapterEvent, CoreApplication.instance.account?.accountId)
    }


    override fun onStop() {
        super.onStop()
        viewBinding.shimmerViewContainer.stopShimmer()
        viewBinding.shimmerViewContainer.gone()
    }

    private fun adapterEvent(
        item: Question?,
        position: Int?,
        typeEvent: Int,
        isSubQuestion: Boolean?
    ) {
        when (typeEvent) {
            ITEM_REPLY -> {
                viewBinding.edtQuestion.setText("@ ${item?.username} ")
                _parentQuestionID = item?.questionID
                posAddSubQuestion = position
                requireActivity().showKeyBoard()
                viewBinding.edtQuestion.setCursorEnd(viewBinding.edtQuestion.text.toString())
            }
            ITEM_DEL -> {
                (requireActivity() as? BaseActivity<*>)?.showPopup(
                    PopUp(R.layout.layout_pop_up, messageQueue = { popupBinding ->
                        (popupBinding as? LayoutPopUpBinding)?.apply {
                            title = getString(R.string.confirmDelQuestion)
                            left = getString(R.string.ok)
                            right = getString(R.string.cancel)
                            btnOk.setOnClickListener {
                                item?.questionID?.let { it1 ->
                                    viewModel.delQuestion(it1, if (isSubQuestion == true) 1 else 0)
                                }
                                (requireActivity() as? BaseActivity<*>)?.closePopup()
                            }
                            btnCancel.setOnClickListener {
                                (requireActivity() as? BaseActivity<*>)?.closePopup()
                            }
                        }

                    })
                )
            }
            ITEM_EDIT -> {
                posUpdateQuestion = position
                editQuestion = item
                viewBinding.edtQuestion.setText(item?.question)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.btnNav?.visibility = View.GONE
        viewBinding.shimmerViewContainer.startShimmer()
        viewBinding.shimmerViewContainer.visible()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productId = arguments?.get("productId") as Int?
        productId?.let { viewModel.getQuestion(it) }
        viewBinding.shimmerViewContainer.startShimmer()
        viewBinding.shimmerViewContainer.visible()
        setupToolbar()
        initAdapter()
        initViews()
    }

    private fun initViews() {
        viewBinding.swlRefresh.setOnTouchListener { v, event ->
            requireActivity().hideKeyboard()
            true
        }

        var questionStr = ""
        viewBinding.btnSend.setOnClickListener {
            if (CoreApplication.instance.account?.accountId == null) {
                navController.navigateAnimation(
                    R.id.loginFragment,
                    bundle = bundleOf("fromQuestionFragment" to true)
                )
                return@setOnClickListener
            }
            questionStr = viewBinding.edtQuestion.getTextTrim()
            if (questionStr.isNotEmpty()) {
                question = Question().apply {
                    accountID = CoreApplication.instance.account?.accountId
                    username = CoreApplication.instance.account?.userName
                    question = questionStr
                    productID = productId
                    parentQuestionID = _parentQuestionID
                }
                if (editQuestion == null) {
                    question?.let { it1 -> viewModel.addQuestion(it1) }

                    if (posAddSubQuestion == null) {
                        question?.let { it1 -> adapter.addQuestion(it1) }
                    } else {
                        adapter.addSubQuestion(question, posAddSubQuestion)
                    }
                } else {
                    editQuestion?.question = questionStr
                    editQuestion?.let { it1 -> viewModel.updateQuestion(it1) }
                    posUpdateQuestion?.let { it1 -> adapter.notifyItemChanged(it1) }
                    posUpdateQuestion = null
                }

                viewBinding.rvQuestion.smoothScrollToPosition(adapter.itemCount)
            } else {
                messageHandler?.runMessageErrorHandler(getString(R.string.error_empty, "Question"))
            }
            editQuestion = null
            viewBinding.edtQuestion.setText("")
        }
    }

    private fun setupToolbar() {
        (requireActivity() as? MainActivity)?.apply {
            initToolBar(
                viewBinding.layoutToolbar.toolbar, true,
                hasBackRight = false,
                hasLeft = false,
                hasRight = false
            )
            setupToolbar(
                viewBinding.layoutToolbar.toolbar,
                getString(R.string.question_product).capitalize()
            )
        }
    }

    private fun initAdapter() {
        viewBinding.rvQuestion.adapter = adapter
        viewBinding.swlRefresh.setOnRefreshListener {
            productId?.let { viewModel.getQuestion(it) }
        }
    }

    override fun bindViewModel() {
        super.bindViewModel()
        viewModel.listQuestion.observe(this, Observer {
            viewBinding.swlRefresh.isRefreshing = false
            if (it == null) {
                viewBinding.tvNoQuestions.visible()
            } else {
                viewBinding.shimmerViewContainer.startShimmer()
                viewBinding.shimmerViewContainer.gone()
                viewBinding.tvNoQuestions.gone()
                adapter.submitList(it)
            }
        })
        viewModel.networkState.observe(this, Observer {
            if (it == true) viewBinding.progressbar.visible() else viewBinding.progressbar.gone()
        })
        viewModel.error.observe(this, Observer {
            viewBinding.progressbar.gone()
            if (it.second == ERROR_CODE_404) {
                (requireActivity() as? MainActivity)?.isShowErrorNetwork(true)
            } else {
                messageHandler?.runMessageHandler(it.first)
            }
        })

        viewModel.updateQuestionResult.observe(this, Observer {
            viewBinding.progressbar.gone()
            editQuestion = null
            if (it?.data ?: 0 > 0) {
                messageHandler?.runMessageHandler(getString(R.string.editQuestionSuccess))

            } else {
                messageHandler?.runMessageErrorHandler(getString(R.string.editQuestionErr))
            }
        })

        viewModel.addQuestionResult.observe(this, Observer {
            if (it.status == true) {
                messageHandler?.runMessageHandler(getString(R.string.addQuestionSuccess))
                viewBinding.edtQuestion.emptyEdt()
                requireActivity().hideKeyboard()
                question = null
                posAddSubQuestion = null
                _parentQuestionID = null
            } else {
                messageHandler?.runMessageErrorHandler(getString(R.string.addQuestionFail))
                adapter.delQuestionFailure()
                viewBinding.rvQuestion.smoothScrollToPosition(adapter.itemCount)
            }
        })
    }
}