package org.example.ainer.secure.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.example.ainer.secure.domain.analyzer.PasswordAnalyzer
import org.example.ainer.secure.domain.model.PasswordAnalysisResult
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import kotlinx.coroutines.launch


// Helper function: strength label color
private fun strengthColor(label: String): Color =
    when (label) {
        "Weak" -> Color(0xFFEF5350)        // soft red
        "Medium" -> Color(0xFFFFA726)      // amber
        "Strong" -> Color(0xFF66BB6A)      // green
        "Very Strong" -> Color(0xFF26A69A) // emerald
        else -> Color.Gray
    }

@Composable
fun PasswordAnalyzerScreen() {
    var password by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<PasswordAnalysisResult?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var suggestedPasswords by remember { mutableStateOf<List<String>>(emptyList()) }
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    // Snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

// Theme toggle
    var isDarkTheme by remember { mutableStateOf(true) }

// Password history (last 3)
    var passwordHistory by remember { mutableStateOf<List<String>>(emptyList()) }


    // Generic strong password generator (if needed)
    fun generateStrongPassword(length: Int = 12): String {
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*()-_=+[]{};:,.<>?/"
        val allChars = upper + lower + digits + symbols
        return (1..length)
            .map { allChars.random() }
            .joinToString("")
    }

    // Smart stronger password generator based on current password
    fun generateStrongerPassword(base: String, length: Int = 12): String {
        val upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        val lower = "abcdefghijklmnopqrstuvwxyz"
        val digits = "0123456789"
        val symbols = "!@#\$%^&*()-_=+[]{};:,.<>?/"

        val newPw = base.toMutableList()

        // Ensure at least one uppercase
        if (!newPw.any { it.isUpperCase() }) newPw.add(upper.random())
        // Ensure at least one lowercase
        if (!newPw.any { it.isLowerCase() }) newPw.add(lower.random())
        // Ensure at least one digit
        if (!newPw.any { it.isDigit() }) newPw.add(digits.random())
        // Ensure at least one symbol
        if (!newPw.any { symbols.contains(it) }) newPw.add(symbols.random())

        // Fill remaining length randomly
        val allChars = upper + lower + digits + symbols
        while (newPw.size < length) {
            newPw.add(allChars.random())
        }

        // Shuffle to mix added characters
        return newPw.shuffled().joinToString("")
    }

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF1F2933) // Option 2 background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }


            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState) // enables scrolling
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = if (isDarkTheme) "🌙 Dark" else "☀️ Light",
                        color = Color(0xFFB0BEC5),
                        modifier = Modifier
                            .clickable { isDarkTheme = !isDarkTheme }
                            .padding(8.dp)
                    ) }


                    // Title
                    Text(
                        text = "AINER Secure",
                        color = Color(0xFFE1BEE7), // soft lavender
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Password Strength Analyzer",
                        color = Color(0xFFB0BEC5), // muted silver
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            suggestedPasswords = emptyList() // reset previous suggestions
                        },
                        label = { Text("Enter password", color = Color(0xFFB0BEC5)) },
                        textStyle = TextStyle(color = Color(0xFFECEFF1)),
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            TextButton(onClick = { passwordVisible = !passwordVisible }) {
                                Text(if (passwordVisible) "🙈" else "👁️")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF64B5F6),
                            unfocusedBorderColor = Color(0xFF546E7A),
                            cursorColor = Color(0xFF64B5F6)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Analyze Button
                    Button(
                        onClick = {
                            result = PasswordAnalyzer.analyze(password)
                            passwordHistory =
                                (listOf(password) + passwordHistory)
                                    .distinct()
                                    .take(3)

                        }
                    ) {
                        Text("Analyze")
                    }

                    if (passwordHistory.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Recent Passwords:",
                            color = Color(0xFF90CAF9),
                            style = MaterialTheme.typography.titleSmall
                        )
                        passwordHistory.forEach {
                            Text(
                                text = "• $it",
                                color = Color(0xFFB0BEC5),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }


                    // Suggest Password Button (appears for Weak, Medium, Strong)
                    if (result != null && result!!.strengthLabel != "Very Strong") {
                        Button(
                            onClick = {
                                suggestedPasswords = List(2) { generateStrongerPassword(password) }
                            }
                        ) {
                            Text("Suggest Password")
                        }
                    }

                    // Display Suggested Passwords
                    suggestedPasswords.forEach { pw ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "• $pw",
                                color = Color(0xFFB0BEC5),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextButton(onClick = {
                                clipboardManager.setText(AnnotatedString(pw))
                                scope.launch {
                                    snackbarHostState.showSnackbar("Password copied to clipboard")
                                }
                            }) {
                                Text("Copy")


                            }
                        }
                    }


                    // Analysis Results
                    result?.let { analysis ->
                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // Strength Badge
                        Surface(
                            color = strengthColor(analysis.strengthLabel),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = "Strength : ${analysis.strengthLabel}",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                color = Color.Black,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }

                        // Issues
                        if (analysis.issues.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Issues:",
                                color = Color(0xFFFF8A80),
                                style = MaterialTheme.typography.titleSmall
                            )
                            analysis.issues.forEach {
                                Text(
                                    "• $it",
                                    color = Color(0xFFFFCDD2)
                                )
                            }
                        }

                        // Suggestions
                        if (analysis.suggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Suggestions:",
                                color = Color(0xFF81C784),
                                style = MaterialTheme.typography.titleSmall
                            )
                            analysis.suggestions.forEach {
                                Text(
                                    "• $it",
                                    color = Color(0xFFC8E6C9)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

