package com.done.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform