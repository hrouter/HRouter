package com.lq.gradletest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lq.lib_annotation.AutoWired
import com.lq.lib_annotation.Route
import com.lq.lib_annotation.DeepLink
import com.lq.lib_api.HRouter

@Route(path = "/main/test")
@DeepLink(schemes = ["myapp"], hosts = ["com.lq.mock","main/test"])
class TestActivity : ComponentActivity() {

    @AutoWired
    var userName: String?="gg"

    @AutoWired
    var account:Int ?= 10000

    @AutoWired
    lateinit var user: UserInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestScreen()
        }
        HRouter.inject(this)
    }
    @Composable
    fun TestScreen(){
        Column(modifier = Modifier.fillMaxSize().padding(88.dp)) {
            Text(text = "/main/test", fontSize = 28.sp)
            Text("userName: $userName")
            Text("account: $account")
            Text("user:$user")
        }
    }
}

