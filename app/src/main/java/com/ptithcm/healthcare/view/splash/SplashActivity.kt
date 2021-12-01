package com.ptithcm.healthcare.view.splash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val soundUri: Uri = Uri.parse(
                "android.resource://" +
                        applicationContext.packageName +
                        "/" +
                        R.raw.alarm)

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .build()

            val channel = NotificationChannel(getString(R.string.default_notification_channel_id),
                "FCM Notification",
                NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(soundUri, audioAttributes)
            channel.enableVibration(true)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

        }

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