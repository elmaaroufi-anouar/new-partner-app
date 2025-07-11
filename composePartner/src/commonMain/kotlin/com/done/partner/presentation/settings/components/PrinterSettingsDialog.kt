package com.done.partner.presentation.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.core.domain.models.language.LanguageCodes
import com.done.core.presentation.core.design_system.DoneOutlinedButton
import com.done.core.presentation.core.ui.theme.doneGreen
import com.done.partner.domain.util.PrinterType
import com.done.partner.presentation.settings.SettingsActions
import com.done.partner.presentation.settings.SettingsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@Composable
fun PrinterSettingsDialog(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onAction: (SettingsActions) -> Unit,
    picture: ByteArray,
    onToggleCapturing: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(Res.string.printer),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier
                    .padding(top = 12.dp)
            )

            IconButton(
                onClick = {
                    onDismiss()
                },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = doneGreen.copy(0.1f)
                )
            ) {
                Icon(
                    imageVector = Icons.Rounded.Check,
                    contentDescription = null,
                    tint = doneGreen,
                    modifier = Modifier
                        .size(28.dp)
                )
            }
        }

        Spacer(Modifier.size(8.dp))

        Text(
            text = stringResource(Res.string.type),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
        )
        Spacer(Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            PrinterSettingRadio(
                selected = state.printerType == PrinterType.SUNMI,
                text = stringResource(Res.string.sunmi),
                onSelect = {
                    onAction(SettingsActions.OnSelectPrinterType(PrinterType.SUNMI))
                }
            )
            Spacer(Modifier.size(8.dp))
            PrinterSettingRadio(
                selected = state.printerType == PrinterType.LANDI,
                text = stringResource(Res.string.landi_m20),
                onSelect = {
                    onAction(SettingsActions.OnSelectPrinterType(PrinterType.LANDI))
                }
            )
            Spacer(Modifier.size(8.dp))
            PrinterSettingRadio(
                selected = state.printerType == PrinterType.ALPS,
                text = stringResource(Res.string.alps_q6),
                onSelect = {
                    onAction(SettingsActions.OnSelectPrinterType(PrinterType.ALPS))
                }
            )
        }

        Spacer(Modifier.size(22.dp))

        Text(
            text = stringResource(Res.string.language),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Medium
            ),
            modifier = Modifier.padding(end = 16.dp)
        )

        Spacer(Modifier.size(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            PrinterSettingRadio(
                selected = state.printLangCode == LanguageCodes.FR,
                text = stringResource(Res.string.french),
                onSelect = {
                    onAction(SettingsActions.OnChangePrintLang(LanguageCodes.FR))
                }
            )
            Spacer(Modifier.size(8.dp))
            PrinterSettingRadio(
                selected = state.printLangCode == LanguageCodes.EN,
                text = stringResource(Res.string.english),
                onSelect = {
                    onAction(SettingsActions.OnChangePrintLang(LanguageCodes.EN))
                }
            )
            Spacer(Modifier.size(8.dp))
            PrinterSettingRadio(
                selected = state.printLangCode == LanguageCodes.AR,
                text = stringResource(Res.string.arabic),
                onSelect = {
                    onAction(SettingsActions.OnChangePrintLang(LanguageCodes.AR))
                }
            )
        }

        Spacer(Modifier.size(24.dp))

        DoneOutlinedButton(
            onClick = {
                scope.launch {
                    onToggleCapturing(true)
                    try {
                        delay(500)
                        onAction(
                            SettingsActions.OnTestPrinter(picture)
                        )
                    } catch (e: Exception) {
                        try {
                            delay(500)
                            onAction(
                                SettingsActions.OnTestPrinter(picture)
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    onToggleCapturing(false)
                }
            },
            verticalPadding = 4.dp,
            modifier = Modifier.padding(end = 16.dp),
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Print,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(Res.string.test_printer),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }
        )
    }
}

@Composable
fun PrinterSettingRadio(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    onSelect: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.clickable { onSelect() }
    ) {
        RadioButton(
            selected = selected,
            onClick = { onSelect() },
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}