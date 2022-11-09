package com.mstech.rideioliteowner.Model

data class DriverListResponse(
    val address: String,
    val countryId: Int,
    val createdDay: String,
    val dob: String,
    val driverId: Int,
    val driverName: String,
    val drivingLicence: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val genderText: String,
    val imei: String,
    val isActive: Int,
    val isActiveText: String,
    val lastName: String,
    val location: String,
    val mobile: String,
    val modifiedDay: String,
    val ownerId: Int,
    val password: String,
    val photoDrivingLicence: Any,
    val totalRecords: Int
)