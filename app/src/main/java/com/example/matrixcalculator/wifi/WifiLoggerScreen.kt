package com.example.matrixcalculator.wifi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Typeface
import android.net.wifi.WifiManager
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class WifiLoggerTab {
    COLLECT,
    COMPARE
}

@Composable
fun WifiLoggerScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val wifiDataManager = remember { WifiDataManager(context) }
    val loggingState = remember { mutableStateOf(false) }
    
    // Use predefined locations from WifiDataManager
    val locations = WifiDataManager.PREDEFINED_LOCATIONS
    var selectedLocation by remember { mutableStateOf(locations.firstOrNull() ?: "") }
    
    var currentSamples by remember { mutableStateOf<List<Int>>(emptyList()) }
    var permissionGranted by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var progressMessage by remember { mutableStateOf("") }
    var allLocationsData by remember { mutableStateOf<Map<String, List<Int>>>(emptyMap()) }
    
    var selectedTab by remember { mutableStateOf(WifiLoggerTab.COLLECT) }
    val scope = rememberCoroutineScope()
    
    val colorScheme = MaterialTheme.colorScheme
    val chartBarColor = colorScheme.primary

    // Launcher for location permission
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        permissionGranted = isGranted
        if (!isGranted) {
            errorMessage = "Location permission is required for Wi-Fi scanning"
        } else {
            errorMessage = ""
        }
    }

    // Check if permission is already granted
    LaunchedEffect(Unit) {
        permissionGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        if (!permissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        
        // Load existing data
        allLocationsData = wifiDataManager.getAllWifiData()
    }

    // Collect logs when logging is active - only get 100 samples as required
    LaunchedEffect(loggingState.value) {
        if (loggingState.value) {
            currentSamples = emptyList()
            var sampleCount = 0
            
            while (loggingState.value && sampleCount < WifiDataManager.SAMPLES_PER_LOCATION) {
                try {
                    val rssi = wifiDataManager.getCurrentWifiRssi()
                    if (rssi != WifiDataManager.RSSI_ERROR) {
                        currentSamples = currentSamples + rssi
                        sampleCount++
                        
                        // Update progress message
                        progressMessage = "Collecting samples: $sampleCount / ${WifiDataManager.SAMPLES_PER_LOCATION}"
                        
                        // If we've collected all samples, stop logging
                        if (sampleCount >= WifiDataManager.SAMPLES_PER_LOCATION) {
                            // Save the data
                            wifiDataManager.saveWifiData(selectedLocation, currentSamples)
                            
                            // Update all locations data
                            allLocationsData = wifiDataManager.getAllWifiData()
                            
                            // Stop logging
                            loggingState.value = false
                            progressMessage = "Collection complete! 100 samples collected."
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WifiLogger", "Error collecting Wi-Fi data", e)
                    errorMessage = "Error collecting Wi-Fi data: ${e.message}"
                }
                delay(100) // Sample every 100ms to quickly get to 100 samples
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Wi-Fi Signal Matrix Logger",
            style = TextStyle(
                color = colorScheme.onBackground,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Tab selection
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            TabButton(
                text = "Collect Data",
                selected = selectedTab == WifiLoggerTab.COLLECT,
                onClick = { selectedTab = WifiLoggerTab.COLLECT },
                modifier = Modifier.weight(1f),
                colorScheme = colorScheme
            )
            
            TabButton(
                text = "Compare Locations",
                selected = selectedTab == WifiLoggerTab.COMPARE,
                onClick = { selectedTab = WifiLoggerTab.COMPARE },
                modifier = Modifier.weight(1f),
                colorScheme = colorScheme
            )
        }

        when (selectedTab) {
            WifiLoggerTab.COLLECT -> {
                // Collection Tab
                CollectionTab(
                    colorScheme = colorScheme,
                    locations = locations,
                    selectedLocation = selectedLocation,
                    onLocationSelected = { selectedLocation = it },
                    isLogging = loggingState.value,
                    onStartLogging = {
                        if (!permissionGranted) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        } else if (selectedLocation.isBlank()) {
                            errorMessage = "Please select a location"
                        } else {
                            loggingState.value = true
                            errorMessage = ""
                        }
                    },
                    onStopLogging = { loggingState.value = false },
                    permissionGranted = permissionGranted,
                    errorMessage = errorMessage,
                    progressMessage = progressMessage,
                    currentSamples = currentSamples,
                    onClearData = {
                        scope.launch {
                            wifiDataManager.clearAllData()
                            allLocationsData = wifiDataManager.getAllWifiData()
                            currentSamples = emptyList()
                            progressMessage = "All data cleared"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )
            }
            
            WifiLoggerTab.COMPARE -> {
                // Comparison Tab
                ComparisonTab(
                    colorScheme = colorScheme,
                    allLocationsData = allLocationsData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                )
            }
        }

        ElevatedButton(
            onClick = onBackClick,
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = colorScheme.secondary,
                contentColor = colorScheme.onSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text("Back")
        }
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme
) {
    Button(
        onClick = onClick,
        modifier = modifier.padding(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) colorScheme.primary else colorScheme.surface,
            contentColor = if (selected) colorScheme.onPrimary else colorScheme.onSurface
        ),
        elevation = if (selected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) 
                  else ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}

@Composable
fun CollectionTab(
    colorScheme: ColorScheme,
    locations: List<String>,
    selectedLocation: String,
    onLocationSelected: (String) -> Unit,
    isLogging: Boolean,
    onStartLogging: () -> Unit,
    onStopLogging: () -> Unit,
    permissionGranted: Boolean,
    errorMessage: String,
    progressMessage: String,
    currentSamples: List<Int>,
    onClearData: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Location",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Location selection
                var isLocationDropdownExpanded by remember { mutableStateOf(false) }
                
                Box {
                    OutlinedButton(
                        onClick = { isLocationDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.primary
                        )
                    ) {
                        Text(selectedLocation.ifEmpty { "Select a location" })
                    }
                    
                    DropdownMenu(
                        expanded = isLocationDropdownExpanded,
                        onDismissRequest = { isLocationDropdownExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.9f)
                    ) {
                        locations.forEach { location ->
                            DropdownMenuItem(
                                text = { Text(location) },
                                onClick = {
                                    onLocationSelected(location)
                                    isLocationDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status text
                if (!permissionGranted) {
                    Text(
                        text = "Location permission is required for Wi-Fi scanning",
                        color = colorScheme.error,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else if (progressMessage.isNotEmpty()) {
                    Text(
                        text = progressMessage,
                        color = colorScheme.primary,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                // Start/Stop Button
                ElevatedButton(
                    onClick = {
                        if (isLogging) onStopLogging() else onStartLogging()
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (isLogging) colorScheme.error else colorScheme.primary,
                        contentColor = if (isLogging) colorScheme.onError else colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (isLogging) "Stop Collection" else "Start Collection")
                }
                
                // Clear data button
                OutlinedButton(
                    onClick = onClearData,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All Data")
                }
            }
        }

        // Error message card
        if (errorMessage.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.errorContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = colorScheme.onErrorContainer,
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Current samples preview
        if (currentSamples.isNotEmpty()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Current Samples (${currentSamples.size}/${WifiDataManager.SAMPLES_PER_LOCATION}):",
                        color = colorScheme.onSecondaryContainer,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    val matrix = currentSamples.chunked(10)
                    matrix.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row.forEach { value ->
                                Text(
                                    text = value.toString(),
                                    color = colorScheme.onSecondaryContainer,
                                    style = TextStyle(fontSize = 12.sp),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(24.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Stats
                    if (currentSamples.isNotEmpty()) {
                        val avgRssi = currentSamples.average()
                        val minRssi = currentSamples.minOrNull() ?: 0
                        val maxRssi = currentSamples.maxOrNull() ?: 0
                        
                        Text(
                            text = "Stats: Avg RSSI: ${String.format("%.2f", avgRssi)} dBm, " +
                                   "Min: $minRssi dBm, Max: $maxRssi dBm",
                            color = colorScheme.onSecondaryContainer,
                            style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComparisonTab(
    colorScheme: ColorScheme,
    allLocationsData: Map<String, List<Int>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "WiFi Signal Strength Comparison",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                // Check if we have data for all locations
                val hasAllData = allLocationsData.values.all { it.size == WifiDataManager.SAMPLES_PER_LOCATION }
                
                if (!hasAllData) {
                    Text(
                        text = "Please collect 100 samples for all locations to view comparison",
                        color = colorScheme.error,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    // Stats comparison
                    val statsData = allLocationsData.mapValues { (_, values) ->
                        Triple(
                            values.average(),
                            values.minOrNull() ?: 0,
                            values.maxOrNull() ?: 0
                        )
                    }
                    
                    // Comparison table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        // Table header
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Location",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Avg RSSI",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Min RSSI",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = "Max RSSI",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                                color = colorScheme.onSurface
                            )
                        }
                        
                        Divider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = colorScheme.outline
                        )
                        
                        // Table rows
                        statsData.forEach { (location, stats) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Text(
                                    text = location,
                                    modifier = Modifier.weight(2f),
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = String.format("%.2f", stats.first),
                                    modifier = Modifier.weight(1f),
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = stats.second.toString(),
                                    modifier = Modifier.weight(1f),
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = stats.third.toString(),
                                    modifier = Modifier.weight(1f),
                                    color = colorScheme.onSurface
                                )
                            }
                            
                            Divider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = colorScheme.outline.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
        
        // Bar chart for comparison
        if (allLocationsData.any { it.value.isNotEmpty() }) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(horizontal = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "RSSI Ranges by Location",
                        color = colorScheme.onSurfaceVariant,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Draw bar chart
                    val data = allLocationsData.filter { it.value.isNotEmpty() }
                    if (data.isNotEmpty()) {
                        val minRssi = data.values.flatMap { it }.minOrNull() ?: -100
                        val maxRssi = data.values.flatMap { it }.maxOrNull() ?: -30
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 40.dp, top = 20.dp, end = 20.dp, bottom = 40.dp)
                        ) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val barSpacing = 20f
                            val barWidth = (canvasWidth - barSpacing * (data.size - 1)) / data.size
                            
                            // Draw coordinate system
                            drawLine(
                                color = colorScheme.outline,
                                start = Offset(0f, 0f),
                                end = Offset(0f, canvasHeight),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = colorScheme.outline,
                                start = Offset(0f, canvasHeight),
                                end = Offset(canvasWidth, canvasHeight),
                                strokeWidth = 2f
                            )
                            
                            // Draw bars
                            var index = 0
                            for (entry in data.entries) {
                                val location = entry.key
                                val values = entry.value
                                
                                if (values.isEmpty()) continue
                                
                                val avg = values.average().toFloat()
                                val min = values.minOrNull()?.toFloat() ?: 0f
                                val max = values.maxOrNull()?.toFloat() ?: 0f
                                
                                // Normalize values to canvas height (higher is better, so invert)
                                val rssiRange = (maxRssi - minRssi).toFloat()
                                val avgHeight = ((maxRssi - avg) / rssiRange) * canvasHeight
                                val minHeight = ((maxRssi - min) / rssiRange) * canvasHeight
                                val maxHeight = ((maxRssi - max) / rssiRange) * canvasHeight
                                
                                val x = index * (barWidth + barSpacing)
                                
                                // Draw min-max range as transparent bar
                                drawRect(
                                    color = colorScheme.primary.copy(alpha = 0.3f),
                                    topLeft = Offset(x, maxHeight),
                                    size = Size(barWidth, minHeight - maxHeight)
                                )
                                
                                // Draw average as solid bar
                                drawLine(
                                    color = colorScheme.primary,
                                    start = Offset(x, avgHeight),
                                    end = Offset(x + barWidth, avgHeight),
                                    strokeWidth = 4f
                                )
                                
                                // Draw location label
                                rotate(90f) {
                                    drawContext.canvas.nativeCanvas.apply {
                                        drawText(
                                            location,
                                            x + barWidth/2 - 20f,
                                            canvasHeight + 30f,
                                            Paint().apply {
                                                color = colorScheme.onSurfaceVariant.toArgb()
                                                textSize = 30f
                                                textAlign = Paint.Align.CENTER
                                            }
                                        )
                                    }
                                }
                                
                                // Draw values
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        "Avg: ${avg.toInt()}",
                                        x + barWidth/2,
                                        avgHeight - 10f,
                                        Paint().apply {
                                            color = colorScheme.onSurfaceVariant.toArgb()
                                            textSize = 25f
                                            textAlign = Paint.Align.CENTER
                                        }
                                    )
                                }
                                
                                index++
                            }
                            
                            // Draw RSSI scale on y-axis
                            val steps = 5
                            val step = (maxRssi - minRssi) / steps
                            
                            for (i in 0..steps) {
                                val rssiValue = minRssi + i * step
                                val rssiRange = (maxRssi - minRssi).toFloat()
                                val y = ((maxRssi - rssiValue) / rssiRange) * canvasHeight
                                
                                drawLine(
                                    color = colorScheme.outline.copy(alpha = 0.5f),
                                    start = Offset(-5f, y),
                                    end = Offset(canvasWidth, y),
                                    strokeWidth = 1f
                                )
                                
                                drawContext.canvas.nativeCanvas.apply {
                                    drawText(
                                        rssiValue.toInt().toString(),
                                        -30f,
                                        y + 5f,
                                        Paint().apply {
                                            color = colorScheme.onSurfaceVariant.toArgb()
                                            textSize = 25f
                                            textAlign = Paint.Align.CENTER
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // Matrix View (10x10 grid for each location)
        if (allLocationsData.any { it.value.size == WifiDataManager.SAMPLES_PER_LOCATION }) {
            allLocationsData.filter { it.value.size == WifiDataManager.SAMPLES_PER_LOCATION }.forEach { (location, values) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "$location - Signal Strength Matrix (10×10)",
                            color = colorScheme.onSurface,
                            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        // Display matrix
                        val matrix = values.chunked(10)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, colorScheme.outline, RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        ) {
                            matrix.forEach { row ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    row.forEach { value ->
                                        Text(
                                            text = value.toString(),
                                            color = getRssiColor(value, colorScheme),
                                            style = TextStyle(fontSize = 10.sp),
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun formatTimestamp(timestamp: Long): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

@Composable
private fun getRssiColor(rssi: Int, colorScheme: ColorScheme): Color {
    return when {
        rssi >= -50 -> colorScheme.primary        // Excellent
        rssi >= -60 -> colorScheme.primary        // Good
        rssi >= -70 -> colorScheme.tertiary       // Fair
        rssi >= -80 -> colorScheme.error.copy(alpha = 0.7f)    // Poor
        else -> colorScheme.error                 // Very poor
    }
} 