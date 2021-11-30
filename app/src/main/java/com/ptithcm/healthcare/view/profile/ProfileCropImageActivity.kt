package com.ptithcm.healthcare.view.profile

import android.app.Activity
import android.content.Intent
import com.ptithcm.healthcare.R
import com.ptithcm.healthcare.base.BaseActivity
import com.ptithcm.healthcare.constant.KEY_FILE
import com.ptithcm.healthcare.databinding.ActivityProfileCropImageBinding
import java.io.File

class ProfileCropImageActivity : BaseActivity<ActivityProfileCropImageBinding>() {

    override val layoutId: Int
        get() = R.layout.activity_profile_crop_image

    fun returnIntent(file: File?) {
        val resultIntent = Intent()
        resultIntent.putExtra(KEY_FILE, file)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}
