package org.example.ainer.secure

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform