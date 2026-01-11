package org.example.ainer.secure

import org.example.ainer.secure.domain.analyzer.PasswordAnalyzer
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedCommonTest {

    @Test
    fun example() {
        println(PasswordAnalyzer.analyze("aaa123"))
        println(PasswordAnalyzer.analyze("password"))
        println(PasswordAnalyzer.analyze("Ainer@Secure2025"))
        println(PasswordAnalyzer.analyze("qwerty123"))
    }


}