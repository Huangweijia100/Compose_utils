package com.example.applicationtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.applicationtest.pager.PrePageTab
import com.example.applicationtest.ui.theme.ApplicationTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hookScrollableTabRowMinimumTabWidth()
        setContent {
            ApplicationTestTheme {
                Scaffold {
                    Box(Modifier.padding(it)) {
                        PrePageTab()
                    }
                }
            }
        }
    }
}

private fun hookScrollableTabRowMinimumTabWidth(){
    runCatching {
        Class.forName("androidx.compose.material3.TabRowKt")
            .getDeclaredField("ScrollableTabRowMinimumTabWidth").apply {
                isAccessible = true
            }.set(null,0f)
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ApplicationTestTheme {
        Greeting("Android")
    }
}