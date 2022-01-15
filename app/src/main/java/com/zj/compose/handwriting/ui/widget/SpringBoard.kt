package com.zj.compose.handwriting.ui.widget

import android.graphics.Bitmap
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zj.compose.handwriting.viewmodel.SpringBoardViewAction
import com.zj.compose.handwriting.viewmodel.SpringBoardViewModel

/**
 * 春联手写板
 */

@ExperimentalComposeUiApi
@Composable
fun SpringBoard() {
    val viewModel = viewModel<SpringBoardViewModel>()
    val states by viewModel.viewStates.collectAsState()
    BoxWithConstraints(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val width = with(LocalDensity.current) { maxWidth.toPx() }
        val height = with(LocalDensity.current) { maxHeight.toPx() }
        val bitmap = remember {
            Bitmap.createBitmap(
                width.toInt(),
                height.toInt(),
                Bitmap.Config.ARGB_8888
            )
        }
        val newCanvas = remember {
            android.graphics.Canvas(bitmap)
        }
        val paint = remember {
            Paint().apply {
                color = android.graphics.Color.BLACK
            }
        }
        Log.i("tiaoshi", "her:" + bitmap)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter(onTouchEvent = {
                    when (it.action) {
                        MotionEvent.ACTION_DOWN -> {
                            viewModel.dispatch(SpringBoardViewAction.ActionDown(it))
                        }
                        MotionEvent.ACTION_MOVE -> {
                            viewModel.dispatch(SpringBoardViewAction.ActionMove(it))
                        }
                        MotionEvent.ACTION_UP -> {
                            states.pointList.forEach { point ->
                                newCanvas.drawCircle(point.x, point.y, point.width, paint)
                            }
                            viewModel.dispatch(SpringBoardViewAction.ActionUp(it))
                        }
                    }
                    true
                })
        ) {
            drawIntoCanvas {
                it.nativeCanvas.drawBitmap(bitmap, 0f, 0f, paint)
            }
            states.pointList.forEach {


                drawCircle(Color.Black, it.width, Offset(it.x, it.y))
//                drawOval(
//                    Color.Black,
//                    topLeft = Offset(it.x, it.y),
//                    Size(it.width,it.width),
//                    style = Stroke(
//                        width = 10f,
//                        miter = 1f,
//                        cap = StrokeCap.Round,
//                        join = StrokeJoin.Round
//                    )
//                )
            }
        }
    }
}