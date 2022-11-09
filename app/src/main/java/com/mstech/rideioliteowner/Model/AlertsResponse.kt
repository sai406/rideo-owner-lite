package com.mstech.rideiodriverlite.Model

data class AlertsResponse(
    val driverName: String,
    val address: String,
    val angle: String,
    val dataDay: String,
    val dataUnixTime: String,
    val deviceName: String,
    val event: String,
    val gpsOdometer: String,
    val ignitionStatus: String,
    val imei: String,
    val latitude: Double,
    val latitudeDirection: String,
    val longitude: Double,
    val longitudeDirection: String,
    val packetTypeId: String,
    val satellites: String,
    val sno: String,
    val speedKmph: String,
    val vehicleEvent: String,
    val vehicleNumber: String
)