package com.ptithcm.healthcare.view.authentication

import android.os.Bundle
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.databinding.ActivityCreateProfileBinding
import com.ptithcm.healthcare.viewmodel.ShareDataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateProfileActivity : BaseActivity<ActivityCreateProfileBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_create_profile

    private val shareDataViewmodel by viewModel<ShareDataViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        shareDataViewmodel.getAllTags()
    }

}