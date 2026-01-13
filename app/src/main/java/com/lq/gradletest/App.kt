package com.lq.gradletest

import android.app.Application
import com.lq.core.HRouter

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        HRouter.init(this)
    }
}
