package com.lq.gradletest

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
    val context = LocalContext.current
    Button(onClick = {
        toLogin()
    },modifier = Modifier.padding(50.dp,100.dp)) {
        Text(text = "跳转信息")
    }
}

fun toLogin(){
    HRouter.build("/main/test").withParams {
//       "userName" to "Android"
//        "account" to 100
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