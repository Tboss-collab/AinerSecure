package org.example.ainer.secure.domain.model

data class PasswordAnalysisResult(
    val score: Int,            // 0-100
    val strengthLabel: String, //Weak, Medium, Strong
    val issues: List<String>,  //What's wrong
    val suggestions: List<String>   // How to improve
)