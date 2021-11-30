package com.ptithcm.healthcare.view.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ptithcm.core.CoreApplication
import com.ptithcm.core.model.Account
import com.ptithcm.core.model.Profile
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.view.MainActivity
import com.ptithcm.healthcare.view.authentication.AuthenticationActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast


class SplashActivity : AppCompatActivity(){

    private var currentAccount: Profile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        currentAccount = CoreApplication.instance.profile
        if(currentAccount == null){
            startActivity<AuthenticationActivity>()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }else{
            startActivity<MainActivity>()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}