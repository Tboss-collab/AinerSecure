package org.example.ainer.secure

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import org.example.ainer.secure.ui.PasswordAnalyzerScreen
import org.example.ainer.secure.ui.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            var showSplash by remember { mutableStateOf(true) }

            if (showSplash) {
                SplashScreen { showSplash = false }
            } else {
                PasswordAnalyzerScreen()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppAndroidPreview() {
   PasswordAnalyzerScreen()
}

//@Preview(showBackground = true)
//@Composable
//fun SplashScreenPreview() {
   // SplashScreen(onTimeout = {}) // Pass empty lambda
//}

//@Preview(showBackground = true)
//@Composable
//fun AppFlowPreview() {
   // var showSplash by remember { mutableStateOf(true) }

   // if (showSplash) {
      //  SplashScreen { showSplash = false }
   // } else {
    //    PasswordAnalyzerScreen()
  //  }
//}
