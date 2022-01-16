package com.zj.compose.handwriting.ui.widget

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi


@ExperimentalFoundationApi
@ExperimentalComposeUiApi
@Composable
fun HomeEntry() {
    var isPreview by remember { mutableStateOf(false) }

    if (isPreview) {
        SpringPreviewPage { isPreview = false }
    } else {
        SpringPage { isPreview = true }
    }

}

