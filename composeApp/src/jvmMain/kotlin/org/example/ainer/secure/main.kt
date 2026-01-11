package org.example.ainer.secure

import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import org.example.ainer.secure.ui.SplashScreen
import org.example.ainer.secure.ui.PasswordAnalyzerScreen
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    var showSplash by remember { mutableStateOf(true) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "AINER Secure",
        icon = painterResource("icons/ainersecure.webp")
    ) {
        if (showSplash) {
            SplashScreen(onTimeout = { showSplash = false })
        } else {
            PasswordAnalyzerScreen()
        }
    }
}

