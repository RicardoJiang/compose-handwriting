package com.zj.compose.handwriting.viewmodel

import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import com.zj.compose.handwriting.ui.widget.HandWritingPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
        updatePointList(event = event)
    }

    private fun onActionMove(event: MotionEvent) {
        updatePointList(event = event)
    }

    private fun onActionUp(event: MotionEvent) {
        updatePointList(event = event)
    }

    private fun updatePointList(event: MotionEvent){
        val list = mutableListOf<HandWritingPoint>()
        list.addAll(_viewStates.value.pointList)
        list.add(HandWritingPoint(event.x, event.y))
        _viewStates.value = _viewStates.value.copy(pointList = list)
    }
}

data class SpringBoardViewStates(
    val pointList: List<HandWritingPoint> = listOf()
)

sealed class SpringBoardViewAction {
    data class ActionDown(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionMove(val event: MotionEvent) : SpringBoardViewAction()
    data class ActionUp(val event: MotionEvent) : SpringBoardViewAction()
}