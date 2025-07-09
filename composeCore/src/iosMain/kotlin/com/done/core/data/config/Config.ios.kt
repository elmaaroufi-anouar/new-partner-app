package com.done.core.data.config

import platform.Foundation.NSProcessInfo

actual object Platform {
    actual val isDebugBinary: Boolean
        get() = NSProcessInfo.processInfo.environment["DEBUG"] == "true"
}