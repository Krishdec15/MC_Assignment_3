package com.example.matrixcalculator.wifi

/**
 * Data class representing a single WiFi signal measurement
 */
data class WifiDataPoint(
    val locationName: String,
    val timestamp: Long,
    val rssi: Int
) 