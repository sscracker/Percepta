package com.percepta.app

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform