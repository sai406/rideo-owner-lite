package com.mstech.rideiodriver.Model

data class GetTripsResponse(
    val avgSpeed: Double,
    val avgSpeedInKmph: String,
    val companionIds: String,
    val companions: String,
    val distanceInKm: String,
    val distanceTravel: Double,
    val driveTime: String,
    val driverEmail: String,
    val driverFirstName: String,
    val driverId: String,
    val driverLastName: String,
    val driverName: String,
    val driverPhone: String,
    val endAddress: String,
    val endDay: String,
    val endLatitude: Double,
    val endLatitudeDirection: String,
    val endLongitude: Double,
    val endLongitudeDirection: String,
    val endUnixTime: String,
    val idleTime: String,
    val imei: String,
    val maxSpeed: Double,
    val maxSpeedInKmph: String,
    val ownerId: String,
    val startAddress: String,
    val startDay: String,
    val startLatitude: Double,
    val startLatitudeDirection: String,
    val startLongitude: Double,
    val startLongitudeDirection: String,
    val startUnixTime: String,
    val stopTime: String,
    val tripHistory: Any,
    val tripId: String,
    val tripInfo: String,
    val vehicleNumber: String
)