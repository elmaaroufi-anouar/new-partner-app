package com.done.partner.presentation.settings

import android.graphics.Picture
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.done.core.domain.models.language.Language
import com.done.core.domain.models.language.LanguageCodes
import com.done.core.presentation.core.design_system.DoneAlertDialog
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.design_system.DoneScaffold
import com.done.core.presentation.core.design_system.DoneTopBar
import com.done.core.presentation.core.ui.components.ObserveAsEvent
import com.done.core.presentation.core.ui.theme.DoneTheme
import com.done.partner.BuildConfig
import com.done.partner.R
import com.done.partner.domain.models.orders.previewOrders
import com.done.partner.domain.util.Printer
import com.done.partner.domain.util.PrinterType
import com.done.partner.presentation.core.components.ScreenShootTicket
import com.done.partner.presentation.settings.components.LanguageDropDownMenu
import com.done.partner.presentation.settings.components.PrinterSettingsDialog
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenCore(
    viewModel: SettingsViewModel = koinViewModel(),
    onRestartApp: () -> Unit,
) {
    ObserveAsEvent(viewModel.event) { event ->
        when (event) {
            SettingsEvent.LanguageChanged -> onRestartApp()
            SettingsEvent.LoggedOut -> onRestartApp()
        }
    }

    SettingsScreen(
        state = viewModel.state,
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    state: SettingsState,
    onAction: (SettingsActions) -> Unit
) {

    var isDialogShowing by remember { mutableStateOf(false) }
    var isLogoutDialog: Boolean? by remember { mutableStateOf(null) }

    DoneScaffold(
        topBar = {
            DoneTopBar(
                titleText = stringResource(R.string.settings),
//                actionIconContent = {
//                    Row(
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(10.dp))
//                            .clickable {
//                                onAction(SettingsActions.OnHelpClick)
//                            }
//                            .background(doneLighterOrange)
//                            .padding(8.dp)
//                            .padding(horizontal = 4.dp),
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
//                            contentDescription = null,
//                            modifier = Modifier.size(20.dp)
//                        )
//
//                        Spacer(Modifier.width(8.dp))
//
//                        Text(
//                            text = stringResource(R.string.help),
//                            style = MaterialTheme.typography.bodyMedium,
//                        )
//                    }
//                }
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SettingsSection(
                state = state,
                onAction = { action ->
                    when (action) {
                        SettingsActions.OnToggleReceiveNotifications -> {
                            if (!state.receiveNotifications) {
                                onAction(SettingsActions.OnToggleReceiveNotifications)
                            } else {
                                isLogoutDialog = false
                                isDialogShowing = true
                            }
                        }

                        else -> onAction(action)
                    }
                }
            )

            DoneOutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    isLogoutDialog = true
                    isDialogShowing = true
                },
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.logout),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.log_out),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }

    if (isDialogShowing) {
        if (isLogoutDialog == true) {
            DoneAlertDialog(
                title = stringResource(R.string.log_out),
                description = stringResource(R.string.are_you_sure_you_want_to_log_out),
                primaryButtonText = stringResource(R.string.no),
                secondaryButtonText = stringResource(R.string.yes),
                onDismiss = {
                    isLogoutDialog = null
                    isDialogShowing = false
                },
                onConfirm = {
                    isLogoutDialog = null
                    isDialogShowing = false
                    onAction(SettingsActions.OnLogout)
                }
            )
        } else if (isLogoutDialog == false) {
            DoneAlertDialog(
                title = stringResource(R.string.disable_notifications),
                description = stringResource(R.string.are_you_sure_you_want_to_disable_notifications),
                primaryButtonText = stringResource(R.string.no),
                secondaryButtonText = stringResource(R.string.yes),
                onDismiss = {
                    isLogoutDialog = null
                    isDialogShowing = false
                },
                onConfirm = {
                    isLogoutDialog = null
                    isDialogShowing = false
                    onAction(SettingsActions.OnToggleReceiveNotifications)
                }
            )
        }
    }
}


@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onAction: (SettingsActions) -> Unit
) {
    val picture = remember { Picture() }
    var isCapturing by remember { mutableStateOf(false) }
    var isPrinterSettingsDialogShown by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        ElevatedCard {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.onPrimary)
                    .padding(16.dp)
            ) {

                ProfileSettingsItem(
                    title = stringResource(R.string.language),
                    icon = Icons.Outlined.Translate,
                    description = stringResource(R.string.language_description),
                ) {
                    LanguageDropDownMenu(
                        state = state,
                        onAction = onAction
                    )
                }

                Spacer(Modifier.size(16.dp))

                HorizontalDivider(Modifier.alpha(0.4f))

                Spacer(Modifier.size(16.dp))
                ProfileSettingsItem(
                    title = stringResource(R.string.printer),
                    icon = Icons.Outlined.Print,
                    description = when (Printer.CURRENT_PRINTER_TYPE) {
                        PrinterType.SUNMI -> stringResource(R.string.sunmi)
                        PrinterType.ALPS -> stringResource(R.string.alps_q6)
                        PrinterType.LANDI -> stringResource(R.string.landi_m20)
                        null -> stringResource(R.string.unkown)
                    },
                    onClick = {
                        isPrinterSettingsDialogShown = true
                    }
                ) {
                    IconButton(
                        onClick = {
                            isPrinterSettingsDialogShown = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (isCapturing) {
            Spacer(Modifier.height(500.dp))
            ScreenShootTicket(
                order = previewOrders[0],
                printLang = state.printLangCode,
                storeName = "Done Store",
                picture = picture,
                printTwo = false
            )
        }
    }

    if (isPrinterSettingsDialogShown) {
        Dialog(
            onDismissRequest = { isPrinterSettingsDialogShown = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            PrinterSettingsDialog(
                state = state,
                onAction = onAction,
                picture = picture,
                onToggleCapturing = { isCapturing = it },
                onDismiss = { isPrinterSettingsDialogShown = false },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
fun ProfileSettingsItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit = {},
    actionContent: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = description
            )
        }

        Spacer(Modifier.width(16.dp))

        actionContent()
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    DoneTheme {
        SettingsScreen(
            state = SettingsState(
                currentLanguage = Language(
                    nameResource = R.string.french,
                    code = LanguageCodes.FR,
                    image = R.drawable.frensh
                ),
                languages = listOf(
                    Language(
                        nameResource = R.string.english,
                        code = LanguageCodes.EN,
                        image = R.drawable.english
                    ),
                    Language(
                        nameResource = R.string.arabic,
                        code = LanguageCodes.AR,
                        image = R.drawable.arabic
                    ),
                    Language(
                        nameResource = R.string.french,
                        code = LanguageCodes.FR,
                        image = R.drawable.frensh
                    )
                )
            ),
            onAction = {}
        )
    }
}