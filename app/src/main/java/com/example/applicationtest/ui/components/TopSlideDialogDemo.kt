package com.example.applicationtest.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TopSlideDialogDemo() {
    var showDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 显示弹窗的按钮
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("显示顶部弹窗")
        }

        // 顶部弹窗
        TopSlideDialog(
            isVisible = showDialog,
            onDismiss = { showDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("这是一个可以上滑关闭的顶部弹窗")
                Text("你可以尝试上下滑动它")
                Text("向上滑动超过阈值会自动关闭")
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { showDialog = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("关闭")
                }
            }
        }
    }
}