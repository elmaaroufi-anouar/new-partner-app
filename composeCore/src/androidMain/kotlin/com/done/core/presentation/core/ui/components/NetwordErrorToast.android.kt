package com.done.core.presentation.core.ui.components

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName
import new_partner_app.composecore.generated.resources.Res
import new_partner_app.composecore.generated.resources.make_sure_you_have_a_valid_internet_connection
import new_partner_app.composecore.generated.resources.something_went_wrong
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun networkErrorToast(
    networkError: NetworkError?,
) {
    val context = LocalContext.current
    val generalError = stringResource(Res.string.something_went_wrong)
    val internetError = stringResource(Res.string.make_sure_you_have_a_valid_internet_connection)
    val error = when (networkError?.name) {
        NetworkErrorName.CLIENT_ERROR, NetworkErrorName.REDIRECTION_ERROR, NetworkErrorName.SERVER_ERROR -> {
            networkError.message ?: generalError
        }
        NetworkErrorName.SERIALIZATION_ERROR, NetworkErrorName.UNKNOWN, null -> {
            generalError
        }
        NetworkErrorName.NO_INTERNET_ERROR -> {
            internetError
        }
    }
    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
}