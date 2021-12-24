package com.ptithcm.healthcare.view.authentication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.databinding.ActivityAuthenticationBinding
import com.ptithcm.healthcare.viewmodel.ShareDataViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AuthenticationActivity : BaseActivity<ActivityAuthenticationBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}