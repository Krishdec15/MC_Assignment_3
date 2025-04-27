package com.example.matrixcalculator.wifi

import android.content.Context
import android.content.SharedPreferences
import android.net.wifi.WifiManager
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Utility class to manage WiFi data collection
 */
class WifiDataManager(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("wifi_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        const val RSSI_ERROR = -999
        private const val TAG = "WifiDataManager"
        
        // Predefined locations
        val PREDEFINED_LOCATIONS = listOf("Living Room", "Bedroom", "Kitchen")
        const val SAMPLES_PER_LOCATION = 100
    }
    
    /**
     * Gets the current WiFi RSSI (signal strength) value
     * @return RSSI value in dBm or RSSI_ERROR if unavailable
     */
    fun getCurrentWifiRssi(): Int {
        try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager == null) {
                Log.e(TAG, "WiFi manager not available")
                return RSSI_ERROR
            }
            
            if (!wifiManager.isWifiEnabled) {
                Log.e(TAG, "WiFi is not enabled")
                return RSSI_ERROR
            }
            
            val wifiInfo = wifiManager.connectionInfo
            if (wifiInfo == null) {
                Log.e(TAG, "WiFi info not available")
                return RSSI_ERROR
            }
            
            val rssi = wifiInfo.rssi
            Log.d(TAG, "Current RSSI: $rssi dBm")
            return rssi
        } catch (e: Exception) {
            Log.e(TAG, "Error getting WiFi RSSI", e)
            return RSSI_ERROR
        }
    }
    
    /**
     * Saves WiFi data for a specific location
     */
    fun saveWifiData(location: String, data: List<Int>) {
        val allData = getAllWifiData().toMutableMap()
        allData[location] = data
        val json = gson.toJson(allData)
        sharedPreferences.edit().putString("wifi_matrix_data", json).apply()
    }
    
    /**
     * Gets WiFi data for a specific location
     */
    fun getWifiData(location: String): List<Int> {
        return getAllWifiData()[location] ?: emptyList()
    }
    
    /**
     * Gets all stored WiFi data
     */
    fun getAllWifiData(): Map<String, List<Int>> {
        val json = sharedPreferences.getString("wifi_matrix_data", null)
        if (json == null) {
            // Initialize with empty lists for predefined locations
            val initialData = PREDEFINED_LOCATIONS.associateWith { emptyList<Int>() }
            return initialData
        }
        
        val type = object : TypeToken<Map<String, List<Int>>>() {}.type
        return gson.fromJson(json, type) ?: PREDEFINED_LOCATIONS.associateWith { emptyList<Int>() }
    }
    
    /**
     * Clears all stored WiFi data
     */
    fun clearAllData() {
        sharedPreferences.edit().remove("wifi_matrix_data").apply()
    }
} 