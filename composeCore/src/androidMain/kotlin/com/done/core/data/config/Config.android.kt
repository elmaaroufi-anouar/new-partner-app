package com.done.core.data.config

actual object Platform {
    actual val isDebugBinary: Boolean
        get() = BuildConfig.IS_DEBUG
}
