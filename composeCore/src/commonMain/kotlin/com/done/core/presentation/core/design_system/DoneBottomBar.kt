package com.done.core.presentation.core.design_system

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DoneBottomBar(
    modifier: Modifier = Modifier,
    items: List<BottomNavigationItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit,
) {

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
    ) {
        Column {
            HorizontalDivider(Modifier.alpha(0.3f))
            Row(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = selectedItem == index
                    NavigationBarItem(
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor =  MaterialTheme.colorScheme.primary.copy(0.1f),
                        ),
                        selected = isSelected,
                        onClick = {
                            onItemClick(index)
                        },
                        icon = {
                            Icon(
                                painter = if (isSelected) {
                                    painterResource(item.selectedIcon)
                                } else {
                                    painterResource(item.unselectedIcon)
                                },
                                contentDescription = stringResource(item.label),
                                tint = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(0.4f)
                                },
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = stringResource(item.label),
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(0.4f)
                                }
                            )
                        }
                    )
                }
            }
        }
    }
}

data class BottomNavigationItem(
    val label: StringResource,
    val selectedIcon: DrawableResource,
    val unselectedIcon: DrawableResource
)
