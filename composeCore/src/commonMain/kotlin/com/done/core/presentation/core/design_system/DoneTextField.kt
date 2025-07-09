package com.done.core.presentation.core.design_system

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.done.core.presentation.core.ui.theme.DoneTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DoneTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
    startIcon: ImageVector? = null,
    startIconTint: Color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
    endIcon: ImageVector? = null,
    endIconContent: (@Composable () -> Unit)? = null,
    endIconTint: Color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
    hint: String? = null,
    title: String? = null,
    error: String? = null,
    applyTextWeight: Boolean = true,
    height: Dp = 60.dp,
    style: TextStyle = LocalTextStyle.current,
    focus: Boolean = false,
    centerText: Boolean = false,
    onFocusChanged: (isFocused: Boolean) -> Unit = {},
    keyBoardType: KeyboardType = KeyboardType.Text,
    additionalInfo: String? = null
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (focus) {
            focusRequester.requestFocus()
        } else {
            focusRequester.freeFocus()
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (title != null) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium
                )
            } else if (additionalInfo != null) {
                Text(
                    text = additionalInfo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .border(
                    width = 0.5.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {

            BasicTextField(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusEvent { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
                state = textFieldState,
                textStyle = style.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = if (centerText) TextAlign.Center else TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = keyBoardType
                ),
                lineLimits = TextFieldLineLimits.SingleLine,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
                decorator = { innerBox ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        if (startIcon != null) {
                            Icon(
                                imageVector = startIcon,
                                contentDescription = null,
                                tint = startIconTint,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        if (!centerText) {
                            Spacer(modifier = Modifier.width(12.dp))
                        }

                        Box(
                            modifier = if (applyTextWeight) Modifier.weight(1f) else Modifier,
                            contentAlignment = if (centerText) Alignment.Center else Alignment.CenterStart
                        ) {
                            if (textFieldState.text.isEmpty() && hint != null) {
                                Text(
                                    text = hint,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                                    style = style,
                                )
                            }
                            innerBox()
                        }

                        if (endIcon != null) {
                            Icon(
                                imageVector = endIcon,
                                contentDescription = null,
                                tint = endIconTint,
                            )
                        } else if (endIconContent != null) {
                            Box(modifier = Modifier.padding(end = 4.dp)) {
                                endIconContent()
                            }
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
private fun TextFieldPreview() {
    DoneTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            DoneTextField(
                textFieldState = TextFieldState(""),
                hint = "Search in products",
                style = MaterialTheme.typography.bodyLarge,
                height = 55.dp,
                startIcon = Icons.Rounded.Search,
                startIconTint = MaterialTheme.colorScheme.onBackground.copy(0.3f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}













