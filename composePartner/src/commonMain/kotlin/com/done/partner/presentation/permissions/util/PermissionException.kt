package com.done.partner.presentation.permissions.util

open class PermissionException(message: String? = null) : Exception(message)

class PermissionDeniedException(message: String? = null) : PermissionException(message)
class PermissionDeniedAlwaysException(message: String? = null) : PermissionException(message)
class PermissionRequestCanceledException(message: String? = null) : PermissionException(message)
