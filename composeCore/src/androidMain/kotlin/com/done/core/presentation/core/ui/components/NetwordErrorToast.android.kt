package com.done.core.presentation.core.ui.components

import android.content.Context
import android.widget.Toast
import com.done.core.domain.util.result.NetworkError
import com.done.core.domain.util.result.NetworkErrorName

actual fun networkErrorToast(
    context: Any,
    networkError: NetworkError?,
    generalError: String,
    internetError: String
) {
    val ctx = context as Context
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
    Toast.makeText(ctx, error, Toast.LENGTH_SHORT).show()
}