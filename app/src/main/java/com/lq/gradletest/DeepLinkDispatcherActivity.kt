package com.lq.gradletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.lq.core.HRouter

class DeepLinkDispatcherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getInfo()
    }

    private fun getInfo() {
        intent?.data?.let { uri ->
            HRouter
                .buildUriWithNoParameters(uri.toString())
                .withContext(this)
                .navigate()
        }
        finish()
    }
}
