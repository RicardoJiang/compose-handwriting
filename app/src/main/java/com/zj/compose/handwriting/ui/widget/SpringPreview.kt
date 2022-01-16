package com.zj.compose.handwriting.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zj.compose.handwriting.viewmodel.SpringBoardViewModel

@Composable
fun SpringPreviewPage(onBack: () -> Unit) {
    SpringPreview()
}

@Composable
fun SpringPreview() {
    val viewModel = viewModel<SpringBoardViewModel>()
    val states by viewModel.viewStates.collectAsState()
    BoxWithConstraints(
        modifier = Modifier
            .width(100.dp)
            .fillMaxHeight()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(Color.Red, Offset.Zero, size = size)
            val itemWidth = size.width - 32
            val itemHeight = (size.height - 32) / states.bitmapList.size
            for (i in states.bitmapList.indices) {
                drawImage(
                    states.bitmapList[i].asImageBitmap(),
                    Offset(16f, 16f + itemHeight)
                )
            }
        }
    }
}