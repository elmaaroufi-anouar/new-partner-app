package com.done.core.presentation.core.util

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

actual fun openFirebaseDistribution(context: Any, url: String) {
    val ctx = context as Context
    val intent = Intent(Intent.ACTION_VIEW, url.toUri())
    ctx.startActivity(intent)
}