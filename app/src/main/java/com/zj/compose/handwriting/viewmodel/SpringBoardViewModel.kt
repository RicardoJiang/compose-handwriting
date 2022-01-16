package com.zj.compose.handwriting.viewmodel

import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.zj.compose.handwriting.viewmodel.SpringBoardViewModel.Companion.NORMAL_WIDTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SpringBoardViewModel : ViewModel() {
    companion object {
        const val MAX_SPEED = 2f
        const val MIN_SPEED = 0.1f
        const val NORMAL_WIDTH = 20f
        const val STEP_FACTOR = 10
    }

    private val _viewStates = MutableStateFlow(SpringBoardViewStates())
    val viewStates = _viewStates.asStateFlow()
    private val bezier = Bezier()

    fun dispatch(action: SpringBoardViewAction) {
        when (action) {
            is SpringBoardViewAction.ActionDown -> onActionDown(action.event)
            is SpringBoardViewAction.ActionMove -> onActionMove(action.event)
            is SpringBoardViewAction.ActionUp -> onActionUp(action.event)
        }
    }

    private fun onActionDown(event: MotionEvent) {
        val curPoint = ControllerPoint(event.x, event.y)
        curPoint.width = 0f
        _viewStates.value = _viewStates.value.copy(
            pointList = emptyList(),
            curPoint = curPoint,
            curX = event.x,
            curY = event.y,
            curWidth = NORMAL_WIDTH,
            curTime = System.currentTimeMillis()
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
        val moveX = event.x
        val moveY = event.y
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
        _viewStates.value = _viewStates.value.copy(
            curPoint = curPoint,
            curX = moveX,
            curY = moveY,
            curWidth = lineWidth,
            curTime = System.currentTimeMillis()
        )

    }

    private fun onActionUp(event: MotionEvent) {
        bezier.end()
        _viewStates.value = _viewStates.value.copy(pointList = emptyList())
        //updatePointList(event = event)
    }

    private fun calWidth(event: MotionEvent): Float {
        val distance = getDistance(event)
        val calVel = distance * 0.002
        //返回指定数字的自然对数
        //手指滑动的越快，这个值越小，为负数
        val vfac = Math.log(1.5 * 2.0f) * -calVel

        val width = NORMAL_WIDTH * maxOf(Math.exp(-calVel), 0.2)
        return width.toFloat()
    }

    private fun getDistance(event: MotionEvent): Float {
        val lastX = viewStates.value.curX
        val lastY = viewStates.value.curY
        return (event.x - lastX) * (event.x - lastX) + (event.y - lastY) * (event.y - lastY)
    }

    private fun getTime(): Long {
        val lastTime = viewStates.value.curTime
        return System.currentTimeMillis() - lastTime
    }

    private fun updatePointList(event: MotionEvent) {
        val list = mutableListOf<ControllerPoint>()
        list.addAll(_viewStates.value.pointList)
        list.add(ControllerPoint(event.x, event.y))
        _viewStates.value = _viewStates.value.copy(pointList = list)
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
    val curX: Float = 0f,
    val curY: Float = 0f,
    val curWidth: Float = NORMAL_WIDTH,
    val curTime: Long = System.currentTimeMillis()
)

sealed class SpringBoardViewAction {
    data class ActionDown(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionMove(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionUp(val event: MotionEvent) : SpringBoardViewAction()
    object DeleteItem : SpringBoardViewAction()
    object ConfirmItem : SpringBoardViewAction()
}