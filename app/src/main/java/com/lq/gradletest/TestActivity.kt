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
import com.lq.lib_annotation.Route
import com.lq.lib_annotation.RouteDeepLink

@Route(path = "/main/test")
@RouteDeepLink(["myapp://test","http://www.google.com:8090/"])
class TestActivity : ComponentActivity() {

    var userName: String?="gg"
    var account:Int ?= 10000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TestScreen()
        }
    }
    @Composable
    fun TestScreen(){
        Column(modifier = Modifier.fillMaxSize().padding(88.dp)) {
            Text(text = "测试信息", fontSize = 28.sp)
            Text("userName: $userName")
            Text("account: $account")
        }
    }
}

