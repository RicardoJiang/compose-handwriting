package com.zj.compose.handwriting.ui.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * 春联手写板
 */

@Composable
fun SpringBoard() {
    Box(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(onDragStart = {

                }, onDragEnd = {

                }, onDrag = { change, dragAmount ->

                })
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier) {

        }
    }
}