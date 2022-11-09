package com.mstech.rideioliteowner.Model

data class GeofenceListResponse(
    val createdDay: String,
    val driverId: Int,
    val email: String,
    val id: Int,
    val latitude: Double,
    val location: String,
    val longitude: Double,
    val mobile: String,
    val geofenceName: String,
    val name: String,
    val radius: Double,
    val radiusinKm: String,
    val totalRecords: Int
)
