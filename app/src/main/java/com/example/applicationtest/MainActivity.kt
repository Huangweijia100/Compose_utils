package com.example.applicationtest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.applicationtest.pager.PrePageTab
import com.example.applicationtest.ui.components.TopSlideDialogDemo
import com.example.applicationtest.ui.theme.ApplicationTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hookScrollableTabRowMinimumTabWidth()
        setContent {
            ApplicationTestTheme {
                Scaffold {
                    Box(Modifier.padding(it).fillMaxSize()) {
                        TopSlideDialogDemo()
//                        Image(
//                            painter = painterResource(R.drawable.ic_launcher_background),
//                            "",
//                            Modifier.fillMaxSize(),
//                            contentScale = ContentScale.FillBounds
//                        )
//                        PreImInput(Modifier.fillMaxWidth().height(500.dp).align(Alignment.BottomCenter))
                    }
//                    Box(Modifier.padding(it)) {
//                        PrePageTab()
//                    }
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