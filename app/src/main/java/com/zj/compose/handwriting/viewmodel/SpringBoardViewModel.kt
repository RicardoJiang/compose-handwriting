package com.zj.compose.handwriting.viewmodel

import android.graphics.Bitmap
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import com.zj.compose.handwriting.SpringBoardConfig.NORMAL_WIDTH
import com.zj.compose.handwriting.SpringBoardConfig.STEP_FACTOR
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.exp

class SpringBoardViewModel : ViewModel() {
    private val _viewStates = MutableStateFlow(SpringBoardViewStates())
    val viewStates = _viewStates.asStateFlow()
    private val bezier = Bezier()

    fun dispatch(action: SpringBoardViewAction) {
        when (action) {
            is SpringBoardViewAction.ActionDown -> onActionDown(action.event)
            is SpringBoardViewAction.ActionMove -> onActionMove(action.event)
            is SpringBoardViewAction.ActionUp -> onActionUp(action.event)
            is SpringBoardViewAction.ConfirmItem -> confirmItem(action.bitmap)
            is SpringBoardViewAction.DeleteItem -> deleteItem()
        }
    }

    private fun deleteItem() {
        val bitmapList = viewStates.value.bitmapList.toMutableList()
        if (bitmapList.isNotEmpty()) {
            bitmapList.removeAt(bitmapList.size - 1)
            _viewStates.value = _viewStates.value.copy(bitmapList = bitmapList)
        }
    }

    private fun confirmItem(bitmap: Bitmap) {
        val bitmapList = viewStates.value.bitmapList.toMutableList().apply { add(bitmap) }
        _viewStates.value = viewStates.value.copy(bitmapList = bitmapList)
    }

    private fun onActionDown(event: MotionEvent) {
        val curPoint = ControllerPoint(event.x, event.y)
        curPoint.width = 0f
        _viewStates.value = _viewStates.value.copy(
            pointList = emptyList(),
            curPoint = curPoint
        )
    }

    private fun onActionMove(event: MotionEvent) {
        val lastPoint = viewStates.value.curPoint
        val curPoint = ControllerPoint(event.x, event.y)
        val lineWidth = calWidth(event = event)
        curPoint.width = lineWidth
        if (viewStates.value.pointList.size < 2) {
            bezier.init(lastPoint, curPoint)
        } else {
            bezier.addNode(curPoint)
        }
        val curDis = getDistance(event)
        val steps: Int = 1 + (curDis / STEP_FACTOR).toInt()
        val step = 1.0 / steps
        val list = mutableListOf<ControllerPoint>()
        var t = 0.0
        while (t < 1.0) {
            val point: ControllerPoint = bezier.getPoint(t)
            list.add(point)
            t += step
        }
        addPoints(list)
        _viewStates.value = _viewStates.value.copy(curPoint = curPoint)
    }

    private fun onActionUp(event: MotionEvent) {
        bezier.end()
        _viewStates.value = _viewStates.value.copy(pointList = emptyList())
    }

    private fun calWidth(event: MotionEvent): Float {
        val distance = getDistance(event)
        val calVel = distance * 0.002
        val width = NORMAL_WIDTH * maxOf(exp(-calVel), 0.2)
        return width.toFloat()
    }

    private fun getDistance(event: MotionEvent): Float {
        val lastX = viewStates.value.curPoint.x
        val lastY = viewStates.value.curPoint.y
        return (event.x - lastX) * (event.x - lastX) + (event.y - lastY) * (event.y - lastY)
    }

    private fun addPoints(pointList: List<ControllerPoint>) {
        val list = mutableListOf<ControllerPoint>()
        list.addAll(_viewStates.value.pointList)
        list.addAll(pointList)
        _viewStates.value = _viewStates.value.copy(pointList = list)
    }
}

data class SpringBoardViewStates(
    val pointList: List<ControllerPoint> = listOf(),
    val curPoint: ControllerPoint = ControllerPoint(),
    val bitmapList: List<Bitmap> = listOf()
)

sealed class SpringBoardViewAction {
    data class ActionDown(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionMove(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionUp(val event: MotionEvent) : SpringBoardViewAction()
    data class ConfirmItem(val bitmap: Bitmap) : SpringBoardViewAction()
    object DeleteItem : SpringBoardViewAction()
}