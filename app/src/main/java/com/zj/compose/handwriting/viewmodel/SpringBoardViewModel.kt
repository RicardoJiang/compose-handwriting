package com.zj.compose.handwriting.viewmodel

import android.view.MotionEvent
import androidx.compose.ui.graphics.Path
import androidx.lifecycle.ViewModel
import com.zj.compose.handwriting.ui.widget.HandWritingPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class SpringBoardViewModel : ViewModel() {
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
            val cx = (moveX + previousX) / 2
            val cy = (moveY + previousY) / 2
            val curPath = viewStates.value.curPath
            curPath.quadraticBezierTo(previousX, previousY, cx, cy)
            _viewStates.value =
                _viewStates.value.copy(curPath = curPath, curX = moveX, curY = moveY)
        }
    }

    private fun onActionUp(event: MotionEvent) {
        //updatePointList(event = event)
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
    val curY: Float = 0f
)

sealed class SpringBoardViewAction {
    data class ActionDown(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionMove(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionUp(val event: MotionEvent) : SpringBoardViewAction()
}