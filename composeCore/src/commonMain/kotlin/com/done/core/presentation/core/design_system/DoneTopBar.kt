package com.done.core.presentation.core.design_system

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.core.presentation.core.ui.theme.DoneTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneTopBar(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    titleFontWeight: FontWeight = FontWeight.Medium,
    navigationIcon: ImageVector? = null,
    navigationIconContent: (@Composable () -> Unit)? = null,
    navigationIconDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContent: (@Composable () -> Unit)? = null,
    actionIconDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.onPrimary
) {

    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrolledContainerColor
        ),
        title = {
            if (titleText != null) {
                Text(
                    text = titleText,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = titleFontWeight
                )
            } else if (titleContent != null) {
                titleContent()
            }
        },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else if (navigationIconContent != null) {
                navigationIconContent()
            }
        },
        actions = {
            Row {
                if (actionIcon != null) {
                    IconButton(onClick = onActionClick) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = actionIconDescription,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else if (actionIconContent != null) {
                    actionIconContent()
                }
                Spacer(Modifier.width(16.dp))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneMediumTopBar(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    titleFontWeight: FontWeight = FontWeight.SemiBold,
    navigationIcon: ImageVector? = null,
    navigationIconContent: (@Composable () -> Unit)? = null,
    navigationIconDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContent: (@Composable () -> Unit)? = null,
    actionIconDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.onPrimary
) {

    MediumTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrolledContainerColor
        ),
        title = {
            if (titleText != null) {
                Text(
                    text = titleText,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = titleFontWeight
                )
            } else if (titleContent != null) {
                titleContent()
            }
        },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else if (navigationIconContent != null) {
                navigationIconContent()
            }
        },
        actions = {
            Row {
                if (actionIcon != null) {
                    IconButton(onClick = onActionClick) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = actionIconDescription,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else if (actionIconContent != null) {
                    actionIconContent()
                }
                Spacer(Modifier.width(8.dp))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoneLargeTopBar(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    titleContent: (@Composable () -> Unit)? = null,
    titleFontWeight: FontWeight = FontWeight.Bold,
    navigationIcon: ImageVector? = null,
    navigationIconContent: (@Composable () -> Unit)? = null,
    navigationIconDescription: String? = null,
    actionIcon: ImageVector? = null,
    actionIconContent: (@Composable () -> Unit)? = null,
    actionIconDescription: String? = null,
    onNavigationClick: () -> Unit = {},
    onActionClick: () -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
    containerColor: Color = MaterialTheme.colorScheme.onPrimary,
    scrolledContainerColor: Color = MaterialTheme.colorScheme.onPrimary
) {

    LargeTopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        windowInsets = windowInsets,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = scrolledContainerColor
        ),
        title = {
            if (titleText != null) {
                Text(
                    text = titleText,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = titleFontWeight
                )
            } else if (titleContent != null) {
                titleContent()
            }
        },
        navigationIcon = {
            if (navigationIcon != null) {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
            } else if (navigationIconContent != null) {
                navigationIconContent()
            }
        },
        actions = {
            Row {
                if (actionIcon != null) {
                    IconButton(onClick = onActionClick) {
                        Icon(
                            imageVector = actionIcon,
                            contentDescription = actionIconDescription,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                } else if (actionIconContent != null) {
                    actionIconContent()
                }
                Spacer(Modifier.width(8.dp))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun TopBarPreview() {
    DoneTheme {
        DoneTopBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = Icons.AutoMirrored.Default.ArrowBackIos,
            actionIcon = Icons.Outlined.Search,
            titleText = "Done",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun MediumTopAppBarPreview() {
    DoneTheme {
        DoneMediumTopBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = Icons.AutoMirrored.Default.ArrowBackIos,
            actionIcon = Icons.Outlined.Search,
            titleText = "Done",
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun LargeTopBarPreview() {
    DoneTheme {
        DoneLargeTopBar(
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = Icons.AutoMirrored.Default.ArrowBackIos,
            actionIcon = Icons.Outlined.Search,
            titleText = "Done",
        )
    }
}