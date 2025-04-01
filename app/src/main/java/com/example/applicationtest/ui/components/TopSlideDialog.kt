package com.example.applicationtest.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun TopSlideDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    var offsetY by remember { mutableStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    
    val animatedOffset by animateFloatAsState(
        targetValue = if (!isVisible) -1000f else offsetY,
        label = "offset"
    )

    if (isVisible || isDragging) {
        Box(
            modifier = Modifier
//                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onDragStart = { isDragging = true },
                        onDragEnd = {
                            isDragging = false
                            // 如果上滑超过一定距离，则关闭弹窗
                            if (offsetY < -200) {
                                onDismiss()
                                offsetY = 0f
                            } else {
                                offsetY = 0f
                            }
                        },
                        onDragCancel = {
                            isDragging = false
                            offsetY = 0f
                        },
                        onVerticalDrag = { change, dragAmount ->
                            change.consume()
                            // 只允许向上拖动或在原位置附近拖动
                            offsetY = (offsetY + dragAmount).coerceAtMost(0f)
                        }
                    )
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .offset { IntOffset(0, animatedOffset.roundToInt()) }
                    .padding(16.dp),
//                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
//                Box(
//                    modifier = Modifier.padding(16.dp)
//                ) {
                    content()
//                }
            }
        }
    }
}