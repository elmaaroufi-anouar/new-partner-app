package com.done.partner.presentation.permissions

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.ui.components.OnResumeCompose
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.partner.R
import com.done.partner.presentation.core.components.Banner
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreenCore(
    onGranted: () -> Unit
) {

    DoneScaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Banner()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PermissionsScreen(
                    onGranted = {
                        onGranted()
                    }
                )
            }
        }
    }
}

@Composable
expect fun PermissionsScreen(
    onGranted: () -> Unit
)

@Composable
fun Permission(
    onEnable: () -> Unit,
) {
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = stringResource(R.string.notification_permission),
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
    Text(
        textAlign = TextAlign.Center,
        text = stringResource(R.string.notifications_description)
    )

    Spacer(modifier = Modifier.height(20.dp))
    DoneOutlinedButton(
        onClick = { onEnable() },
        text = stringResource(R.string.enable_notifications),
        modifier = Modifier
    )
}


//@Preview
//@Composable
//private fun PermissionsScreenPreview() {
//    DoneTheme {
//        PermissionsScreen(
//            onGranted = {}
//        )
//    }
//}