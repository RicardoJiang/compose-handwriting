package com.zj.compose.handwriting.viewmodel

import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.zj.compose.handwriting.ui.widget.HandWritingPoint
import com.zj.compose.handwriting.viewmodel.SpringBoardViewModel.Companion.NORMAL_WIDTH
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class SpringBoardViewModel : ViewModel() {
    companion object {
        const val MAX_SPEED = 2f
        const val MIN_SPEED = 0.1f
        const val NORMAL_WIDTH = 20f
    }

    private val _viewStates = MutableStateFlow(SpringBoardViewStates())
    val viewStates = _viewStates.asStateFlow()

    fun dispatch(action: SpringBoardViewAction) {
        when (action) {
            is SpringBoardViewAction.ActionDown -> onActionDown(action.event)
            is SpringBoardViewAction.ActionMove -> onActionMove(action.event)
            is SpringBoardViewAction.ActionUp -> onActionUp(action.event)
        }
    }

    private fun onActionDown(event: MotionEvent) {
        //updatePointList(event = event)
        val curPath = viewStates.value.curPath
        curPath.reset()
        curPath.moveTo(event.x, event.y)
        _viewStates.value =
            _viewStates.value.copy(curPath = curPath, curX = event.x, curY = event.y)
    }

    private fun onActionMove(event: MotionEvent) {
        //updatePointList(event = event)
        val moveX = event.x
        val moveY = event.y
        val previousX = viewStates.value.curX
        val previousY = viewStates.value.curY
        val dx = abs(moveX - previousX)
        val dy = abs(moveY - previousY)
        if (dx > 3 || dy > 3) {
            val curPath = viewStates.value.curPath
            curPath.quadraticBezierTo(previousX, previousY, moveX, moveY)
            val lineWidth = calWidth(event = event)
            _viewStates.value = _viewStates.value.copy(
                curPath = curPath,
                curX = moveX,
                curY = moveY,
                curWidth = lineWidth,
                curTime = System.currentTimeMillis()
            )
        }
    }

    private fun onActionUp(event: MotionEvent) {
        //updatePointList(event = event)
    }

    /**
     * var v = s / t;
    var ResultLineWidth;
    //处理速度很慢和很快的情况
    if (v <= minlinespeed)
    ResultLineWidth = maxlinewidth;
    else if (v >= maxlinespeed)
    ResultLineWidth = minlinewidth;
    else
    ResultLineWidth = maxlinewidth - (v - minlinespeed) / (maxlinespeed - minlinespeed) * (maxlinewidth - minlinewidth);
    if (laslinewidth == -1)
    return ResultLineWidth;
    return laslinewidth * 2 / 3 + ResultLineWidth * 1 / 3;
     */
    private fun calWidth(event: MotionEvent): Float {
        val distance = getDistance(event)
        val duration = getTime()
        val speed = distance / duration
        val lineWidth = when {
            speed <= MIN_SPEED -> {
                (NORMAL_WIDTH * MAX_SPEED)
            }
            speed >= MAX_SPEED -> {
                (NORMAL_WIDTH * MIN_SPEED)
            }
            else -> {
                (NORMAL_WIDTH * speed)
            }
        }
        val lastWidth = viewStates.value.curWidth
        return ((lastWidth * 2 / 3) + (lineWidth * 1 / 3))
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
        val list = mutableListOf<HandWritingPoint>()
        list.addAll(_viewStates.value.pointList)
        list.add(HandWritingPoint(event.x, event.y))
        _viewStates.value = _viewStates.value.copy(pointList = list)
    }
}

data class SpringBoardViewStates(
    val pointList: List<HandWritingPoint> = listOf(),
    val curPath: Path = Path(),
    val curX: Float = 0f,
    val curY: Float = 0f,
    val curWidth: Float = NORMAL_WIDTH,
    val curTime: Long = System.currentTimeMillis()
)

sealed class SpringBoardViewAction {
    data class ActionDown(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionMove(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionUp(val event: MotionEvent) : SpringBoardViewAction()
}