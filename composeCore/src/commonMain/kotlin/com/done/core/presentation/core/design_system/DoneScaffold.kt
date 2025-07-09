package com.done.core.presentation.core.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneScaffold(
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState()),
    withScrollBehavior: Boolean = true,
    withPullToRefresh: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.background,
    showTopBarHorizontalDivider: Boolean = false,
    snackbarHost: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    topBar: @Composable (TopAppBarScrollBehavior) -> Unit = {},
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    bottomBar: @Composable () -> Unit = {},
    topBarContainerColor: Color = MaterialTheme.colorScheme.background,
    onPullToRefresh: () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {

    val scrollModifier =
        if (withScrollBehavior) modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        else modifier

    if (withPullToRefresh) {
        val refreshScope = rememberCoroutineScope()
        var refreshing by remember { mutableStateOf(false) }

        fun refresh() = refreshScope.launch {
            refreshing = true
            delay(100)
            onPullToRefresh()
            delay(500)
            refreshing = false
        }

        val state = rememberPullToRefreshState()
        PullToRefreshBox(
            isRefreshing = refreshing,
            state = state,
            onRefresh = ::refresh
        ) {
            Scaffold(
                containerColor = containerColor,
                contentWindowInsets = contentWindowInsets,
                snackbarHost = snackbarHost,
                modifier = scrollModifier,
                topBar = {
                    Column(
                        modifier = Modifier.background(topBarContainerColor)
                    ) {
                        topBar(scrollBehavior)
                        if (showTopBarHorizontalDivider) {
                            HorizontalDivider(Modifier.alpha(0.4f))
                        }
                    }
                },
                bottomBar = bottomBar,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition
            ) { padding ->
                content(padding)
            }
        }
    } else {
        Scaffold(
            containerColor = containerColor,
            contentWindowInsets = contentWindowInsets,
            snackbarHost = snackbarHost,
            modifier = scrollModifier,
            topBar = {
                Column(
                    modifier = Modifier.background(topBarContainerColor)
                ) {
                    topBar(scrollBehavior)
                    if (showTopBarHorizontalDivider) {
                        HorizontalDivider(Modifier.alpha(0.4f))
                    }
                }
            },
            bottomBar = bottomBar,
            floatingActionButton = floatingActionButton,
            floatingActionButtonPosition = floatingActionButtonPosition
        ) { padding ->
            content(padding)
        }
    }
}
