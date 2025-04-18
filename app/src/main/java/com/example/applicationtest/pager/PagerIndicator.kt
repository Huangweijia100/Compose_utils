package com.example.applicationtest.pager

import androidx.annotation.FloatRange
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabPosition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.lerp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


@Composable
fun PagerTabIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState,
    color: Color,
    @FloatRange(from = 0.0, to = 1.0) percent: Float = 1f,
    height: Dp = 4.dp,
    isFromUser: () -> Boolean, // 用于判断是否是用户手动操作
) {

    val currentPage by rememberUpdatedState(
        newValue = minOf(
            tabPositions.lastIndex,
            pagerState.currentPage
        )
    )
    val fraction by rememberUpdatedState(newValue = pagerState.currentPageOffsetFraction)
    val currentTab = tabPositions.getOrNull(currentPage) ?: return
    val previousTab = tabPositions.getOrNull(currentPage - 1)
    val nextTab = tabPositions.getOrNull(currentPage + 1)

    val currentAnimationLeft by animateDpAsState(
        targetValue = currentTab.left,
        animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
    ) // 如果是用户手动操作，则使用动画滑块

    Spacer(modifier = Modifier
        .fillMaxHeight()
        .drawBehind {
            val indicatorWidth = currentTab.width.toPx() * percent
            var nextWidth = indicatorWidth
            val indicatorOffset = if (fraction > 0 && nextTab != null) { // 根据前后的tab宽度计算滑块的大小
                nextWidth = nextTab.width.toPx() * percent
                lerp(currentTab.left, nextTab.left, fraction).toPx()
            } else if (fraction < 0 && previousTab != null) {
                nextWidth = previousTab.width.toPx() * percent
                lerp(currentTab.left, previousTab.left, -fraction).toPx()
            } else currentTab.left.toPx()
            val left = indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2)
            drawRoundRect(
                color = color,
                topLeft = Offset(
                    if (isFromUser()) currentAnimationLeft.toPx() else left,
                    size.height - height.toPx()
                ),
                size = Size(lerp(indicatorWidth, nextWidth, abs(fraction)), height.toPx()),
                cornerRadius = CornerRadius(50f)
            )
        }
    )
}

@Composable
fun PagerTab(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    index: Int,
    pageCount: Int,
    text: String,
    selectedContentColor: Color,
    unselectedContentColor: Color,
    selectedFontSize: TextUnit,
    unSelectedFontSize: TextUnit,
    selectedFontWeight: FontWeight = FontWeight.Bold,
    unSelectedFontWeight: FontWeight = FontWeight.Bold,
    padding: Dp = 9.dp,
) {
    val previousIndex = max(index - 1, 0)
    val nextIndex = min(index + 1, pageCount - 1)
    val currentIndexPlusOffset = pagerState.currentPage + pagerState.currentPageOffsetFraction
    val progress =
        if (currentIndexPlusOffset >= previousIndex && currentIndexPlusOffset <= nextIndex)
            1f - abs(index - currentIndexPlusOffset)
        else 0f
    val fontSize = lerp(unSelectedFontSize, selectedFontSize, progress)
    val fontWeight = lerp(unSelectedFontWeight, selectedFontWeight, progress)
    val color = lerp(unselectedContentColor, selectedContentColor, progress)

    Box(
        modifier = modifier
            .padding(horizontal = padding)
            .zIndex(10f), // 用于让字体浮现在indicator上面
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = color, fontSize = fontSize, fontWeight = fontWeight)
    }

}

val list = listOf(
    "News",
    "Release",
    "Ranking",
    "Servers",
    "Blog",
    "Team",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabScrollRow(
    modifier: Modifier,
    pagerState: PagerState,
    contentHeight:Dp,
    indicatorColor: Color,
    selectedContentColor: Color,
    unselectedContentColor: Color,
    selectedFontSize: TextUnit,
    unSelectedFontSize: TextUnit,
    indicatorHeight: Dp = 4.dp,
    edgePadding: Dp = 0.dp,
    selectedFontWeight: FontWeight = FontWeight.Bold,
    unSelectedFontWeight: FontWeight = FontWeight.Normal,
    list: () -> List<String>,
) {
    var fromUserTime by remember { mutableLongStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    ScrollableTabRow(modifier = modifier,
        selectedTabIndex = pagerState.currentPage,
        edgePadding = edgePadding,
        divider = {},
        indicator = { tabPositions ->
            PagerTabIndicator(
                tabPositions,
                pagerState,
                indicatorColor,
                height = indicatorHeight
            ) { fromUserTime > 0L }
        }) {
        list().forEachIndexed { index, s ->
            PagerTab(
                pagerState = pagerState,
                index = index,
                pageCount = list().size,
                text = s,
                modifier = Modifier
                    .height(contentHeight)
                    .clickable {
                        fromUserTime = System.currentTimeMillis()
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        coroutineScope.launch {
                            delay(250)
                            fromUserTime = 0L  // 重置操作
                        }
                    },
                unSelectedFontSize = unSelectedFontSize,
                selectedFontSize = selectedFontSize,
                unselectedContentColor = unselectedContentColor,
                selectedContentColor = selectedContentColor,
                unSelectedFontWeight = unSelectedFontWeight,
                selectedFontWeight = selectedFontWeight,
            )
        }
    }
}

@Composable
fun PrePageTab() {
    val pagerSate = rememberPagerState { list.size }
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        TabScrollRow(
            Modifier.background(Color.White),
            pagerSate,
            48.dp,
            Color.Cyan,
            Color.DarkGray,
            Color.LightGray,
            16.sp,
            14.sp
        ) { list }
        HorizontalPager(
            pagerSate, Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Gray),
            beyondViewportPageCount = list.size
        ) {
            TestBox(list.getOrNull(it) ?: "")
        }
    }

}

@Composable
fun TestBox(key: String) {
    Box(
        Modifier
            .fillMaxSize()
//            .randomColor()
            .background(Color.White)
    ) {
        Text(key, modifier = Modifier.align(Alignment.Center))
    }

}


fun Modifier.randomColor() = this.then(
    Modifier.background(
        listOf(
            Color.Gray,
            Color.Black,
            Color.Red,
            Color.Green,
            Color.White,
            Color.Blue,
            Color.DarkGray
        ).random()
    )
)
