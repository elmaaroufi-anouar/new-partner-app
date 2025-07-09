package com.done.partner.presentation.permissions

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.partner.presentation.core.components.Banner
import com.done.partner.presentation.permissions.components.BatteryDialog
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import io.ktor.http.parameters
import io.ktor.http.parametersOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionsScreenCore(
    onGranted: () -> Unit
) {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }

    BindEffect(controller)

    val viewModel = koinViewModel<PermissionsViewModel>(parameters = { parametersOf(controller) })

//    DoneScaffold { paddingValues ->
//        Column(
//            modifier = Modifier
//                .padding(paddingValues)
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.primary),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//
//            Banner()
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
//                    .background(MaterialTheme.colorScheme.background)
//                    .padding(horizontal = 16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                PermissionsScreen(
//                    permissionState = viewModel.state.value,
//                    permissionsController = controller,
//                    onGranted = {
//                        onGranted()
//                    }
//                )
//            }
//        }
//    }

    PermissionsScreen(
        viewModel = viewModel,
        permissionsController = controller,
        onGranted = {
            onGranted()
        }
    )
}

@Composable
fun PermissionsScreen(
    viewModel: PermissionsViewModel,
    permissionsController: PermissionsController,
    onGranted: () -> Unit
) {

    when (viewModel.state.value) {
        PermissionState.Granted -> {
            onGranted()
        }
        PermissionState.DeniedAlways -> {
            Permission(
                onEnable = {
                    permissionsController.openAppSettings()
                }
            )
        }

        else -> viewModel.provideOrRequestRemoteNotificationPermission()
    }
}

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
//            viewModel = PermissionsViewModel(
//                permissionsController = PermissionsController()
//            ),
//            permissionsController = PermissionsController(),
//            onGranted = {}
//        )
//    }
//}