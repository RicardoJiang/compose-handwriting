package com.zj.compose.handwriting.ui.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.flowlayout.FlowRow
import com.zj.compose.handwriting.R
import com.zj.compose.handwriting.viewmodel.SpringBoardViewAction
import com.zj.compose.handwriting.viewmodel.SpringBoardViewModel
import com.zj.compose.handwriting.viewmodel.SpringBoardViewStates

/**
 * 春联手写板
 */

@ExperimentalComposeUiApi
@Composable
fun SpringPage(onPreview: () -> Unit) {
    val viewModel = viewModel<SpringBoardViewModel>()
    val states by viewModel.viewStates.collectAsState()
    val width = with(LocalDensity.current) { 300.dp.toPx() }
    val height = with(LocalDensity.current) { 300.dp.toPx() }
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
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .size(300.dp)
                    .padding(16.dp)
            ) {


                Image(
                    painter = painterResource(id = R.mipmap.icon_draw_bg),
                    modifier = Modifier
                        .fillMaxSize(),
                    contentDescription = ""
                )
                SpringBoard(viewModel = viewModel, states = states, bitmap, newCanvas)
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFCCCCCC))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.icon_delete),
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable {
                            newCanvas.drawColor(android.graphics.Color.WHITE, PorterDuff.Mode.CLEAR)
                        },
                    contentDescription = ""
                )
                Image(
                    painter = painterResource(id = R.mipmap.icon_confirm),
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable {
                            val curBitmap = bitmap.copy(bitmap.config, true)
                            viewModel.dispatch(SpringBoardViewAction.ConfirmItem(curBitmap))
                            newCanvas.drawColor(android.graphics.Color.WHITE, PorterDuff.Mode.CLEAR)
                        },
                    contentDescription = ""
                )
            }
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFCCCCCC))
            )
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                states.bitmapList.forEach {
                    Image(
                        bitmap = it.asImageBitmap(),
                        modifier = Modifier.size(80.dp),
                        contentDescription = ""
                    )
                }
            }
        }
        Button(
            onClick = { onPreview.invoke() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp)
                .align(Alignment.BottomCenter)
        ) {
            Text(text = "生成春联")
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun SpringBoard(
    viewModel: SpringBoardViewModel,
    states: SpringBoardViewStates,
    bitmap: Bitmap,
    newCanvas: Canvas
) {
    val paint = remember {
        Paint().apply {
            color = android.graphics.Color.BLACK
        }
    }
    BoxWithConstraints(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

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
            }
        }
    }
}