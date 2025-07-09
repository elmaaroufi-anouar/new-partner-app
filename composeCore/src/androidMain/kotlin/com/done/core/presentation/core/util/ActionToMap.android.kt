package com.done.core.presentation.core.util

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

actual fun Any.toMap(): Map<String, Any> {
    val kClass = this::class
    val typeName = kClass.simpleName.orEmpty()

    try {
        return if (kClass.isData) {
            @Suppress("UNCHECKED_CAST")
            val params =
                (kClass.memberProperties as Collection<KProperty1<Any, *>>).associate { prop ->
                    try {
                        if (!prop.isAccessible) {
                            prop.isAccessible = true
                        }
                        prop.name to (prop.get(this)?.toString() ?: "null")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        prop.name to "access-error"
                    }
                }

            mapOf(
                "action" to typeName,
                "params" to params
            )
        } else {
            mapOf("action" to typeName)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return  mapOf("action" to typeName)
    }
}