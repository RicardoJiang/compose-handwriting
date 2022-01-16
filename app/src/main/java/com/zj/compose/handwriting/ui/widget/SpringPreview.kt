package com.zj.compose.handwriting.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zj.compose.handwriting.viewmodel.SpringBoardViewModel

@Composable
fun SpringPreviewPage(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SpringPreview()
    }
}

@Composable
fun SpringPreview() {
    val viewModel = viewModel<SpringBoardViewModel>()
    val states by viewModel.viewStates.collectAsState()
    BoxWithConstraints(
        modifier = Modifier
            .width(300.dp)
            .height((300 * states.bitmapList.size).dp)
    ) {
        val itemSize = with(LocalDensity.current) { 300.dp.toPx() }

        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Red, Offset.Zero, size = size)
            for (i in states.bitmapList.indices) {
                drawImage(
                    states.bitmapList[i].asImageBitmap(),
                    Offset(0f, itemSize * i)
                )
            }
        }
    }
}