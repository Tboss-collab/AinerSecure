package org.example.ainer.secure.ui


import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    var displayText by remember { mutableStateOf("") }
    val splashText = "AINER"

    // LETTER ANIMATION
    LaunchedEffect(Unit) {
        displayText = ""
        splashText.forEach { char ->
            displayText += char
            delay(250)
        }
        delay(800)
        onTimeout()
    }

    // 🌈 MOVING NEON ANIMATION (MUST BE BEFORE CANVAS)
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1F2933))
    ) {

        // 🌈 NEON EDGE GLOW (NOW ACTUALLY MOVES)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            val strokeWidth = 4.dp.toPx()
            val glowWidth = 18.dp.toPx()
            val radius = 48.dp.toPx()

            val radians = Math.toRadians(angle.toDouble())
            val dx = (Math.cos(radians) * size.width).toFloat()
            val dy = (Math.sin(radians) * size.height).toFloat()

            val animatedGradient = Brush.linearGradient(
                colors = listOf(
                    Color.Cyan,
                    Color.Magenta,
                    Color.Yellow,
                    Color.Red,
                    Color.Cyan
                ),
                start = Offset(size.width / 2f - dx, size.height / 2f - dy),
                end = Offset(size.width / 2f + dx, size.height / 2f + dy)
            )

            // OUTER GLOW
            drawRoundRect(
                brush = animatedGradient,
                size = size,
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = glowWidth),
                alpha = 0.35f
            )

            // SHARP EDGE
            drawRoundRect(
                brush = animatedGradient,
                size = size,
                cornerRadius = CornerRadius(radius, radius),
                style = Stroke(width = strokeWidth)
            )
        }

        // 🧠 CONTENT
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = displayText,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 48.sp,
                    color = Color(0xFFE1BEE7),
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Secure",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFC107),
                    shadow = Shadow(
                        color = Color(0xFFFFC107),
                        blurRadius = 20f,
                        offset = Offset.Zero
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "@Osaki",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
        }
    }
}
