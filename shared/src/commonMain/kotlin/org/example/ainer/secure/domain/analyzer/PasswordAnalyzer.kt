package org.example.ainer.secure.domain.analyzer

import org.example.ainer.secure.domain.model.PasswordAnalysisResult

object PasswordAnalyzer {

    private fun hasRepeatedCharacters(password: String): Boolean {
        return password.windowed(3, 1).any { it[0] == it[1] && it[1] == it[2] }
    }

    private val keyboardSequences = listOf(
        "qwerty", "asdf", "zxcv",
        "12345", "09876"
    )

    private fun containsKeyboardSequence(password: String): Boolean {
        val lower = password.lowercase()
        return keyboardSequences.any { lower.contains(it) }
    }


    fun analyze(password: String): PasswordAnalysisResult {
        val issues = mutableListOf<String>()
        val suggestions = mutableListOf<String>()
        var score = 0

        // 1 Length check
        if (password.length < 8) {
            issues.add("Password is too short")
            suggestions.add("Use at least 8 characters")
        } else {
            score += 20
        }

        // 2 Upper & lowercase
        if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) {
            score += 20
        } else {
            issues.add("Missing uppercase or lowercase letters")
            suggestions.add("Mix uppercase and lowercase letters")
        }

        // 3 Numbers
        if (password.any { it.isDigit() }) {
            score += 20
        } else {
            issues.add("No numbers included")
            suggestions.add("Add at least one number")
        }

        // 4 Special characters
        if (password.any { !it.isLetterOrDigit() }) {
            score += 20
        } else {
            issues.add("No special characters")
            suggestions.add("Add symbols like @, #, !")
        }

        // 5 Common weak patterns
        val weakPatterns = listOf("123", "password", "qwerty", "abc")
        if (weakPatterns.any { password.lowercase().contains(it) }) {
            issues.add("Contains common weak patterns")
            suggestions.add("Avoid common sequences like 123 or qwerty")
        } else {
            score += 20
        }

        // 6️ Repeated characters (e.g. aaa, 111)
        if (hasRepeatedCharacters(password)) {
            issues.add("Contains repeated characters")
            suggestions.add("Avoid repeating the same character multiple times")
        } else {
            score += 10
        }

        // 7️ Keyboard sequences
        if (containsKeyboardSequence(password)) {
            issues.add("Contains keyboard sequence patterns")
            suggestions.add("Avoid using keyboard sequences like qwerty or 12345")
        } else {
            score += 10
        }



        val strength = when {
            score >= 85 -> "Very Strong"
            score >= 65 -> "Strong"
            score >= 40 -> "Medium"
            else -> "Weak"
        }


        return PasswordAnalysisResult(
            score = score.coerceAtMost(100),
            strengthLabel = strength,
            issues = issues,
            suggestions = suggestions
        )


    }

}