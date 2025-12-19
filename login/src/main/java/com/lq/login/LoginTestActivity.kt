package com.lq.login

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

@Route(path = "/login/test")
class LoginTestActivity: ComponentActivity() {
    @AutoWired
    var userName:String = ""

    @AutoWired
    var account :Int?=null

//    @AutoWired
//    lateinit var mock: String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        HRouter.inject(this)

        setContent {
            LoginScreen()
        }
    }

    @Composable
    fun LoginScreen(){
        Column(modifier = Modifier.fillMaxSize().padding(88.dp)) {
            Text(text = "这是我的测试信息", fontSize = 28.sp)
            Text("userName: $userName")
            Text("account: $account")
//            Text("mock: $mock")
        }


    }
}
