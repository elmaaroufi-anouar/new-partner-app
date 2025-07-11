package com.done.partner.presentation.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.done.core.presentation.core.design_system.DoneButton
import com.done.core.presentation.core.design_system.DonePasswordTextField
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.design_system.DoneTextField
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.components.OnResumeCompose
import com.done.core.presentation.core.ui.components.networkErrorToast
import com.done.partner.platform
import com.done.partner.presentation.core.components.Banner
import com.done.partner.presentation.core.components.UpdatingPlayServicesDialog
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreenCore(
    viewModel: LoginViewModel = koinViewModel(),
    onLoginSuccess: () -> Unit,
    onRestartApp: () -> Unit
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            LoginEvent.LoginSuccess -> {
                onLoginSuccess()
            }

            is LoginEvent.LoginError -> {
                keyboardController?.hide()
                networkErrorToast(
                    networkError = event.error,
                )
            }

            LoginEvent.RestartApp -> {
                onRestartApp()
            }
        }
    }

    OnResumeCompose {
        viewModel.onAction(LoginAction.OnLoad)
    }

    LoginScreen(
        state = state,
        onAction = viewModel::onAction
    )

    if (platform() == "Android" && state.isUpdatingPlayServices) {
        UpdatingPlayServicesDialog()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    DoneScaffold { paddingValues ->
        Column(
            modifier = padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Banner()

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LoginSection(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun LoginSection(
    state: LoginState,
    onAction: (LoginAction) -> Unit
) {
    Spacer(Modifier.height(16.dp))

    DoneTextField(
        textFieldState = state.email,
        startIcon = Icons.Outlined.Email,
        keyBoardType = KeyboardType.Email,
        hint = stringResource(Res.string.email),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    DonePasswordTextField(
        textFieldState = state.password,
        isPasswordVisible = state.isPasswordVisible,
        onTogglePasswordVisibility = {
            onAction(LoginAction.OnTogglePasswordVisibilityClick)
        },
        hint = stringResource(Res.string.password),
        modifier = Modifier.fillMaxWidth(),
        showPassword = stringResource(Res.string.show_password),
        hidePassword = stringResource(Res.string.hide_password),
    )

    Spacer(modifier = Modifier.height(22.dp))

    DoneButton(
        text = stringResource(Res.string.login),
        isLoading = state.isLoggingIn,
        enabled = state.canLogin,
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onAction(LoginAction.OnLoginClick)
        }
    )
}

//@Preview
//@Composable
//private fun LoginScreenPreview() {
//    DoneTheme {
//        LoginScreen(
//            state = LoginState(),
//            onAction = {}
//        )
//    }
//}