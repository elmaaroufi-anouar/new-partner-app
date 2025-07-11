package com.done.app.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.done.app.util.provideInternetConnectionHandler
import com.done.core.presentation.core.design_system.DoneButton
import com.done.partner.R
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NoInternetView() {

    val connectionHandler = provideInternetConnectionHandler()

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.image_error_internet),
            contentDescription = null,
            modifier = Modifier.width(150.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(com.done.core.R.string.make_sure_you_have_a_valid_internet_connection),
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        DoneButton(
            text = stringResource(R.string.activate_internet),
            verticalPadding = 8.dp,
            onClick = {
                connectionHandler.openConnectionSettings()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
