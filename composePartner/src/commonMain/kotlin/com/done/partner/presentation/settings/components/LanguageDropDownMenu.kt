package com.done.partner.presentation.settings.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.done.core.domain.models.language.Language
import com.done.partner.R
import com.done.partner.presentation.settings.SettingsActions
import com.done.partner.presentation.settings.SettingsState


@Composable
fun LanguageDropDownMenu(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onAction: (SettingsActions) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
    ) {

        LanguageItem(
            language = state.currentLanguage,
            onExpand = { expanded = true },
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = MaterialTheme.colorScheme.onPrimary
        ) {
            state.languages.forEach { language ->
                if (language != state.currentLanguage) {
                    LanguageItem(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .padding(horizontal = 10.dp),
                        language = language,
                        showArrow = false,
                        onExpand = {
                            expanded = !expanded
                            if (language.code != state.currentLanguage?.code) {
                                onAction(SettingsActions.OnChangeLanguage(language.code))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageItem(
    modifier: Modifier = Modifier,
    language: Language,
    showArrow: Boolean = true,
    onExpand: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onExpand() }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        Image(
            painter = painterResource(language?.image ?: R.drawable.frensh),
            contentDescription = null,
            modifier = Modifier
                .size(27.dp)
                .clip(RoundedCornerShape(50.dp))
        )

        if (showArrow) {
            Spacer(Modifier.width(10.dp))
        } else {
            Spacer(Modifier.width(16.dp))
        }

        Text(
            text = stringResource(language?.nameResource ?: R.string.french),
            fontWeight = FontWeight.Medium
        )

        if (showArrow) {
            Spacer(Modifier.width(2.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null
            )
        }

    }
}
