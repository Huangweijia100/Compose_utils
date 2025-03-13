package com.example.applicationtest

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import kotlin.math.max

val LocalKeyboardChangeHeight = compositionLocalOf { 0 }
val LocalKeyShow = compositionLocalOf { false }
val LocalKeyboardHeight = compositionLocalOf { 0 }

@Composable
fun ImInput(modifier: Modifier, content: @Composable BoxScope.() -> Unit) {
    var preHeight by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }
    var isKeyShow by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var defaultHeight by remember { mutableIntStateOf(with(density) { 300.dp.toPx().toInt() }) }
    val keyHeight by remember {
        derivedStateOf {
            defaultHeight = max(defaultHeight, height)
            defaultHeight
        }
    }
    val navigationHeight =
        with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(this) }
    Box(modifier) {
        Spacer(
            Modifier
                .onSizeChanged {
                    height = it.height - navigationHeight
                    isKeyShow = height - preHeight > 0
                    preHeight = height
                    Log.i("ImInput", "size:${it.width},${it.height},isKeyShow:$isKeyShow")
                }
                .imePadding())
        CompositionLocalProvider(LocalKeyboardChangeHeight provides height) {
            CompositionLocalProvider(LocalKeyShow provides isKeyShow) {
                CompositionLocalProvider(LocalKeyboardHeight provides keyHeight) {
                    content()
                }
            }
        }
    }

}


const val DEFAULT_NONE = -1

val LocalKeyboardStatus = compositionLocalOf { -1 }

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxWithKeyboard(
    modifier: Modifier,
    keyboard: @Composable ColumnScope.(changeStatus: (status: Int) -> Unit) -> Unit,
    expand: @Composable BoxScope.() -> Unit,
    body: @Composable BoxScope.() -> Unit
) {
    var preHeight by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }
    var isKeyShow by remember { mutableStateOf(false) }
    val density = LocalDensity.current
    var defaultHeight by remember { mutableIntStateOf(with(density) { 300.dp.toPx().toInt() }) }
    val keyHeight by remember {
        derivedStateOf {
            defaultHeight = max(defaultHeight, height)
            defaultHeight
        }
    }
    var currentHeight by remember { mutableIntStateOf(0) }
    var keyStatus by remember { mutableIntStateOf(DEFAULT_NONE) }
    val navigationHeight =
        with(LocalDensity.current) { WindowInsets.navigationBars.getBottom(this) }

    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(isKeyShow) {
        if (isKeyShow)
            keyStatus = DEFAULT_NONE
//            keyboardController?.hide()
//        }
    }

    Box(modifier) {
        Log.i("BoxWithKeyboard", "box:recomposed")

        Spacer(
            Modifier
                .onSizeChanged {
                    height = it.height - navigationHeight
                    isKeyShow = height - preHeight > 0
                    preHeight = height
                    Log.i("ImInput", "size:${it.width},${it.height},isKeyShow:$isKeyShow")
                }
                .imePadding())
        CompositionLocalProvider(LocalKeyboardStatus provides keyStatus) {
//            Log.i("BoxWithKeyboard","compositionLocal:recomposed")

            Column(Modifier.fillMaxSize()) {
                Box(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .pointerInteropFilter {
                            keyboardController?.hide()
                            keyStatus = DEFAULT_NONE
                            false
                        }) { body() }
                keyboard {
                    keyStatus = it
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(height = with(LocalDensity.current) {
                            if (isKeyShow) {
                                min(keyHeight.toDp(), max(currentHeight.toDp(), height.toDp()))
                            } else {
                                if (keyStatus != DEFAULT_NONE) Dp.Unspecified else height.toDp()
                            }
//
                        })
                        .onSizeChanged {
                            currentHeight = it.height
                        }
                ) {
                    expand()
                }
            }
        }
    }
}

@Composable
fun PreImInput(modifier: Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current

    BoxWithKeyboard(modifier.fillMaxSize(), keyboard = {
        val keyStatus = LocalKeyboardStatus.current

        Log.i("BoxWithKeyboard", "keyboard:recomposed")
        val textSate = rememberTextFieldState("")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BasicTextField(
                textSate,
                Modifier
                    .weight(1f)
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.Magenta),
                textStyle = TextStyle(fontSize = 16.sp)
            )
            Icon(
                Icons.Default.Add, "", Modifier
                    .size(32.dp)
                    .clickable {
                        if (keyStatus == DEFAULT_NONE)
                            it(1)
                        else it(DEFAULT_NONE)
                        if (keyStatus == DEFAULT_NONE) keyboardController?.hide()
                    })
        }
    }, expand = {
        Log.i("BoxWithKeyboard", "expand:recomposed")
        Box(
            Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(Color.DarkGray)
        )
    }) {
        Log.i("BoxWithKeyboard", "body:recomposed")
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Gray)
        )
    }
}