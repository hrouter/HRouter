package com.lq.gradletest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.lq.lib_annotation.Route
import com.lq.lib_api.HRouter
import com.lq.gradletest.ui.theme.GradleTestTheme
import com.lq.gradletest.ui.theme.MockInfo

@Route(path = "/main/main")
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GradleTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }


}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize().wrapContentHeight()) {
        Button(onClick = {
            toLogin("/main/test")
        },modifier = Modifier.padding(50.dp,20.dp)) {
            Text(text = "跳转")
        }


        Button(onClick = {
            toLogin("/login/test")
        },modifier = Modifier.padding(50.dp,20.dp)) {
            Text(text = "登录拦截")
        }

        Button(onClick = {
            toLogin("/fake/fake")
        },modifier = Modifier.padding(50.dp,20.dp)) {
            Text(text = "降级")
        }

       /* Button(onClick = {
            HRouter.build("/main/test").withParams {
                "userName" to "Android"
                "account" to 20
                "mock" to "Mock"
                "mockInfo" to MockInfo("MockAndroid","Mock123456",100f)
            }.navigate()
        },modifier = Modifier.padding(50.dp,20.dp)) {
            Text(text = "注入类型错误该闪退")
        }*/
    }
}

fun toLogin(path: String){
    HRouter.build(path).withParams {
       "userName" to "Android"
        "account" to 20f
        "mock" to "Mock"
        "user" to UserInfo("Android","123456",10f)
    }.navigate()
}

fun toTest(context: Context){
}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GradleTestTheme {
        Greeting("Android")
    }

}