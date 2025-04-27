package com.example.matrixcalculator

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.example.matrixcalculator.matrix.MatrixCalculatorScreen
import com.example.matrixcalculator.ui.theme.MatrixCalculatorTheme
import com.example.matrixcalculator.wifi.WifiLoggerScreen

class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Log.d("Permission", "Location permission denied")
        }
    }

    init {
        System.loadLibrary("matrixcalculator")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = resources.getColor(R.color.purple_500, theme)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            MatrixCalculatorTheme {
                MatrixCalculatorApp()
            }
        }
    }

    @Composable
    fun MatrixCalculatorApp() {
        var screen by remember { mutableStateOf("Main") }
        val textColor = if (isSystemInDarkTheme()) Color.White else Color.Black

        Box(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .background(if (isSystemInDarkTheme()) Color.Black else Color.White)
        ) {
            when (screen) {
                "Main" -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Matrix Calculator",
                            style = TextStyle(
                                color = textColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                        
                        Button(onClick = { screen = "Matrix" }) {
                            Text("Matrix Calculator")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { screen = "Wifi" }) {
                            Text("WiFi Logger")
                        }
                    }
                }
                "Matrix" -> {
                    MatrixCalculatorScreen(
                        onBackClick = { screen = "Main" }
                    )
                }
                "Wifi" -> {
                    WifiLoggerScreen(
                        onBackClick = { screen = "Main" }
                    )
                }
            }
        }
    }
}