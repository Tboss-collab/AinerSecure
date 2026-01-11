package org.example.ainer.secure.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    val letters = listOf("A", "I", "N", "E", "R")
    var visibleLetters by remember { mutableStateOf("") }
    var currentIndex by remember { mutableStateOf(0) }

    // Glow animation for "Secure"
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate letters appearing
    LaunchedEffect(Unit) {
        while (currentIndex < letters.size) {
            visibleLetters += letters[currentIndex]
            currentIndex++
            delay(300) // delay between letters
        }
        delay(500) // small pause
        // wait 2s more for glowing "Secure"
        delay(2000)
        onTimeout() // go to main screen
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1F2933) // same as Option 2 background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BasicText(
                text = visibleLetters,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE1BEE7) // soft lavender
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            BasicText(
                text = "Secure",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE1BEE7).copy(alpha = glowAlpha)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            BasicText(
                text = "@Osaki",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 12.sp,
                    color = Color(0xFFB0BEC5) // muted small signature
                )
            )
        }
    }
}
