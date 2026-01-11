package org.example.ainer.secure.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.example.ainer.secure.domain.analyzer.PasswordAnalyzer
import org.example.ainer.secure.domain.model.PasswordAnalysisResult
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordAnalyzerScreen() {

    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<PasswordAnalysisResult?>(null) }
    var suggestedPasswords by remember { mutableStateOf<List<String>>(emptyList()) }
    var isDarkTheme by remember { mutableStateOf(true) }
    var passwordHistory by remember { mutableStateOf<List<String>>(emptyList()) }

    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    fun strengthColor(strength: String): Color = when (strength) {
        "Weak" -> Color(0xFFEF5350)
        "Medium" -> Color(0xFFFFA726)
        "Strong" -> Color(0xFF66BB6A)
        "Very Strong" -> Color(0xFF26A69A)
        else -> Color.Gray
    }

    fun generateStrongerPassword(base: String, length: Int = 12): String {
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*()-_=+[]{};:,.<>?/"

        val pw = base.toMutableList()
        if (!pw.any { it.isUpperCase() }) pw.add(upper.random())
        if (!pw.any { it.isLowerCase() }) pw.add(lower.random())
        if (!pw.any { it.isDigit() }) pw.add(digits.random())
        if (!pw.any { symbols.contains(it) }) pw.add(symbols.random())

        val all = upper + lower + digits + symbols
        while (pw.size < length) pw.add(all.random())

        return pw.shuffled().joinToString("")
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("AINER Secure") }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(WindowInsets.systemBars.asPaddingValues())
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // Dark/Light Theme Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (isDarkTheme) "🌙 Dark" else "☀️ Light",
                        modifier = Modifier
                            .clickable { isDarkTheme = !isDarkTheme }
                            .padding(8.dp)
                    )
                }

                // Password input
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        suggestedPasswords = emptyList()
                    },
                    label = { Text("Enter password") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = false
                    )
                )

                // Analyze button
                Button(
                    onClick = {
                        result = PasswordAnalyzer.analyze(password)
                        passwordHistory =
                            (listOf(password) + passwordHistory).distinct().take(3)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Analyze")
                }

                // Password history
                if (passwordHistory.isNotEmpty()) {
                    Text("Recent Passwords:", style = MaterialTheme.typography.titleSmall)
                    passwordHistory.forEach {
                        Text("• $it", style = MaterialTheme.typography.bodySmall)
                    }
                }

                // Strength display
                result?.let { analysis ->

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        color = strengthColor(analysis.strengthLabel),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "Strength: ${analysis.strengthLabel}",
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 16.sp
                        )
                    }

                    // Suggest stronger button
                    if (analysis.strengthLabel != "Very Strong") {
                        Button(
                            onClick = {
                                suggestedPasswords = List(2) { generateStrongerPassword(password) }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Suggest Stronger Password")
                        }

                        // Display suggested passwords as cards
                        suggestedPasswords.forEach { pw ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isDarkTheme) Color(0xFF2E3C43) else Color(0xFFE0E0E0),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• $pw")
                                    TextButton(onClick = {
                                        clipboardManager.setText(AnnotatedString(pw))
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                message = "Password copied",
                                                duration = SnackbarDuration.Short,
                                                withDismissAction = true
                                            )
                                        }
                                    }) {
                                        Text("Copy")
                                    }
                                }
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    // Issues
                    if (analysis.issues.isNotEmpty()) {
                        Text("Issues:", style = MaterialTheme.typography.titleSmall)
                        analysis.issues.forEach {
                            Text("• $it")
                        }
                    }

                    // Suggestions
                    if (analysis.suggestions.isNotEmpty()) {
                        Text("Suggestions:", style = MaterialTheme.typography.titleSmall)
                        analysis.suggestions.forEach {
                            Text("• $it")
                        }
                    }
                }
            }
        }
    }
}
